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
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;


// 尽量少在Origin Fur中修改 减少后续工作量
public class FormTextureUtils {

    // onixary: 加入每个通道的覆盖强度overrideStrength的float参数，范围0.0~1.0
    // 一些形态原版的贴图过黑，转换为灰度后无法看出自定义颜色，
    // 应用后的灰度 = max(原灰度 + overrideStrength*255, 255)
    public record ColorSetting(int primaryColor, int accentColor1, int accentColor2, int eyeColor
            , boolean primaryGreyReverse, boolean accent1GreyReverse, boolean accent2GreyReverse) {
        public int getPrimaryColor() {
            return this.primaryColor;
        }
        public int getAccentColor1() {
            return this.accentColor1;
        }
        public int getAccentColor2() {
            return this.accentColor2;
        }
        public int getEyeColor() {
            return this.eyeColor;
        }
        public boolean getPrimaryGreyReverse() {
            return this.primaryGreyReverse;
        }
        public boolean getAccent1GreyReverse() {
            return this.accent1GreyReverse;
        }
        public boolean getAccent2GreyReverse() {
            return this.accent2GreyReverse;
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

    public static int ARGB2ABGR(int color) {
        int Alpha = (color >> 24) & 0xFF;
        return RGBA2ABGR((color << 8) | Alpha);
    }

    public static int ABGR2RGBA(int color) {
        int R = color & 0xFF;
        int G = (color >> 8) & 0xFF;
        int B = (color >> 16) & 0xFF;
        int A = (color >> 24) & 0xFF;
        return R << 24 | G << 16 | B << 8 | A;
    }

    public static int ABGR2RGB(int color) {
        return (ABGR2RGBA(color) >> 8) & 0x00FFFFFF;
    }

    public static int ABGR2ARGB(int color) {
        return ABGR2RGB(color) | (color & 0XFF000000);
    }

    // 除非编译为inline 否则对性能提升不大 而java只支持动态inline
    // #define div_255_fast_v2(x) (((x) + 1 + (((x) + 1) >> 8)) >> 8)
    // public static final int div_255_fast_v2(int x) {
    //     return (((x) + 1 + (((x) + 1) >> 8)) >> 8);
    // }

    //public static int ColorMix(int ColorA, int ColorB, int Mask) {
    //    // 颜色通道 ColorA -> 63 ColorB -> 127 M -> 127 Result -> 95
    //    // Mask <= 0xFF
    //    if(Mask <= 0) return ColorA;
    //    if(Mask >= 255) return ColorB;
    //    int invMask = 255 - Mask;
    //    // 保留ColorA的Alpha通道(>>>24)，只混合RGB通道
    //    return (ColorA & 0xFF000000) | // Alpha通道
    //           ((((ColorA >> 16) & 0xFF) * invMask + ((ColorB >> 16) & 0xFF) * Mask) / 255 << 16) | // Red
    //           ((((ColorA >> 8) & 0xFF) * invMask + ((ColorB >> 8) & 0xFF) * Mask) / 255 << 8) | // Green
    //           (((ColorA & 0xFF) * invMask + (ColorB & 0xFF) * Mask) / 255); // Blue
    //}

    // public static int ColorMul(int ColorA, int ColorB) {
    //     // 保留ColorA的Alpha通道(>>>24)，只混合RGB通道
    //     return (ColorA & 0xFF000000) | // Alpha通道
    //            ((((ColorA >> 16) & 0xFF) * ((ColorB >> 16) & 0xFF)) / 255 << 16) | // Red
    //            ((((ColorA >> 8) & 0xFF) * ((ColorB >> 8) & 0xFF)) / 255 << 8) | // Green
    //            (((ColorA & 0xFF) * (ColorB & 0xFF)) / 255); // Blue
    // }

    public static int ColorMulBytes(int ColorA, int Bytes) {
        // 保留ColorA的Alpha通道(>>>24)，只混合RGB通道
        // 几乎大部分情况不会超过255
        // if(Mask <= 0) return ColorA;
        // if(Mask >= 255) return ColorB;
        return (ColorA & 0xFF000000) | // Alpha通道
               ((((ColorA >> 16) & 0xFF) * Bytes) / 255 << 16) | // Red
               ((((ColorA >> 8) & 0xFF) * Bytes) / 255 << 8) | // Green
               (((ColorA & 0xFF) * Bytes) / 255); // Blue
    }

    public static int GreyScaleMul(int Color, float GreyScale) {
        // ABGR顺序
        int R = Math.min(255, Math.max((int)(GreyScale * (Color & 0xFF)), 0));
        int G = Math.min(255, Math.max((int)(GreyScale * ((Color >> 8) & 0xFF)), 0));
        int B = Math.min(255, Math.max((int)(GreyScale * ((Color >> 16) & 0xFF)), 0));
        // Math.clamp 是Java 21的方法
        return 0xFF000000 | (B << 16) | (G << 8) | R;
    }

    // public static int ColorOverlay(int ColorA, int ColorOverlay) {
    //     int OverlayA = (ColorOverlay >> 24) & 0xFF;
    //     return ColorMix(ColorA, ColorOverlay, OverlayA);
    // }

    public static int getGreyScale(int color) {
        // 提取RGB通道（忽略Alpha通道）
        int R = (color >> 16) & 0xFF;
        int G = (color >> 8) & 0xFF;
        int B = color & 0xFF;
        return (R*28 + G*151 + B*77) >> 8;
    }

    // onixary: 加入overrideStrength参数影响
    public static int overrideGreyScale(int a, int b, float t) {
        return Math.min(a + (int)(t * b), 255);
    }

    public static Triple<Integer, Integer, Integer> getAverageGreyScale(NativeImage image, NativeImage maskImage) {
        // ABGR顺序
        int textureWidth = image.getWidth();
        int textureHeight = image.getHeight();
        long R = 0, G = 0, B = 0;
        int RC = 0, GC = 0, BC = 0;
        for (int x = 0; x < textureWidth; x++) {
            for (int y = 0; y < textureHeight; y++) {
                int Mask = maskImage.getColor(x, y);
                if ((Mask & 0x00FF0000) > 0) {
                    B += getGreyScale(image.getColor(x, y));
                    BC ++;
                }
                else if ((Mask & 0x0000FF00) > 0) {
                    G += getGreyScale(image.getColor(x, y));
                    GC ++;
                }
                else if ((Mask & 0x000000FF) > 0) {
                    R += getGreyScale(image.getColor(x, y));
                    RC ++;
                }
            }
        }
        return new ImmutableTriple<>((RC == 0 ? 255 : (int)R/RC), (GC == 0 ? 255 : (int)G/GC), (BC == 0 ? 255 : (int)B/BC));
    }

    public static int ProcessMaskChannel(int Color, int Mask, int ColorSetting, int AverageGreyScale, boolean ReverseGreyScale) {
        if (((ColorSetting >> 24) & 0xFF) == 0) return Color;
        int GreyScaleAdd = ReverseGreyScale ? AverageGreyScale - getGreyScale(Color) : getGreyScale(Color) - AverageGreyScale;
        Color = GreyScaleMul(ColorSetting | 0xFF000000, 1.0f + (float)GreyScaleAdd / 255.0f);
        return ColorMulBytes(Color, Mask);
    }

    public static int ProcessPixel(int Color, int Mask, ColorSetting colorSetting, Triple<Integer, Integer, Integer> MaskLayerAverageGreyScale) {
        // ABGR顺序
        // int A = (Mask >> 24);

        // 检查Mask的Alpha通道是否在0-16范围内，如果是则使用eyeColor
        int maskAlpha = Mask & 0xFF000000;
        if (maskAlpha >= 0 && maskAlpha < 16) {
            // 使用eyeColor替换颜色，但保留原始颜色的Alpha通道
            if (((colorSetting.eyeColor >> 24) & 0xFF) == 0) {
                return Color;
            }
            return (colorSetting.eyeColor & 0x00FFFFFF) | (Color & 0xFF000000);
        }

        if (Mask == 0) return Color;
        
        // 提取原始颜色的 alpha 值
        int B = (Mask >> 16) & 0xFF;
        if (B > 0) {
            int result = ProcessMaskChannel(Color, B, colorSetting.accentColor2, MaskLayerAverageGreyScale.getRight(), colorSetting.accent2GreyReverse);
            return (result & 0x00FFFFFF) | (Color & 0xFF000000);
        }
        int G = (Mask >> 8) & 0xFF;
        if (G > 0) {
            int result = ProcessMaskChannel(Color, G, colorSetting.accentColor1, MaskLayerAverageGreyScale.getMiddle(), colorSetting.accent1GreyReverse);
            return (result & 0x00FFFFFF) | (Color & 0xFF000000);
        }
        int R = Mask & 0xFF;
        if (R > 0) {
            int result = ProcessMaskChannel(Color, R, colorSetting.primaryColor, MaskLayerAverageGreyScale.getLeft(), colorSetting.primaryGreyReverse);
            return (result & 0x00FFFFFF) | (Color & 0xFF000000);
        }
        return Color;
    }

    public static Identifier BakeTexture(Identifier texture, Identifier mask, ColorSetting colorSetting)  {
        if (texture == null || mask == null) return null;
        NativeImage textureImage = toNativeImage(texture);
        NativeImage maskImage = toNativeImage(mask);
        int textureWidth = textureImage.getWidth();
        int textureHeight = textureImage.getHeight();
        Triple<Integer, Integer, Integer> MaskLayerAverageGreyScale = getAverageGreyScale(textureImage, maskImage);
        for (int x = 0; x < textureWidth; x++) {
            for (int y = 0; y < textureHeight; y++) {
                textureImage.setColor(x, y, ProcessPixel(textureImage.getColor(x, y), maskImage.getColor(x, y), colorSetting, MaskLayerAverageGreyScale));
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
