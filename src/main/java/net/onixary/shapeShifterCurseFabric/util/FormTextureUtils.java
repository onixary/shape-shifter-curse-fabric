package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form_render.OriginFurModel;

import java.io.IOException;
import java.io.InputStream;


// 尽量少在Origin Fur中修改 减少后续工作量
public class FormTextureUtils {

    public record ColorSetting(int primaryColor, int accentColor1, int accentColor2) {
        public int getPrimaryColor() {
            return this.primaryColor;
        }
        public int getAccentColor1() {
            return this.accentColor1;
        }
        public int getAccentColor2() {
            return this.accentColor2;
        }
    }

    public static Identifier getColorMask_Texture(OriginFurModel model) {
        String MaskIDStr = JsonHelper.getString(model.json, "texture_mask", null);
        return MaskIDStr == null ? null : Identifier.tryParse(MaskIDStr);
    }

    public static Identifier getColorMask_OverlayTexture(OriginFurModel model, boolean Slim) {
        String MaskIDStr = null;
        if (!Slim) {
            MaskIDStr = JsonHelper.getString(model.json, "overlay_mask", null);
        }
        else {
            MaskIDStr = JsonHelper.getString(model.json, "overlay_slim_mask", null);
        }
        return MaskIDStr == null ? null : Identifier.tryParse(MaskIDStr);
    }

    public static Identifier getColorMask_EmissiveTexture(OriginFurModel model, boolean Slim) {
        String MaskIDStr = null;
        if (!Slim) {
            MaskIDStr = JsonHelper.getString(model.json, "emissive_overlay_mask", null);
        }
        else {
            MaskIDStr = JsonHelper.getString(model.json, "emissive_overlay_slim_mask", null);
        }
        return MaskIDStr == null ? null : Identifier.tryParse(MaskIDStr);
    }

    public static NativeImage toNativeImage(Identifier texture) {
        NativeImage nativeImage = null;
        ResourceManager RM = MinecraftClient.getInstance().getResourceManager();
        Resource resource = null;
        try {
            resource = RM.getResourceOrThrow(texture);
            InputStream inputStream = resource.getInputStream();
            nativeImage = NativeImage.read(inputStream);
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.warn("Failed to load texture: " + texture);
        }
        return nativeImage;
    }

    public static int RGBA2ABGR(int color) {
        // Native Image 获取的像素为ABGR顺序 但是大众习惯为RGBA
        int R = (color >> 24) & 0xFF;
        int G = (color >> 16) & 0xFF;
        int B = (color >> 8) & 0xFF;
        int A = color & 0xFF;
        return (A << 24) | (B << 16) | (G << 8) | R;
    }

    public static int RGB2ABGR(int color) {
        // Native Image 获取的像素为ABGR顺序 但是大众习惯为RGB 默认Alpha为255
        return RGBA2ABGR((color<<8) | 0xFF);
    }

    public static int ABGR2RGBA(int color) {
        int R = color & 0xFF;
        int G = (color >> 8) & 0xFF;
        int B = (color >> 16) & 0xFF;
        int A = (color >> 24) & 0xFF;
        return R << 24 | G << 16 | B << 8 | A;
    }

    public static int ABGR2RGB(int color) {
        return ABGR2RGBA(color) >> 8;
    }

    // 除非编译为inline 否则对性能提升不大 而java只支持动态inline
    // #define div_255_fast_v2(x) (((x) + 1 + (((x) + 1) >> 8)) >> 8)
    // public static final int div_255_fast_v2(int x) {
    //     return (((x) + 1 + (((x) + 1) >> 8)) >> 8);
    // }

    public static int ColorMul(int ColorA, int ColorB, int Mask) {
        // 颜色通道 ColorA -> 63 ColorB -> 127 M -> 127 Result -> 95
        // Mask <= 0xFF
        if(Mask <= 0) return ColorA;
        if(Mask >= 255) return ColorB;

        int invMask = 255 - Mask;
        // 保留ColorA的Alpha通道(>>>24)，只混合RGB通道
        return (ColorA & 0xFF000000) | // Alpha通道
               ((((ColorA >> 16) & 0xFF) * invMask + ((ColorB >> 16) & 0xFF) * Mask) / 255 << 16) | // Red
               ((((ColorA >> 8) & 0xFF) * invMask + ((ColorB >> 8) & 0xFF) * Mask) / 255 << 8) | // Green
               (((ColorA & 0xFF) * invMask + (ColorB & 0xFF) * Mask) / 255); // Blue
    }

