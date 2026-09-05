package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

// CV高手忘了怎么写了 从我很久以前的Mod CV的 自己借鉴自己属于是
public class RegMenuType {

    public static Identifier ALTER_CRAFT_UI_ID = ShapeShifterCurseFabric.identifier("alter_craft_ui");
    public static ScreenHandlerType<AlterCraftUIHandler> AlterCraftUI = register(ALTER_CRAFT_UI_ID, new ScreenHandlerType<>(AlterCraftUIHandler::createMenu, FeatureFlags.VANILLA_FEATURES));
    public static <T extends ScreenHandler> ScreenHandlerType<T> register(Identifier id, ScreenHandlerType<T> factory) {
        Registry.register(Registries.SCREEN_HANDLER, id, factory);
        return factory;
    }
}
