#include <string>
#include <list>
#include <utility>

struct pixelPosition {
    int x;
    int y;
    pixelPosition(const int x, const int y) {
        this->x = x;
        this->y = y;
    }

    pixelPosition(const pixelPosition& pixel) {
        this->x = pixel.x;
        this->y = pixel.y;
    }

    bool equals(const pixelPosition& position) const {
        return this->x == position.x && this->y == position.y;
    }
};

struct pixelColor {
    unsigned char r;
    unsigned char g;
    unsigned char b;
    unsigned char a;
    pixelColor(const unsigned char r, const unsigned char g, const unsigned char b, const unsigned char a) {
        this->r = r;
        this->g = g;
        this->b = b;
        this->a = a;
    }

    pixelColor(const pixelColor& pixel) {
        this->r = pixel.r;
        this->g = pixel.g;
        this->b = pixel.b;
        this->a = pixel.a;
    }

    bool equals(const pixelColor& color) const {
        return this->r == color.r && this->g == color.g && this->b == color.b && this->a == color.a;
    }
};

struct pixelData {
    int x;
    int y;
    unsigned char r;
    unsigned char g;
    unsigned char b;
    unsigned char a;
    pixelData(const int x, const int y, const unsigned char r, const unsigned char g, const unsigned char b, const unsigned char a) {
        this->x = x;
        this->y = y;
        this->r = r;
        this->g = g;
        this->b = b;
        this->a = a;
    }

    pixelData(const pixelData &pixel) {
        this->x = pixel.x;
        this->y = pixel.y;
        this->r = pixel.r;
        this->g = pixel.g;
        this->b = pixel.b;
        this->a = pixel.a;
    }

    pixelData(const pixelPosition& position, const pixelColor& color) {
        this->x = position.x;
        this->y = position.y;
        this->r = color.r;
        this->g = color.g;
        this->b = color.b;
        this->a = color.a;
    }

    pixelColor getColor() {
        return {this->r, this->g, this->b, this->a};
    }

    pixelPosition getPosition() {
        return {this->x, this->y};
    }

    bool equals(const pixelData& pixel) const {
        return this->x == pixel.x && this->y == pixel.y && this->r == pixel.r && this->g == pixel.g && this->b == pixel.b && this->a == pixel.a;
    }

    bool equals(const pixelPosition& position) const {
        return this->x == position.x && this->y == position.y;
    }

    bool equals(const pixelColor& color) const {
        return this->r == color.r && this->g == color.g && this->b == color.b && this->a == color.a;
    }
};

struct functionResult {
    bool success;
    const pixelColor pixel;
};

struct pluginFunction {
    std::string functionID;
    functionResult (*function)(const pixelData&, void* programVariables);

    pluginFunction(std::string functionID, functionResult (*function)(const pixelData&, void* programVariables)) {
        this->functionID = functionID;
        this->function = function;
    }
};

struct pluginProgramLine {
    std::string functionLineID;
    std::string imageToMaskID;
    std::string maskToImageID;
    pluginProgramLine(std::string functionLineID, std::string imageToMaskID, std::string maskToImageID) {
        this->functionLineID = functionLineID;
        this->imageToMaskID = imageToMaskID;
        this->maskToImageID = maskToImageID;
    }
};

struct pluginProgram {
    std::string programID;
    int programVariableSize;
    void (*initProgramVariables)(void* programVariables);
    std::list<pluginProgramLine> programLines;
    bool allowImageToMask = true;
    bool allowMaskToImage = true;

    pluginProgram(const std::string& programID, const int programVariableSize, void (*initprogramVariables)(void* programVariables), const std::list<pluginProgramLine>& processLines, const bool allowImageToMask = true, const bool allowMaskToImage = true) {
        this->programID = programID;
        this->programVariableSize = programVariableSize;
        this->initProgramVariables = initprogramVariables;
        this->programLines = processLines;
        this->allowImageToMask = allowImageToMask;
        this->allowMaskToImage = allowMaskToImage;
    }
};

struct pluginData {
    std::list<pluginFunction> imageToMaskFunctions;
    std::list<pluginFunction> maskToImageFunctions;
    std::list<pluginProgram> programs;

    pluginData(const std::list<pluginFunction>& imageToMaskFunctions, const std::list<pluginFunction>& maskToImageFunctions, const std::list<pluginProgram>& programs) {
        this->imageToMaskFunctions = imageToMaskFunctions;
        this->maskToImageFunctions = maskToImageFunctions;
        this->programs = programs;
    }
};

struct image {
    int width;
    int height;
    pixelColor *pixels;
};

#ifdef Plugin
extern "C" __declspec(dllexport) pluginData* getPluginData();  // 插件需要实现此函数，返回插件数据
#endif

