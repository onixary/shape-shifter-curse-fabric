#include <iostream>
#include "dataStruct.h"
#include <map>
#include <Windows.h>
#include <format>
#include <filesystem>
#define STB_IMAGE_IMPLEMENTATION
#include <stb_image.h>
#define STB_IMAGE_WRITE_IMPLEMENTATION
#include <stb_image_write.h>
#include <algorithm>
#include <vector>

namespace fs = std::filesystem;

const pixelColor DEFAULT_MASK_COLOR = pixelColor(0, 0, 0, 0);
const pixelColor DEFAULT_IMAGE_COLOR = pixelColor(0, 0, 0, 255);

std::map<std::string, pluginFunction> imageToMaskFunctionRegistry = std::map<std::string, pluginFunction>();
std::map<std::string, pluginFunction> maskToImageFunctionRegistry = std::map<std::string, pluginFunction>();
std::map<std::string, pluginProgram> programRegistry = std::map<std::string, pluginProgram>();


void mkDir(const std::string& path) {
    if (path.empty()) {
        return;
    }
    CreateDirectory(path.c_str(), NULL);
}

void initFolder() {
    mkDir(".\\Image");
    mkDir(".\\LayerData");
    mkDir(".\\Plugins");
}

void initLayerDataDir(const std::string& layerName) {
    mkDir(std::format(".\\LayerData/{}", layerName));
}

std::string getImagePath(const std::string& imageName) {
    return std::format(".\\Image/{}.png", imageName);
}

std::string getMaskPath(const std::string& imageName, const std::string& layerName) {
    return std::format(".\\LayerData/{}/{}.png", imageName, layerName);
}

void ListExportFunctions(const std::string& dllPath) {
    HMODULE hDll = LoadLibrary(dllPath.c_str());
    if (!hDll) {
        std::cerr << "Failed to load DLL!" << std::endl;
        return;
    }

    PIMAGE_DOS_HEADER pDosHeader = (PIMAGE_DOS_HEADER)hDll;
    PIMAGE_NT_HEADERS pNtHeaders = (PIMAGE_NT_HEADERS)((BYTE*)hDll + pDosHeader->e_lfanew);
    PIMAGE_EXPORT_DIRECTORY pExportDir = (PIMAGE_EXPORT_DIRECTORY)((BYTE*)hDll +
                                                                   pNtHeaders->OptionalHeader.DataDirectory[IMAGE_DIRECTORY_ENTRY_EXPORT].VirtualAddress);

    DWORD* pRvaName = (DWORD*)((BYTE*)hDll + pExportDir->AddressOfNames);
    for (DWORD i = 0; i < pExportDir->NumberOfNames; i++) {
        char* functionName = (char*)((BYTE*)hDll + pRvaName[i]);
        std::cout << "Function: " << functionName << std::endl;
    }

    FreeLibrary(hDll);
}

bool loadPlugin(const std::string& dllPath) {
    if (dllPath.empty()) {
        return false;
    }
    HINSTANCE pluginDll = LoadLibrary(dllPath.c_str());
    if (pluginDll == nullptr) {
        return false;
    }
    pluginData* (*pluginDataGetter)() = (pluginData* (*)())GetProcAddress(pluginDll, "getPluginData");
    if (pluginDataGetter == nullptr) {
        return false;
    }
    pluginData* pluginDataPtr = pluginDataGetter();
    if (pluginDataPtr == nullptr) {
        return false;
    }
    pluginData pluginData = *pluginDataPtr;
    for (const pluginFunction& function : pluginData.imageToMaskFunctions) {
        imageToMaskFunctionRegistry.insert(std::make_pair(function.functionID, function));
    }
    for (const pluginFunction& function : pluginData.maskToImageFunctions) {
        maskToImageFunctionRegistry.insert(std::make_pair(function.functionID, function));
    }
    for (const pluginProgram& program : pluginData.programs) {
        programRegistry.insert(std::make_pair(program.programID, program));
    }
    return true;
}

