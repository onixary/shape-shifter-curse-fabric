package net.onixary.shapeShifterCurseFabric.mixin.integration;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.integration.toughasnails.ToughAsNailsPowerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import toughasnails.api.thirst.IThirst;
import toughasnails.thirst.ThirstHooks;

@Mixin(value = ThirstHooks.class, remap = false)
public class ToughAsNailsThirstHooksMixin {
    @Redirect(
            method = "onCauseFoodExhaustion",
            at = @At(
                    value = "INVOKE",
                    target = "Ltoughasnails/api/thirst/IThirst;addExhaustion(F)V",
                    remap = false
            ),
            remap = false
    )
    private static void shapeShifterCurseFabric$modifyThirstExhaustion(IThirst thirst, float exhaustion, PlayerEntity player, float originalExhaustion) {
        thirst.addExhaustion(ToughAsNailsPowerUtils.modifyThirstExhaustion(player, exhaustion));
    }
}
