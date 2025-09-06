package net.onixary.shapeShifterCurseFabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

// 客户端配置 (仅客户端加载)
@Config(name = "shape-shifter-curse-client")
public class ClientConfig implements ConfigData {
    public ClientConfig() {}

    // Comment 不知道如何本地化
    @ConfigEntry.Category("General")
    @Comment("Enable form model on vanilla first person render. Default: true")
    public boolean enableFormModelOnVanillaFirstPersonRender = true;  // 原版第一人称下启用形态模型渲染

    @ConfigEntry.Category("General")
    @Comment("Use Bigger(2x) Start Book Interface. Default: false")
    public boolean newStartBookForBiggerScreen = false;  // 菜单缩放至少为4

    // 开发用
    // @ConfigEntry.Category("InDevelopment")
}