image* readImage(const std::string& imagePath) {
    int width, height, channels;
    unsigned char* imageData = stbi_load(imagePath.c_str(), &width, &height, &channels, 4);
    if (imageData == nullptr) {
        return nullptr;
    }
    // 仅支持RGBA
    if (channels != 4) {
        return nullptr;
    }
    pixelColor* pixelPtr = (pixelColor*)malloc(width * height * sizeof(pixelColor));
    memcpy(pixelPtr, imageData, width * height * sizeof(pixelColor));
    return new image{width, height, pixelPtr};
}

void writeImage(const std::string& imagePath, image* image) {
    unsigned char* imageData = new unsigned char[image->width * image->height * 4];
    pixelColor* pixels = image->pixels;
    for (int i = 0; i < image->width * image->height; i++) {
        imageData[i * 4] = pixels[i].r;
        imageData[i * 4 + 1] = pixels[i].g;
        imageData[i * 4 + 2] = pixels[i].b;
        imageData[i * 4 + 3] = pixels[i].a;
    }
    stbi_write_png(imagePath.c_str(), image->width, image->height, 4, imageData, image->width * 4);
    free(imageData);
}

void executeProgramI2M(const std::string& imageName, const std::string& programID) {
    image* inputImage = readImage(getImagePath(imageName));
    if (inputImage == nullptr) {
        throw std::runtime_error("Image not found");
    }
    if (programID.empty() || programRegistry.find(programID) == programRegistry.end()) {
        throw std::runtime_error("Program not found");
    }
    pluginProgram program = programRegistry.at(programID);
    if (!program.allowImageToMask) {
        throw std::logic_error("Program is not allowed to execute image to mask");
    }
    int imageWidth = inputImage->width;
    int imageHeight = inputImage->height;
    int maskPixelCount = imageWidth * imageHeight;
    int maskCount = program.programLines.size();
    int pixelSize = sizeof(pixelColor);
    int maskPixelSize = maskPixelCount * pixelSize;
    pixelColor *maskPixelPtr = (pixelColor*)malloc(maskPixelSize * maskCount);
    bool *maskPixelStatusPtr = (bool*)malloc(maskPixelCount * sizeof(bool));
    memset(maskPixelStatusPtr, 0, maskPixelCount * sizeof(bool));
    for (int i = 0; i < maskCount; i++) {
        for (int j = 0; j < maskPixelCount; j++) {
            // 填充默认值
            memcpy(&maskPixelPtr[i * maskPixelCount + j], &DEFAULT_MASK_COLOR, pixelSize);
        }
    }
    std::vector<std::string> functionLineName = std::vector<std::string>();
    void* processorVariables = malloc(program.programVariableSize);
    program.initProgramVariables(processorVariables);
    int nowProgramLineIndex = 0;
    for (std::list<pluginProgramLine>::const_reverse_iterator plPtr = program.programLines.crbegin(); plPtr != program.programLines.crend(); ++plPtr) {
        std::string functionLineID = plPtr->functionLineID;
        std::string imageToMaskID = plPtr->imageToMaskID;
        if (imageToMaskFunctionRegistry.find(imageToMaskID) == imageToMaskFunctionRegistry.end()) {
            throw std::runtime_error("ImageToMask Function not found");
        }
        auto function = imageToMaskFunctionRegistry.at(imageToMaskID).function;
        for (int y = 0 ; y < inputImage->height; y++) {
            for (int x = 0; x < inputImage->width; x++) {
                int index = y * imageWidth + x;
                if (maskPixelStatusPtr[index]) {
                    continue;
                }
                pixelData nowPixelData = pixelData(x, y, inputImage->pixels[index].r, inputImage->pixels[index].g, inputImage->pixels[index].b, inputImage->pixels[index].a);
                functionResult result = function(nowPixelData, processorVariables);
                if (!result.success) {
                    continue;
                }
                int maskPixelStart = nowProgramLineIndex * maskPixelCount + index;
                memcpy(&maskPixelPtr[maskPixelStart], &result.pixel, pixelSize);
                maskPixelStatusPtr[index] = true;
            }
        }
        functionLineName.push_back(plPtr->functionLineID);
        nowProgramLineIndex++;
    }
    initLayerDataDir(imageName);
    for (int i = 0; i < maskCount; i++) {
        std::string maskName = functionLineName.at(i);
        image maskImage = image(imageWidth, imageHeight, &maskPixelPtr[i * maskPixelCount]);
        writeImage(getMaskPath(imageName, maskName), &maskImage);
    }
    free(maskPixelPtr);
    free(maskPixelStatusPtr);
    free(processorVariables);
    delete inputImage;
}

