package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.onixary.shapeShifterCurseFabric.additional_power.WitherIntervalPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StatusEffectInstance.class)
public abstract class WitherIntervalMixin {
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;canApplyUpdateEffect(II)Z"))
    private boolean ssc$witherInterval(StatusEffect effect, int duration, int amplifier, LivingEntity entity, Runnable callback) {
        boolean vanilla = effect.canApplyUpdateEffect(duration, amplifier);
        return effect == StatusEffects.WITHER ? WitherIntervalPower.shouldApply(entity, duration, amplifier, vanilla) : vanilla;
    }
}
