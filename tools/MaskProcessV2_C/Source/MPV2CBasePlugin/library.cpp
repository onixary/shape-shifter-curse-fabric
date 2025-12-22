#define Plugin
#include "dataStruct.h"

const pixelColor LayerColor = pixelColor(255, 0, 0, 255);

pluginData* getPluginData() {
    std::list<pluginFunction> imageToMaskFunctions = std::list<pluginFunction>();
    std::list<pluginFunction> maskToImageFunctions = std::list<pluginFunction>();
    std::list<pluginProgram> programs = std::list<pluginProgram>();
    imageToMaskFunctions.push_back(pluginFunction("primaryColorI2M", [](const pixelData& pixel, void* programVariables) -> functionResult {
        bool success = pixel.r == 255 && pixel.a == 255;
        return functionResult(success, LayerColor);
    }));
    maskToImageFunctions.push_back(pluginFunction("primaryColorM2I", [](const pixelData& pixel, void* programVariables) -> functionResult {
        bool success = pixel.equals(LayerColor);
        return functionResult(success, pixelColor(255, 0, 0, 255));
    }));
    imageToMaskFunctions.push_back(pluginFunction("accentColor1I2M", [](const pixelData& pixel, void* programVariables) -> functionResult {
        bool success = pixel.g == 255 && pixel.a == 255;
        return functionResult(success, LayerColor);
    }));
    maskToImageFunctions.push_back(pluginFunction("accentColor1M2I", [](const pixelData& pixel, void* programVariables) -> functionResult {
        bool success = pixel.equals(LayerColor);
        return functionResult(success, pixelColor(0, 255, 0, 255));
    }));
    imageToMaskFunctions.push_back(pluginFunction("accentColor2I2M", [](const pixelData& pixel, void* programVariables) -> functionResult {
        bool success = pixel.b == 255 && pixel.a == 255;
        return functionResult(success, LayerColor);
    }));
    maskToImageFunctions.push_back(pluginFunction("accentColor2M2I", [](const pixelData& pixel, void* programVariables) -> functionResult {
        bool success = pixel.equals(LayerColor);
        return functionResult(success, pixelColor(0, 0, 255, 255));
    }));
    imageToMaskFunctions.push_back(pluginFunction("eyeColorAI2M", [](const pixelData& pixel, void* programVariables) -> functionResult {
        bool success = pixel.r == 0 && pixel.g == 0 && pixel.b == 0 && pixel.a == 1;
        return functionResult(success, LayerColor);
    }));
    maskToImageFunctions.push_back(pluginFunction("eyeColorAM2I", [](const pixelData& pixel, void* programVariables) -> functionResult {
        bool success = pixel.equals(LayerColor);
        return functionResult(success, pixelColor(0, 0, 0, 1));
    }));
    imageToMaskFunctions.push_back(pluginFunction("eyeColorBI2M", [](const pixelData& pixel, void* programVariables) -> functionResult {
        bool success = pixel.r == 0 && pixel.g == 0 && pixel.b == 0 && pixel.a == 2;
        return functionResult(success, LayerColor);
    }));
    maskToImageFunctions.push_back(pluginFunction("eyeColorBM2I", [](const pixelData& pixel, void* programVariables) -> functionResult {
        bool success = pixel.equals(LayerColor);
        return functionResult(success, pixelColor(0, 0, 0, 2));
    }));
    programs.push_back(pluginProgram("defaultProgram", 0, [](void* programVariables) -> void {}, std::list<pluginProgramLine>({
        pluginProgramLine("primaryColor", "primaryColorI2M", "primaryColorM2I"),
        pluginProgramLine("accentColor1", "accentColor1I2M", "accentColor1M2I"),
        pluginProgramLine("accentColor2", "accentColor2I2M", "accentColor2M2I"),
        pluginProgramLine("eyeColorA", "eyeColorAI2M", "eyeColorAM2I"),
        pluginProgramLine("eyeColorB", "eyeColorBI2M", "eyeColorBM2I")
    })));
    return new pluginData(imageToMaskFunctions, maskToImageFunctions, programs);
}

