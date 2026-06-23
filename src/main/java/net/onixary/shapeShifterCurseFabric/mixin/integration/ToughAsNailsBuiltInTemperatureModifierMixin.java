package net.onixary.shapeShifterCurseFabric.mixin.integration;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.integration.toughasnails.ToughAsNailsPowerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.temperature.BuiltInTemperatureModifier;

@Mixin(value = BuiltInTemperatureModifier.class, remap = false)
public class ToughAsNailsBuiltInTemperatureModifierMixin {
    @ModifyExpressionValue(
            method = "lambda$static$2",
            at = @At(
                    value = "INVOKE",
                    target = "Ltoughasnails/temperature/TemperatureHelperImpl;armorModifier(Lnet/minecraft/entity/player/PlayerEntity;Ltoughasnails/api/temperature/TemperatureLevel;)Ltoughasnails/api/temperature/TemperatureLevel;",
                    remap = false
            ),
            remap = false
    )
    private static TemperatureLevel shapeShifterCurseFabric$modifyArmorLayerTemperature(TemperatureLevel armorModified, @Local(argsOnly = true) PlayerEntity player) {
        int modifiedOrdinal = ToughAsNailsPowerUtils.modifyFormTemperatureOrdinal(player, armorModified.ordinal());
        return TemperatureLevel.values()[modifiedOrdinal];
    }
}
