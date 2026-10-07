package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.onixary.shapeShifterCurseFabric.additional_power.FrostDivePower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class FrostFallDamageMixin {
    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void frostFall(float distance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (FrostDivePower.isDiving((LivingEntity) (Object) this)) cir.setReturnValue(false);
    }
}
