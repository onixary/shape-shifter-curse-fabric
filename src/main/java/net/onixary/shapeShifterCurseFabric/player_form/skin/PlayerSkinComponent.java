package net.onixary.shapeShifterCurseFabric.player_form.skin;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;

import java.util.OptionalInt;

public class PlayerSkinComponent implements Component, AutoSyncedComponent {
    private boolean keepOriginalSkin = false;
    private boolean enableFormColor = false;
    private FormTextureUtils.ColorSetting formColor = new FormTextureUtils.ColorSetting(0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0.0f, 0.0f, 0.0f);

    public boolean shouldKeepOriginalSkin() {
        return keepOriginalSkin;
    }

    public boolean isEnableFormColor() {
        return enableFormColor;
    }

    public FormTextureUtils.ColorSetting getFormColor() {
        return formColor;
    }

    public void setKeepOriginalSkin(boolean keepOriginalSkin) {
        this.keepOriginalSkin = keepOriginalSkin;
    }

    public void setEnableFormColor(boolean enableFormColor) {
        this.enableFormColor = enableFormColor;
    }

    public void setFormColor(FormTextureUtils.ColorSetting formColor) {
        this.formColor = formColor;
    }

    public void setFormColor(int primaryColorRGBA, int accentColor1RGBA, int accentColor2RGBA, float primaryOverrideStrength, float accent1OverrideStrength, float accent2OverrideStrength) {
        this.formColor = new FormTextureUtils.ColorSetting(FormTextureUtils.RGBA2ABGR(primaryColorRGBA), FormTextureUtils.RGBA2ABGR(accentColor1RGBA), FormTextureUtils.RGBA2ABGR(accentColor2RGBA), primaryOverrideStrength, accent1OverrideStrength, accent2OverrideStrength);
    }

    public OptionalInt RGBA_Str2RGBA(String rgbaStr) {
        try {
            if (rgbaStr.length() == 6) {
                return OptionalInt.of(Integer.parseUnsignedInt(rgbaStr, 16) << 8 | 0xFF);
            } else if (rgbaStr.length() == 8) {
                return OptionalInt.of(Integer.parseUnsignedInt(rgbaStr, 16));
            }
            return OptionalInt.empty();
        }
        catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    public boolean setFormColor(String primaryColorRGBAHex, String accentColor1RGBAHex, String accentColor2RGBAHex
    , float primaryOverrideStrength, float accent1OverrideStrength, float accent2OverrideStrength) {
        // FFE189 FBD972 F0AD32
        OptionalInt primaryColorRGBA = RGBA_Str2RGBA(primaryColorRGBAHex);
        OptionalInt accentColor1RGBA = RGBA_Str2RGBA(accentColor1RGBAHex);
        OptionalInt accentColor2RGBA = RGBA_Str2RGBA(accentColor2RGBAHex);
        if (primaryColorRGBA.isPresent() && accentColor1RGBA.isPresent() && accentColor2RGBA.isPresent()) {
            setFormColor(primaryColorRGBA.getAsInt(), accentColor1RGBA.getAsInt(), accentColor2RGBA.getAsInt(), primaryOverrideStrength, accent1OverrideStrength, accent2OverrideStrength);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        try {
            this.keepOriginalSkin = tag.getBoolean("KeepOriginalSkin");
            this.enableFormColor = tag.getBoolean("EnableFormColor");
            this.formColor = new FormTextureUtils.ColorSetting(FormTextureUtils.RGBA2ABGR(tag.getInt("PrimaryColor")), FormTextureUtils.RGBA2ABGR(tag.getInt("AccentColor1")), FormTextureUtils.RGBA2ABGR(tag.getInt("AccentColor2"))
            , tag.getFloat("PrimaryOverrideStrength"), tag.getFloat("Accent1OverrideStrength"), tag.getFloat("Accent2OverrideStrength"));
        }
        catch(IllegalArgumentException e)
        {
            this.keepOriginalSkin = false; // Default to false
            this.enableFormColor = false; // Default to false
            this.formColor = new FormTextureUtils.ColorSetting(0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0.0f, 0.0f, 0.0f); // Default to default color
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("KeepOriginalSkin", this.keepOriginalSkin);
        tag.putBoolean("EnableFormColor", this.enableFormColor);
        tag.putInt("PrimaryColor", FormTextureUtils.ABGR2RGBA(this.formColor.getPrimaryColor()));
        tag.putInt("AccentColor1", FormTextureUtils.ABGR2RGBA(this.formColor.getAccentColor1()));
        tag.putInt("AccentColor2", FormTextureUtils.ABGR2RGBA(this.formColor.getAccentColor2()));
        tag.putFloat("PrimaryOverrideStrength", this.formColor.getPrimaryOverrideStrength());
        tag.putFloat("Accent1OverrideStrength", this.formColor.getAccent1OverrideStrength());
        tag.putFloat("Accent2OverrideStrength", this.formColor.getAccent2OverrideStrength());
    }
}
