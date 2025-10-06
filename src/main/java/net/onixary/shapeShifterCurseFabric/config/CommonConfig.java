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

    @ConfigEntry.Category("Patron")
    @Comment("Enable Patron Form System. Default: true")
    public boolean enablePatronFormSystem = true;

    @ConfigEntry.Category("Patron")
    @Comment("Data Pack Version Url. Default: ")
    public String DataPackVersionUrl = "http://localhost:1234/data_version.txt";  // 数据包版本存储URL

    @ConfigEntry.Category("Patron")
    @Comment("Data Pack Download Url. Default: ")
    public String DataPackUrl = "http://localhost:1234/data.zip";  // 数据包下载URL

    @ConfigEntry.Category("Patron")
    @Comment("Resource Pack Version Url. Default: ")
    public String ResourcePackVersionUrl = "http://localhost:1234/resource_version.txt";  // 资源包版本存储URL

    @ConfigEntry.Category("Patron")
    @Comment("Resource Pack Download Url. Default: ")
    public String ResourcePackUrl = "http://localhost:1234/resource.zip";  // 资源包下载URL

    @ConfigEntry.Category("Patron")
    @Comment("Patron Data Url. Default: ")
    public String PatronDataUrl = "http://localhost:1234/patron_data.json";  // 捐助者数据存储URL

    @ConfigEntry.Category("Patron")
    @Comment("Check Update Interval (Seconds). Default: 86400 (1 Day)")
    public int CheckUpdateInterval = 60 * 60 * 24;

    // 开发用
    // @ConfigEntry.Category("InDevelopment")
}
