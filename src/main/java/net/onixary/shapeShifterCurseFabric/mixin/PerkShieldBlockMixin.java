package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnShieldBlockPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PerkShieldBlockMixin {
    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"))
    private void ssc$shieldResponse(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ActionOnShieldBlockPower.onBlock((LivingEntity) (Object) this, source);
    }
}
