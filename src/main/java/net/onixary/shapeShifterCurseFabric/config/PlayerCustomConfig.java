package net.onixary.shapeShifterCurseFabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "shape-shifter-curse-custom")
public class PlayerCustomConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean auto_sync_config = true;

    @ConfigEntry.Gui.Tooltip
    public boolean enable_server_modify_FCD_config = true;  // 用于命令 是否运行服务器修改客户端形态颜色数据 在原版程序上只能由用户自身调用修改 但是可能存在修改版的服务端 加一下防护

    @ConfigEntry.Gui.Tooltip
    public boolean enable_form_default_color_system = true;  // 是否启用形态默认颜色系统 可以用命令/FCS来修改 仅当有数据时发送

    @ConfigEntry.Gui.Tooltip
    public boolean auto_sync_color_config = true;

    @ConfigEntry.Gui.Tooltip
    public boolean keep_original_skin = false;

    @ConfigEntry.Gui.Tooltip
    public boolean enable_form_color = false;
    @ConfigEntry.ColorPicker(allowAlpha = true)
    @ConfigEntry.Gui.Tooltip
    public int primaryColor = 0xFFFFFFFF;
    @ConfigEntry.ColorPicker(allowAlpha = true)
    @ConfigEntry.Gui.Tooltip
    public int accentColor1Color = 0xFFFFFFFF;
    @ConfigEntry.ColorPicker(allowAlpha = true)
    @ConfigEntry.Gui.Tooltip
    public int accentColor2Color = 0xFFFFFFFF;
    @ConfigEntry.ColorPicker(allowAlpha = true)
    @ConfigEntry.Gui.Tooltip
    public int eyeColorA = 0xff000000;
    @ConfigEntry.ColorPicker(allowAlpha = true)
    @ConfigEntry.Gui.Tooltip
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

    @ConfigEntry.Gui.Tooltip
    public boolean primaryGreyReverse = false;
    @ConfigEntry.Gui.Tooltip
    public boolean accent1GreyReverse = false;
    @ConfigEntry.Gui.Tooltip
    public boolean accent2GreyReverse = false;

    @ConfigEntry.Gui.Tooltip
    public boolean enable_form_random_sound = true;
}
