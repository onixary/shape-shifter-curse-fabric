package net.onixary.shapeShifterCurseFabric.player_form.skin;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;

import java.util.OptionalInt;

public class PlayerSkinComponent implements Component, AutoSyncedComponent {
    private boolean keepOriginalSkin = false;
    private boolean enableFormColor = false;
    private FormTextureUtils.ColorSetting formColor = new FormTextureUtils.ColorSetting(0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFF000000, 0xFF000000, false, false, false);

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

    public void setFormColor(int primaryColorRGBA, int accentColor1RGBA, int accentColor2RGBA, int eyeColorA, int eyeColorB, boolean primaryGreyReverse, boolean accent1GreyReverse, boolean accent2GreyReverse) {
        this.formColor = new FormTextureUtils.ColorSetting(FormTextureUtils.RGBA2ABGR(primaryColorRGBA), FormTextureUtils.RGBA2ABGR(accentColor1RGBA), FormTextureUtils.RGBA2ABGR(accentColor2RGBA), FormTextureUtils.RGBA2ABGR(eyeColorA), FormTextureUtils.RGBA2ABGR(eyeColorB), primaryGreyReverse, accent1GreyReverse, accent2GreyReverse);
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

    public boolean setFormColor(String primaryColorRGBAHex, String accentColor1RGBAHex, String accentColor2RGBAHex, String eyeColorAHex, String eyeColorBHex, boolean primaryGreyReverse, boolean accent1GreyReverse, boolean accent2GreyReverse) {
        // FFE189 FBD972 F0AD32
        OptionalInt primaryColorRGBA = RGBA_Str2RGBA(primaryColorRGBAHex);
        OptionalInt accentColor1RGBA = RGBA_Str2RGBA(accentColor1RGBAHex);
        OptionalInt accentColor2RGBA = RGBA_Str2RGBA(accentColor2RGBAHex);
        OptionalInt eyeColorA = RGBA_Str2RGBA(eyeColorAHex);
        OptionalInt eyeColorB = RGBA_Str2RGBA(eyeColorBHex);
        if (primaryColorRGBA.isPresent() && accentColor1RGBA.isPresent() && accentColor2RGBA.isPresent() && eyeColorA.isPresent() && eyeColorB.isPresent()) {
            setFormColor(primaryColorRGBA.getAsInt(), accentColor1RGBA.getAsInt(), accentColor2RGBA.getAsInt(), eyeColorA.getAsInt(), eyeColorB.getAsInt(), primaryGreyReverse, accent1GreyReverse, accent2GreyReverse);
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
            this.formColor = new FormTextureUtils.ColorSetting(FormTextureUtils.RGBA2ABGR(tag.getInt("PrimaryColor")), FormTextureUtils.RGBA2ABGR(tag.getInt("AccentColor1")), FormTextureUtils.RGBA2ABGR(tag.getInt("AccentColor2")), FormTextureUtils.RGBA2ABGR(tag.getInt("EyeColorA")), FormTextureUtils.RGBA2ABGR(tag.getInt("EyeColorB")),
                    tag.getBoolean("PrimaryGreyReverse"), tag.getBoolean("Accent1GreyReverse"), tag.getBoolean("Accent2GreyReverse"));
        }
        catch(IllegalArgumentException e)
        {
            this.keepOriginalSkin = false; // Default to false
            this.enableFormColor = false; // Default to false
            this.formColor = new FormTextureUtils.ColorSetting(0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, false, false, false); // Default to default color
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("KeepOriginalSkin", this.keepOriginalSkin);
        tag.putBoolean("EnableFormColor", this.enableFormColor);
        tag.putInt("PrimaryColor", FormTextureUtils.ABGR2RGBA(this.formColor.getPrimaryColor()));
        tag.putInt("AccentColor1", FormTextureUtils.ABGR2RGBA(this.formColor.getAccentColor1()));
        tag.putInt("AccentColor2", FormTextureUtils.ABGR2RGBA(this.formColor.getAccentColor2()));
        tag.putInt("EyeColorA", FormTextureUtils.ABGR2RGBA(this.formColor.getEyeColorA()));
        tag.putInt("EyeColorB", FormTextureUtils.ABGR2RGBA(this.formColor.getEyeColorB()));
        tag.putBoolean("PrimaryGreyReverse", this.formColor.getPrimaryGreyReverse());
        tag.putBoolean("Accent1GreyReverse", this.formColor.getAccent1GreyReverse());
        tag.putBoolean("Accent2GreyReverse", this.formColor.getAccent2GreyReverse());
    }
}