package net.onixary.shapeShifterCurseFabric.integration.toughasnails;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;

public class ToughAsNailsTemperatureIntegration {
    private static boolean registered = false;

    public static void registerPlayerTemperatureModifier() {
        if (registered) {
            return;
        }
        registered = true;
        TemperatureHelper.registerPlayerTemperatureModifier(ToughAsNailsTemperatureIntegration::modifyTemperature);
        ShapeShifterCurseFabric.LOGGER.info("Registered Tough As Nails form temperature modifier");
    }

    private static TemperatureLevel modifyTemperature(PlayerEntity player, TemperatureLevel current) {
        int modifiedOrdinal = ToughAsNailsPowerUtils.modifyFormTemperatureOrdinal(player, current.ordinal());
        return TemperatureLevel.values()[modifiedOrdinal];
    }
}