void executeProgramM2I(const std::string& imageName, const std::string& programID) {
    if (programID.empty() || programRegistry.find(programID) == programRegistry.end()) {
        throw std::runtime_error("Program not found");
    }
    pluginProgram program = programRegistry.at(programID);
    if (!program.allowMaskToImage) {
        throw std::logic_error("Program is not allowed to execute mask to image");
    }
    int maskCount = program.programLines.size();
    image* maskImages[maskCount];
    int nowMaskIndex = 0;
    for (std::list<pluginProgramLine>::const_reverse_iterator plPtr = program.programLines.crbegin(); plPtr != program.programLines.crend(); ++plPtr) {
        std::string maskName = plPtr->functionLineID;
        image* maskImage = readImage(getMaskPath(imageName, maskName));
        if (maskImage == nullptr) {
            throw std::runtime_error("Mask not found");
        }
        maskImages[nowMaskIndex] = maskImage;
        nowMaskIndex++;
    }
    image* templateImage = maskImages[0];
    int imageWidth = templateImage->width;
    int imageHeight = templateImage->height;
    int imagePixelCount = imageWidth * imageHeight;
    int pixelSize = sizeof(pixelColor);
    int imagePixelSize = imagePixelCount * pixelSize;
    pixelColor *imagePixelPtr = (pixelColor*)malloc(imagePixelSize);
    bool *imagePixelStatusPtr = (bool*)malloc(imagePixelCount * sizeof(bool));
    memset(imagePixelStatusPtr, 0, imagePixelCount * sizeof(bool));
    for (int i = 0; i < imagePixelCount; i++) {
        memcpy(&imagePixelPtr[i], &DEFAULT_IMAGE_COLOR, pixelSize);
    }
    void* processorVariables = malloc(program.programVariableSize);
    program.initProgramVariables(processorVariables);
    int nowProgramLineIndex = 0;
    for (std::list<pluginProgramLine>::const_reverse_iterator plPtr = program.programLines.crbegin(); plPtr != program.programLines.crend(); ++plPtr) {
        std::string maskToImageID = plPtr->maskToImageID;
        if (maskToImageFunctionRegistry.find(maskToImageID) == maskToImageFunctionRegistry.end()) {
            throw std::runtime_error("ImageToMask Function not found");
        }
        auto function = maskToImageFunctionRegistry.at(maskToImageID).function;
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                int index = y * imageWidth + x;
                if (imagePixelStatusPtr[index]) {
                    continue;
                }
                pixelColor* maskPixel = maskImages[nowProgramLineIndex]->pixels;
                pixelData nowPixelData = pixelData(x, y, maskPixel[index].r, maskPixel[index].g, maskPixel[index].b, maskPixel[index].a);
                functionResult result = function(nowPixelData, processorVariables);
                if (!result.success) {
                    continue;
                }
                memcpy(&imagePixelPtr[index], &result.pixel, pixelSize);
                imagePixelStatusPtr[index] = true;
            }
        }
        nowProgramLineIndex++;
    }
    image outputImage = image(imageWidth, imageHeight, imagePixelPtr);
    writeImage(getImagePath(imageName), &outputImage);
    free(imagePixelPtr);
    free(imagePixelStatusPtr);
    free(processorVariables);
    for (int i = 0; i < maskCount; i++) {
        delete maskImages[i];
    }
}


