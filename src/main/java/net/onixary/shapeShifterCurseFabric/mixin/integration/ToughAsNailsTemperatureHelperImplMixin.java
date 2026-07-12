package net.onixary.shapeShifterCurseFabric.mixin.integration;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.integration.toughasnails.ToughAsNailsPowerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.temperature.TemperatureHelperImpl;

@Mixin(value = TemperatureHelperImpl.class, remap = false)
public class ToughAsNailsTemperatureHelperImplMixin {
    @ModifyReturnValue(method = "armorModifier", at = @At("RETURN"), remap = false)
    private static TemperatureLevel shapeShifterCurseFabric$modifyArmorLayerTemperature(TemperatureLevel original, PlayerEntity player, TemperatureLevel current) {
        int modifiedOrdinal = ToughAsNailsPowerUtils.modifyFormTemperatureOrdinal(player, original.ordinal());
        return TemperatureLevel.values()[modifiedOrdinal];
    }
}
