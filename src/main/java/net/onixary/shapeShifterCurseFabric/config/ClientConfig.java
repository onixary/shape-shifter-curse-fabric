package net.onixary.shapeShifterCurseFabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "shape-shifter-curse-client")
public class ClientConfig implements ConfigData {
    public ClientConfig() {}

    // Comment 不知道如何本地化
    @Comment("Enable form model on vanilla first person render Default: true")
    public boolean enableFormModelOnVanillaFirstPersonRender = true;  // 原版第一人称下启用形态模型渲染
}
