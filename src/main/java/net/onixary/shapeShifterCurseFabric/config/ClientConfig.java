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
    @Comment("Ignore noRender arm power. Default: false")
    public boolean ignoreNoRenderArmPower = false;  // 忽略不渲染手臂的Power

    @ConfigEntry.Category("General")
    @Comment("Use Bigger(2x) Start Book Interface. Default: false")
    public boolean newStartBookForBiggerScreen = false;  // 菜单缩放至少为4

    @ConfigEntry.Category("General")
    @Comment("Enable Auto Modify FPM Config. Default: true")
    public boolean enableChangeFPMConfig = true;  // 启用修改FPM配置

    // 开发用
    // @ConfigEntry.Category("InDevelopment")

    @ConfigEntry.Category("UI")
    @Comment("Instinct Bar Middle Position Type (1-9). Default: 8")
    public int instinctBarPosType = 8;  // 本能条中间位置类型 (1-9)

    @ConfigEntry.Category("UI")
    @Comment("Instinct Bar Position X Offset. Default: 100")
    public int instinctBarPosOffsetX = 100;  // 本能条中间 X 位置偏移量

    @ConfigEntry.Category("UI")
    @Comment("Instinct Bar Position Y Offset. Default: -9")
    public int instinctBarPosOffsetY = -9;  // 本能条中间 Y 位置偏移量

    @ConfigEntry.Category("UI")
    @Comment("ManaBar Middle Position Type (1-9). Default: 8")
    public int manaBarPosType = 8;  // 魔力条中间位置类型 (1-9)

    @ConfigEntry.Category("UI")
    @Comment("ManaBar Position X Offset. Default: 100")
    public int manaBarPosOffsetX = 100;  // 魔力条中间 X 位置偏移量

    @ConfigEntry.Category("UI")
    @Comment("ManaBar Position Y Offset. Default: -17")
    public int manaBarPosOffsetY = -17;  // 魔力条中间 Y 位置偏移量
}
