package net.onixary.shapeShifterCurseFabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "shape-shifter-curse-custom")
public class PlayerCustomConfig implements ConfigData {
    @Comment("Enable Auto Sync Config. Default: true")
    public boolean auto_sync_config = true;

    @Comment("Keep original skin. Default: false")
    public boolean keep_original_skin = false;

    @Comment("Enable form color. Default: false")
    public boolean enable_form_color = false;
    @ConfigEntry.ColorPicker(allowAlpha = true)
    @Comment("Primary color (ARGB). Default: white")
    public int primaryColor = 0xFFFFFFFF;
    @ConfigEntry.ColorPicker(allowAlpha = true)
    @Comment("Accent color 1 (ARGB). Default: white")
    public int accentColor1Color = 0xFFFFFFFF;
    @ConfigEntry.ColorPicker(allowAlpha = true)
    @Comment("Accent color 2 (ARGB). Default: white")
    public int accentColor2Color = 0xFFFFFFFF;
    @ConfigEntry.ColorPicker(allowAlpha = true)
    @Comment("Eye color A (ARGB). Default: black")
    public int eyeColorA = 0xff000000;
    @ConfigEntry.ColorPicker(allowAlpha = true)
    @Comment("Eye color B (ARGB). Default: black")
    public int eyeColorB = 0xff000000;

    // @Comment("Primary color override grey strength (0~255). Default: 0")
    // @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    // public int primaryOverrideStrength = 0;
    // @Comment("Accent color 1 override grey strength (0~255). Default: 0")
    // @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    // public int accent1OverrideStrength = 0;
    // @Comment("Accent color 2 override grey strength (0~255). Default: 0")
    // @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
    // public int accent2OverrideStrength = 0;

    @Comment("Primary color reverse grey scale mul. Default: false")
    public boolean primaryGreyReverse = false;
    @Comment("Accent color 1 reverse grey scale mul. Default: false")
    public boolean accent1GreyReverse = false;
    @Comment("Accent color 2 reverse grey scale mul. Default: false")
    public boolean accent2GreyReverse = false;
}
