package net.onixary.shapeShifterCurseFabric.integration.tan;

import net.fabricmc.loader.api.FabricLoader;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;

// 必须加载 Tough as Nails 后才能加载此class
public class TANUtils {
    static {
        if (!FabricLoader.getInstance().isModLoaded("toughasnails")) {
            // 当未加载TAN时触发 按理说应该永远不会触发
            throw new IllegalStateException("Tough as Nails is not loaded!");
        }

        // 温度抗性 -1~2点
        TemperatureHelper.registerPlayerTemperatureModifier(((playerEntity, temperatureLevel) -> {
            int hotResistance = TANHelper.HotResistance.get(playerEntity);
            int coldResistance = TANHelper.ColdResistance.get(playerEntity);
            int temperatureResistance = TANHelper.TemperatureResistance.get(playerEntity);
            int temperatureOffset = TANHelper.TemperatureOffset.get(playerEntity);
            int resistance = temperatureResistance;

            TemperatureLevel current = temperatureLevel.increment(temperatureOffset);
            if (current == TemperatureLevel.WARM || current == TemperatureLevel.HOT) {
                resistance += hotResistance;
                current = current.decrement(resistance);
                if (current == TemperatureLevel.COLD || current == TemperatureLevel.ICY) {
                    current = TemperatureLevel.NEUTRAL;
                }
            } else if (current == TemperatureLevel.COLD || current == TemperatureLevel.ICY) {
                resistance += coldResistance;
                current = current.increment(resistance);
                if (current == TemperatureLevel.WARM || current == TemperatureLevel.HOT) {
                    current = TemperatureLevel.NEUTRAL;
                }
            }
            return current;
        }));
    }

    public static void init() {

    }
}
