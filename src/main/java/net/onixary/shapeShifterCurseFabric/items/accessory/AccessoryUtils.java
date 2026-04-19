package net.onixary.shapeShifterCurseFabric.items.accessory;

import net.fabricmc.loader.api.FabricLoader;

public class AccessoryUtils {
    public static final boolean LOADED_Trinkets = FabricLoader.getInstance().isModLoaded("trinkets");
    public static final boolean LOADED_Curios = FabricLoader.getInstance().isModLoaded("curios");
}
