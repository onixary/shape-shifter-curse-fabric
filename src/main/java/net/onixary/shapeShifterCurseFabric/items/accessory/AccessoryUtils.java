package net.onixary.shapeShifterCurseFabric.items.accessory;

import net.fabricmc.loader.api.FabricLoader;

import java.util.Objects;

public class AccessoryUtils {
    public static final boolean LOADED_Trinkets = FabricLoader.getInstance().isModLoaded("trinkets");
    public static final boolean LOADED_Curios = FabricLoader.getInstance().isModLoaded("curios");

    // 突然想起来我的饰品系统似乎不能处理2个相同的饰品 那还是不做同时兼容吧 这样触发概率低一些 默认使用trinkets 如果没装就试试用curios 毕竟trinkets支持的好一些
    public static String calcAutoMod(String mod) {
        if (Objects.equals(mod, "auto")) {
            if (LOADED_Trinkets) {
                return "trinkets";
            } else if (LOADED_Curios) {
                return "curios";
            } else
                return "none";
        }
        return mod;
    }
}
