package net.onixary.shapeShifterCurseFabric.integration.jei;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.NotImplementedException;

public class JEI_Utils {
    // 我还是比较喜欢用EMI 还是稍微整点兼容吧 但是加依赖又不太好
    public static final boolean isEMILoaded = FabricLoader.getInstance().isModLoaded("emi");
    public static final boolean isJEILoaded = FabricLoader.getInstance().isModLoaded("jei");

    public static void openJEIAltarRecipeScreen() {
        throw new NotImplementedException("Mixin is not loaded");
    }
}
