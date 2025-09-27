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

    public record ColorSetting(int primaryColor, int accentColor1, int accentColor2) { }

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

    public static int ColorMul(int ColorA, int ColorB, int Mask) {
        // Mask <= 0xFF
        int Div = 0xFF * 0xFF;
        int Result = 0;
        Result |= (int) (((ColorA & 0xFF000000) >> 24) * ((ColorA & 0xFF000000) >> 24) * Mask / Div) & 0xFF;  // R
        Result <<= 8;
        Result |= (int) (((ColorA & 0x00FF0000) >> 16) * ((ColorA & 0x00FF0000) >> 16) * Mask / Div) & 0xFF;  // G
        Result <<= 8;
        Result |= (int) (((ColorA & 0x0000FF00) >> 8) * ((ColorA & 0x0000FF00) >> 8) * Mask / Div) & 0xFF;  // B
        Result <<= 8;
        Result |= (int) (((ColorA & 0x000000FF)) * ((ColorA & 0x000000FF)) * Mask / Div) & 0xFF;  // A
        return Result;
    }

    public static int ProcessPixel(int Color, int Mask, ColorSetting colorSetting) {
        int A = Mask & 0xFF;
        int B = (Mask >> 8) & 0xFF;
        int G = (Mask >> 16) & 0xFF;
        int R = (Mask >> 24) & 0xFF;
        int Pixel = ColorMul(Color, colorSetting.primaryColor, R);
        Pixel = ColorMul(Pixel, colorSetting.accentColor1, G);
        Pixel = ColorMul(Pixel, colorSetting.accentColor2, B);
        return Pixel;
    }

    public static Identifier BakeTexture(Identifier texture, Identifier mask, ColorSetting colorSetting)  {
        if (texture == null || mask == null) return null;
        NativeImage textureImage = toNativeImage(texture);
        NativeImage maskImage = toNativeImage(mask);
        int textureWidth = textureImage.getWidth();
        int textureHeight = textureImage.getHeight();
        // TODO Process Texture
        for (int x = 0; x < textureWidth; x++) {
            for (int y = 0; y < textureHeight; y++) {
                textureImage.setColor(x, y, ProcessPixel(textureImage.getColor(x, y), maskImage.getColor(x, y), colorSetting));
            }
        }
        TextureManager TM = MinecraftClient.getInstance().getTextureManager();
        TM.registerDynamicTexture("masked_texture_", new NativeImageBackedTexture(textureImage));
        return null;
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
