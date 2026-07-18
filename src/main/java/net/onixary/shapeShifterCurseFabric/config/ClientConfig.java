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
    @Comment("Minimum blink interval in ticks. Default: 60")
    public int minBlinkIntervalTick = 60;

    @ConfigEntry.Category("General")
    @Comment("Maximum blink interval in ticks. Default: 140")
    public int maxBlinkIntervalTick = 140;

    @ConfigEntry.Category("General")
    @Comment("Blink duration in ticks. Default: 4")
    public int blinkTick = 4;

    @ConfigEntry.Category("General")
    @Comment("Use Bigger(2x) Start Book Interface. Default: false")
    public boolean newStartBookForBiggerScreen = false;  // 菜单缩放至少为4

    @ConfigEntry.Category("General")
    @Comment("Enable Auto Modify FPM Config. Default: true")
    public boolean enableChangeFPMConfig = true;  // 启用修改FPM配置

    @ConfigEntry.Category("General")
    @Comment("Disable Unlock Check In Form Color Select Menu. Default: false")
    public boolean disableUnlockCheckInFormColorSelectMenu = false;  // 禁用颜色选择菜单中的解锁检查

    @ConfigEntry.Category("General")
    @Comment("Form Color Select Menu Use Professional Version. Default: false")
    public boolean fcs_use_v1_menu = false;  // 是否启用V1版本 那版由于Onixary说有点复杂 所以设计了标准版(V2) 我原本想扔拓展里 最后我决定加个专业版后缀放本体里

    @ConfigEntry.Category("Tool")
    @Comment("Unlock All Form For Form Color Select Menu. Default: false")
    public boolean unlockAllFormInFormColorSelectMenu = false;  // 解锁颜色选择菜单中的所有形态

    @ConfigEntry.Category("Tool")
    @Comment("Clear Form Unlock Record For Form Color Select Menu. Default: false")
    public boolean clearFormUnlockRecordInFormColorSelectMenu = false;  // 清除颜色选择菜单中的解锁记录

    @ConfigEntry.Category("Integration")
    @Comment("Enable Better Combat Fix. Default: true")
    public boolean enableBetterCombatFix = true;  // 启用Better Combat Fix

    @ConfigEntry.Category("Patron")
    @Comment("Auto Download Patron Authorization File. Default: true")
    public boolean autoDownloadPatronAuthorizationFile = true;  // 自动下载赞助者授权文件

    @ConfigEntry.Category("Patron")
    @Comment("Custom player UUID (client-side only). Addresses mismatches between offline UUIDs and server-computed ones. Default: \"\" ")
    public String customPlayerUUID = "";  // 仅供自动更新系统使用 实际使用哪个authFile由服务器决定

    @ConfigEntry.Category("Patron")
    @Comment("Patron Authorization Url Path Without Suffix(<UUID>.auth). Default: \"https://raw.githubusercontent.com/onixary/ssc_patron/main/auth/\"")
    public String patronAuthorizationUrlPath = "https://raw.githubusercontent.com/onixary/ssc_patron/main/auth/";  // 赞助者授权文件URL路径

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

    @ConfigEntry.Category("UI")
    @Comment("Item Store Power Middle Position Type (1-9). Default: 8")
    public int itemStorePowerPosType = 8;  // itemStorePower 中间位置类型 (1-9)

    @ConfigEntry.Category("UI")
    @Comment("Item Store Power Position X Offset. Default: -120")
    public int itemStorePowerPosOffsetX = -120;  // itemStorePower 中间 X 位置偏移量

    @ConfigEntry.Category("UI")
    @Comment("Item Store Power Position Y Offset. Default: 1")
    public int itemStorePowerPosOffsetY = 1;  // itemStorePower 中间 Y 位置偏移量
}