    public static int ProcessPixel(int Color, int Mask, ColorSetting colorSetting) {
        // ABGR顺序
        // int A = (Mask >> 24);
        int R = Mask & 0xFF;
        int G = (Mask >> 8) & 0xFF;
        int B = (Mask >> 16) & 0xFF;
        // int Pixel = ColorMul(Color, colorSetting.primaryColor, (Mask >> 16) & 0xFF);
        // Pixel = ColorMul(Pixel, colorSetting.accentColor1, (Mask >> 8) & 0xFF);
        // Pixel = ColorMul(Pixel, colorSetting.accentColor2, (Mask & 0xFF));
        if (B > 0) {
            return ColorMul(Color, colorSetting.accentColor2, B);
        }
        else if (G > 0) {
            return ColorMul(Color, colorSetting.accentColor1, G);
        }
        else if (R > 0) {
            return ColorMul(Color, colorSetting.primaryColor, R);
        }
        else {
            return Color;
        }
        // return Pixel;
    }

    public static Identifier BakeTexture(Identifier texture, Identifier mask, ColorSetting colorSetting)  {
        if (texture == null || mask == null) return null;
        NativeImage textureImage = toNativeImage(texture);
        NativeImage maskImage = toNativeImage(mask);
        int textureWidth = textureImage.getWidth();
        int textureHeight = textureImage.getHeight();
        for (int x = 0; x < textureWidth; x++) {
            for (int y = 0; y < textureHeight; y++) {
                textureImage.setColor(x, y, ProcessPixel(textureImage.getColor(x, y), maskImage.getColor(x, y), colorSetting));
            }
        }
        TextureManager TM = MinecraftClient.getInstance().getTextureManager();
        // 客户端会在每次重载资源包时数据溢出 溢出量不高 等以后再优化吧
        return TM.registerDynamicTexture("masked_texture", new NativeImageBackedTexture(textureImage));
    }

    public static Identifier getBakedTexture(OriginFurModel model, ColorSetting colorSetting) {
        Identifier CachedTexture = model.ColorMask_Baked_Textures.get(colorSetting);
        if (CachedTexture != null) {
            return CachedTexture;
        }
        CachedTexture = BakeTexture(OriginFurModel.dTR(model.json), getColorMask_Texture(model), colorSetting);
        if (CachedTexture == null) {
            CachedTexture = OriginFurModel.dTR(model.json);
        }
        model.ColorMask_Baked_Textures.put(colorSetting, CachedTexture);
        return CachedTexture;
    }

    public static Identifier getBakedOverlayTexture(OriginFurModel model, ColorSetting colorSetting, boolean Slim) {
        if (!Slim) {
            Identifier CachedTexture = model.ColorMask_Baked_OverlayTexture.get(colorSetting);
            if (CachedTexture != null) {
                return CachedTexture;
            }
            CachedTexture = BakeTexture(Identifier.tryParse(JsonHelper.getString(model.json, "overlay", null)), getColorMask_OverlayTexture(model, Slim), colorSetting);
            if (CachedTexture == null) {
                CachedTexture = Identifier.tryParse(JsonHelper.getString(model.json, "overlay", null));
            }
            model.ColorMask_Baked_OverlayTexture.put(colorSetting, CachedTexture);
            return CachedTexture;
        }
        else {
            Identifier CachedTexture = model.ColorMask_Baked_OverlayTexture_Slim.get(colorSetting);
            if (CachedTexture != null) {
                return CachedTexture;
            }
            CachedTexture = BakeTexture(Identifier.tryParse(JsonHelper.getString(model.json, "overlay_slim", null)), getColorMask_OverlayTexture(model, Slim), colorSetting);
            if (CachedTexture == null) {
                CachedTexture = Identifier.tryParse(JsonHelper.getString(model.json, "overlay_slim", null));
            }
            model.ColorMask_Baked_OverlayTexture_Slim.put(colorSetting, CachedTexture);
            return CachedTexture;
        }
    }

    public static Identifier getBakedEmissiveTexture(OriginFurModel model, ColorSetting colorSetting, boolean Slim) {
        if (!Slim) {
            Identifier CachedTexture = model.ColorMask_Baked_EmissiveTexture.get(colorSetting);
            if (CachedTexture != null) {
                return CachedTexture;
            }
            CachedTexture = BakeTexture(Identifier.tryParse(JsonHelper.getString(model.json, "emissive_overlay", null)), getColorMask_EmissiveTexture(model, Slim), colorSetting);
            if (CachedTexture == null) {
                CachedTexture = Identifier.tryParse(JsonHelper.getString(model.json, "emissive_overlay", null));
            }
            model.ColorMask_Baked_EmissiveTexture.put(colorSetting, CachedTexture);
            return CachedTexture;
        } else {
            Identifier CachedTexture = model.ColorMask_Baked_EmissiveTexture_Slim.get(colorSetting);
            if (CachedTexture != null) {
                return CachedTexture;
            }
            CachedTexture = BakeTexture(Identifier.tryParse(JsonHelper.getString(model.json, "emissive_overlay_slim", null)), getColorMask_EmissiveTexture(model, Slim), colorSetting);
            if (CachedTexture == null) {
                CachedTexture = Identifier.tryParse(JsonHelper.getString(model.json, "emissive_overlay_slim", null));
            }
            model.ColorMask_Baked_EmissiveTexture_Slim.put(colorSetting, CachedTexture);
            return CachedTexture;
        }
    }
}
