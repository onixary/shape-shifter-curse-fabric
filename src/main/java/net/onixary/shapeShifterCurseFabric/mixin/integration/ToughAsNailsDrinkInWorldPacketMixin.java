package net.onixary.shapeShifterCurseFabric.mixin.integration;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.integration.toughasnails.ToughAsNailsPowerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import toughasnails.network.DrinkInWorldPacket;

@Mixin(value = DrinkInWorldPacket.class, remap = false)
public class ToughAsNailsDrinkInWorldPacketMixin {
    @Redirect(
            method = "lambda$handle$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z",
                    remap = true
            ),
            remap = false
    )
    private static boolean shapeShifterCurseFabric$preventHandDrinkingThirstEffect(PlayerEntity player, StatusEffectInstance effect) {
        if (ToughAsNailsPowerUtils.shouldPreventDirtyWaterThirstEffect(player)) {
            return false;
        }
        return player.addStatusEffect(effect);
    }
}
