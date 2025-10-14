package net.onixary.shapeShifterCurseFabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

// 双端配置
@Config(name = "shape-shifter-curse-common")
public class CommonConfig implements ConfigData {
    public CommonConfig() {}

    // Cloth Config 没有浮点的边界检查 只有整数的
    @ConfigEntry.Category("General")
    @Comment("Transformative Bat Spawn Chance, 0 For Disable Spawn. Default: 0.5f [0.0f ~ 1.0f]")
    public float transformativeBatSpawnChance = 0.5f;

    @ConfigEntry.Category("General")
    @Comment("Transformative Axolotl Spawn Chance, 0 For Disable Spawn. Default: 1.0f [0.0f ~ 1.0f]")
    public float transformativeAxolotlSpawnChance = 1.0f;

    @ConfigEntry.Category("General")
    @Comment("Transformative Ocelot Spawn Chance, 0 For Disable Spawn. Default: 0.67f [0.0f ~ 1.0f]")
    public float transformativeOcelotSpawnChance = 0.67f;

    @ConfigEntry.Category("General")
    @Comment("Use The New Start Book Interface. Default: true")
    public boolean enableNewStartBook = true;  // 新版启动书

    @ConfigEntry.Category("General")
    @Comment("Curse Moon Phase (0 ~ 7). Default: [1, 5]")
    public int[] curseMoonPhase = {1, 5};

    @ConfigEntry.Category("General")
    @Comment("Allow players to sleep during Cursed Moon. Default: false")
    public boolean allowSleepInCursedMoon = false;

    // 开发用
    // @ConfigEntry.Category("InDevelopment")
}
