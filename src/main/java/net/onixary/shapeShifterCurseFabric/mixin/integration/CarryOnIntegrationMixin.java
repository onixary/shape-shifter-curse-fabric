package net.onixary.shapeShifterCurseFabric.mixin.integration;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.util.integration.CarryOnIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;

@Mixin(value = CarryOnIntegration.class, remap = false)
public class CarryOnIntegrationMixin {
    @Inject(method = "isInCarryingAnimation", at = @At("HEAD"), cancellable = true)
    private static void isInCarryingAnimation(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        CarryOnData carry = CarryOnDataManager.getCarryData(player);
        if(carry.isCarrying() && !player.isSwimming() && !player.isFallFlying()) {
            cir.setReturnValue(true);
        }
        return;
    }
}
