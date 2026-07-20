package net.onixary.shapeShifterCurseFabric.mixin.integration;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.integration.toughasnails.ToughAsNailsPowerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import toughasnails.api.thirst.IThirst;
import toughasnails.thirst.ThirstHooks;

@Mixin(value = ThirstHooks.class, remap = false)
public class ToughAsNailsThirstHooksMixin {
    @WrapOperation(
            method = "onCauseFoodExhaustion",
            at = @At(
                    value = "INVOKE",
                    target = "Ltoughasnails/api/thirst/IThirst;addExhaustion(F)V",
                    remap = false
            ),
            remap = false
    )
    private static void shapeShifterCurseFabric$modifyThirstExhaustion(IThirst thirst, float exhaustion, Operation<Void> original, @Local(argsOnly = true) PlayerEntity player) {
        original.call(thirst, ToughAsNailsPowerUtils.modifyThirstExhaustion(player, exhaustion));
    }
}