std::string removeExtension(const std::string& fileName) {
    std::string::size_type index = fileName.find_last_of('.');
    if (index == std::string::npos) {
        return fileName;
    }
    return fileName.substr(0, index);
}


std::list<std::string> getAllFilesInFolder(const std::string& folderPath, const std::string& extension = "", bool fullPath = false) {
    std::list<std::string> filePaths;
    std::string currentExtension = extension;
    if (!std::filesystem::exists(folderPath)) {
        throw std::runtime_error("Folder not found");
    }
    if (!std::filesystem::is_directory(folderPath)) {
        throw std::runtime_error("Path is not a folder");
    }
    if (!currentExtension.empty() && currentExtension[0] != '.') {
        currentExtension = "." + extension;
    }
    for (const auto& entry : std::filesystem::directory_iterator(folderPath)) {
        if (!entry.is_regular_file()) {
            continue;
        }
        std::string fileName = entry.path().filename().string();
        if (!fileName.ends_with(currentExtension)) {
            continue;
        }
        if (fullPath) {
            filePaths.push_back(entry.path().string());
        } else {
            filePaths.push_back(entry.path().filename().string());
        }
    }
    return filePaths;
}


std::list<std::string> getAllFolderInFolder(const std::string& folderPath, bool fullPath = false) {
    std::list<std::string> folderPaths;
    if (!std::filesystem::exists(folderPath)) {
        throw std::runtime_error("Folder not found");
    }
    if (!std::filesystem::is_directory(folderPath)) {
        throw std::runtime_error("Path is not a folder");
    }
    for (const auto& entry : std::filesystem::directory_iterator(folderPath)) {
        if (!entry.is_directory()) {
            continue;
        }
        if (fullPath) {
            folderPaths.push_back(entry.path().string());
        } else {
            folderPaths.push_back(entry.path().filename().string());
        }
    }
    return folderPaths;
}


bool loadAllPlugins(bool silent = true) {
    std::string pluginFolderPath = ".\\Plugins";
    std::list<std::string> pluginFilePaths = getAllFilesInFolder(pluginFolderPath, ".dll", true);
    bool allPluginLoaded = true;
    int successCount = 0;
    int failCount = 0;
    for (std::string pluginFilePath : pluginFilePaths) {
        bool success = loadPlugin(pluginFilePath);
        if (!success) {
            std::cout << "Failed to load plugin: " << pluginFilePath << std::endl;
            std::cout << "Plugin dll export:" << std::endl;
            ListExportFunctions(pluginFilePath);
            allPluginLoaded = false;
            failCount++;
        } else {
            if (!silent) {
                std::cout << "Loaded plugin: " << pluginFilePath << std::endl;
            }
            successCount++;
        }
    }
    std::cout << "Loaded " << successCount << " plugins, failed to load " << failCount << " plugins" << std::endl;
    return allPluginLoaded;
}


int selectMenuIndex(const std::list<std::string>& menuItems, int startDisplayIndex = 0) {
    std::cout << "选择一个选项:" << std::endl;
    int index = startDisplayIndex;
    for (const std::string& menuItem : menuItems) {
        std::cout << "[" << index << "] " << menuItem << std::endl;
        index++;
    }
    std::cout << "> " << std::flush;
    std::string input;
    std::cin >> input;
    std::cout << std::endl;
    if (!std::all_of(input.begin(), input.end(), ::isdigit)) {
        return -1;
    }
    index = std::stoi(input) - startDisplayIndex;
    if (index < 0 || index >= menuItems.size()) {
        return -1;
    }
    return index;
}


std::string selectMenu(const std::list<std::string>& menuItems, int startDisplayIndex = 0) {
    int index = selectMenuIndex(menuItems, startDisplayIndex);
    if (index < 0) {
        return "";
    }
    for (const std::string& menuItem : menuItems) {
        if (index == 0) {
            return menuItem;
        }
        index--;
    }
    return "";
}


