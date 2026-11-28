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
    @ConfigEntry.Gui.Tooltip
    public float transformativeBatSpawnChance = 0.5f;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public float transformativeAxolotlSpawnChance = 0.5f;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public float transformativeOcelotSpawnChance = 0.5f;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public float transformativeWolfSpawnChance = 0.5f;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public float transformativeSpiderSpawnChance = 0.5f;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public int[] curseMoonPhase = {1, 5};

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public boolean allowSleepInCursedMoon = false;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public boolean enableCursedMoonTransform = true;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public boolean disableAllDebug = false;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public boolean enableDebugCommand = false;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public boolean enableFullStackUpgrade = true;  // 防止一些Mod冲突导致刷物品Bug

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public boolean enableFoodHabitSystem = true;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public boolean immediatelyTransform = false;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public boolean enableInitialForm = false;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public String[] initialFormIds = {"shape-shifter-curse:original_before_enable:1"};

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public boolean statusPotionWithCurse = false;

    @ConfigEntry.Category("General")
    @ConfigEntry.Gui.Tooltip
    public boolean witchPotionForPreBook = false;

    @ConfigEntry.Category("Patron")
    @ConfigEntry.Gui.Tooltip
    public boolean enablePatronFormSystem = true;

    @ConfigEntry.Category("Patron")
    @ConfigEntry.Gui.Tooltip
    public String DataPackVersionUrl = "http://localhost:1234/data_version.txt";  // 数据包版本存储URL

    @ConfigEntry.Category("Patron")
    @ConfigEntry.Gui.Tooltip
    public String DataPackUrl = "http://localhost:1234/data.zip";  // 数据包下载URL

    @ConfigEntry.Category("Patron")
    @ConfigEntry.Gui.Tooltip
    public String ResourcePackVersionUrl = "http://localhost:1234/resource_version.txt";  // 资源包版本存储URL

    @ConfigEntry.Category("Patron")
    @ConfigEntry.Gui.Tooltip
    public String ResourcePackUrl = "http://localhost:1234/resource.zip";  // 资源包下载URL

    @ConfigEntry.Category("Patron")
    @ConfigEntry.Gui.Tooltip
    public String PatronDataUrl = "http://localhost:1234/patron_data.json";  // 捐助者数据存储URL

    @ConfigEntry.Category("Patron")
    @ConfigEntry.Gui.Tooltip
    public int CheckUpdateInterval = 60 * 60 * 24;

    // 开发用
    // @ConfigEntry.Category("InDevelopment")
}
