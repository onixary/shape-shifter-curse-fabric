package net.onixary.shapeShifterCurseFabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

// 双端配置
@Config(name = "shape-shifter-curse-common")
public class CommonConfig implements ConfigData {
    public CommonConfig() {}

    @ConfigEntry.Category("General")
    @Comment("PLACEHOLDER Default: false")
    public boolean generalConfigPlaceHolder = false;  // 占位符 添加新项目时请删除

    // 开发用
    @ConfigEntry.Category("InDevelopment")
    @Comment("PLACEHOLDER Default: false")
    public boolean inDevelopmentConfigPlaceHolder = false;  // 占位符 添加新项目时请删除
}