std::list<std::string> getAllPrograms(bool split, bool merge) {
    std::list<std::string> programNames;
    // programRegistry
    for (std::pair<std::string, pluginProgram> programEntry : programRegistry) {
        if (split && !programEntry.second.allowImageToMask) {
            continue;
        }
        if (merge && !programEntry.second.allowMaskToImage) {
            continue;
        }
        programNames.push_back(programEntry.first);
    }
    return programNames;
}


void splitLayerMenu() {
    std::string FileName = selectMenu(getAllFilesInFolder(".\\Image", ".png", false), 1);
    if (FileName.empty()) {
        return;
    }
    std::string ProgramID = selectMenu(getAllPrograms(true, false), 1);
    if (ProgramID.empty()) {
        return;
    }
    std::string FileNameWithoutExtension = removeExtension(FileName);
    try {
        executeProgramI2M(FileNameWithoutExtension, ProgramID);
        std::cout << "SplitLayer Success [" << FileName << "] [" << ProgramID << "]" << std::endl;
    } catch (std::exception& e) {
        std::cout << "SplitLayer Fail [" << FileName << "] [" << ProgramID << "]:" << e.what() << std::endl;
    }
    std::cout << std::endl;
}


void splitAllLayerMenu() {
    std::string ProgramID = selectMenu(getAllPrograms(true, false), 1);
    if (ProgramID.empty()) {
        return;
    }
    for (const std::string& FileName : getAllFilesInFolder(".\\Image", ".png", false)) {
        std::string FileNameWithoutExtension = removeExtension(FileName);
        try {
            executeProgramI2M(FileNameWithoutExtension, ProgramID);
            std::cout << "SplitLayer Success [" << FileName << "] [" << ProgramID << "]" << std::endl;
        } catch (std::exception& e) {
            std::cout << "SplitLayer Fail [" << FileName << "] [" << ProgramID << "]:" << e.what() << std::endl;
        }
    }
    std::cout << std::endl;
}


void mergeLayerMenu() {
    std::string FileName = selectMenu(getAllFolderInFolder(".\\LayerData", false), 1);
    if (FileName.empty()) {
        return;
    }
    std::string ProgramID = selectMenu(getAllPrograms(false, true), 1);
    if (ProgramID.empty()) {
        return;
    }
    try {
        executeProgramM2I(FileName, ProgramID);
        std::cout << "MergeLayer Success [" << FileName << "] [" << ProgramID << "]" << std::endl;
    } catch (std::exception& e) {
        std::cout << "MergeLayer Fail [" << FileName << "] [" << ProgramID << "]:" << e.what() << std::endl;
    }
    std::cout << std::endl;
}


void mergeAllLayerMenu() {
    std::string ProgramID = selectMenu(getAllPrograms(false, true), 1);
    if (ProgramID.empty()) {
        return;
    }
    for (const std::string& FileName : getAllFolderInFolder(".\\LayerData", false)) {
        try {
            executeProgramM2I(FileName, ProgramID);
            std::cout << "MergeLayer [" << FileName << "]" << std::endl;
        } catch (std::exception& e) {
            std::cout << "MergeLayer Fail [" << FileName << "]:" << e.what() << std::endl;
        }
    }
    std::cout << std::endl;
}


void consoleMenu() {
    std::list<std::string> menuItems = std::list<std::string>{"退出", "分离单个Layer", "分离全部Layer", "合并单个Layer", "合并全部Layer"};
    while (true) {
        int selectedIndex = selectMenuIndex(menuItems, 0);
        switch (selectedIndex) {
            case 0:
                return;
            case 1:
                splitLayerMenu();
                continue;
            case 2:
                splitAllLayerMenu();
                continue;
            case 3:
                mergeLayerMenu();
                continue;
            case 4:
                mergeAllLayerMenu();
                continue;
            default:
                std::cout << "无效的选项" << std::endl;
                continue;
        }
    }
}


int main() {
    SetConsoleOutputCP(65001);
    initFolder();
    loadAllPlugins();
    consoleMenu();
    return 0;
}
