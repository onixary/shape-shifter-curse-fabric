package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.onixary.shapeShifterCurseFabric.additional_power.ModifyInstantDamagePower;
import net.onixary.shapeShifterCurseFabric.additional_power.ModifyInstantHealthPower;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(StatusEffect.class)
public class StatusEffectMixin {
    @Unique
    private boolean applyEffect(StatusEffect realThis, LivingEntity entity, @Nullable Entity source, @Nullable Entity attacker, int amplifier, double proximity, boolean IsInstantEffect) {
        // 返回值是是否覆盖了原版效果 如果返回false 还会执行原版效果
        int EffectValue = 4;
        float FinalValue;
        if (realThis == StatusEffects.INSTANT_HEALTH) {
            List<ModifyInstantHealthPower> PowerList = PowerHolderComponent.getPowers(entity, ModifyInstantHealthPower.class);
            if (PowerList.isEmpty()) {
                return false;
            }
            if (entity.isUndead()) {
                EffectValue = -6;
            }
            FinalValue = (float) (EffectValue << amplifier);
            if (IsInstantEffect) {
                FinalValue = (float) (proximity * FinalValue + 0.5);
            }
            for (ModifyInstantHealthPower power : PowerList) {
                FinalValue = power.ApplyMulScale(FinalValue);
            }
            applyEffectDamage(entity, source, attacker, IsInstantEffect, FinalValue);
            return true;
        }
        if (realThis == StatusEffects.INSTANT_DAMAGE)  {
            List<ModifyInstantDamagePower> PowerList = PowerHolderComponent.getPowers(entity, ModifyInstantDamagePower.class);
            if (PowerList.isEmpty()) {
                return false;
            }
            if (!entity.isUndead()) {
                EffectValue = -6;
            }
            FinalValue = (float) (EffectValue << amplifier);
            if (IsInstantEffect) {
                FinalValue = (float) (proximity * FinalValue + 0.5);
            }
            for (ModifyInstantDamagePower power : PowerList) {
                FinalValue = power.ApplyMulScale(FinalValue);
            }
            applyEffectDamage(entity, source, attacker, IsInstantEffect, FinalValue);
            return true;
        }
        return false;
    }

    @Unique
    private void applyEffectDamage(LivingEntity entity, @Nullable Entity source, @Nullable Entity attacker, boolean IsInstantEffect, float finalValue) {
        if (finalValue == 0.0f) {
            return;
        }
        else if (finalValue > 0.0f) {
            entity.heal(finalValue);
        } else {
            DamageSource damageSource = entity.getDamageSources().magic();
            if (IsInstantEffect && (source != null && attacker != null)) {
                damageSource = entity.getDamageSources().indirectMagic(source, attacker);
            }
            entity.damage(damageSource, -finalValue);
        }
    }

    @Inject(method = "applyUpdateEffect", at = @At("HEAD"), cancellable = true)
    private void applyUpdateEffect(LivingEntity entity, int amplifier, CallbackInfo ci) {
        StatusEffect realThis = (StatusEffect)(Object)this;
        if (applyEffect(realThis, entity, null, null, amplifier, 0.0, false)) {
            ci.cancel();
        }
    }

    @Inject(method = "applyInstantEffect", at = @At("HEAD"), cancellable = true)
    private void applyInstantEffect(Entity source, Entity attacker, LivingEntity target, int amplifier, double proximity, CallbackInfo ci) {
        StatusEffect realThis = (StatusEffect)(Object)this;
        if (applyEffect(realThis, target, source, attacker, amplifier, proximity, true)) {
            ci.cancel();
        }
    }
}