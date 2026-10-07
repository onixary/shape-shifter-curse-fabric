package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.AlwaysSweepingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.CriticalDamageModifierPower;
import net.onixary.shapeShifterCurseFabric.additional_power.EnhancedFallingAttackPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import java.util.List;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnCombatHitPower;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAttackMixin {

    @WrapOperation(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean ssc$successfulPrimaryHit(Entity target, DamageSource source, float amount,
                                             Operation<Boolean> original, @Local(ordinal = 2) boolean critical) {
        float before = ActionOnCombatHitPower.health(target);
        boolean success = original.call(target, source, amount + ActionOnCombatHitPower.meleeBonus((PlayerEntity) (Object) this, target, critical));
        if (success) {
            float dealt = before - ActionOnCombatHitPower.health(target);
            PlayerEntity player = (PlayerEntity) (Object) this;
            ActionOnCombatHitPower.fire(player, target, "melee", dealt);
            if (critical) ActionOnCombatHitPower.fire(player, target, "critical", dealt);
            else ActionOnCombatHitPower.fire(player, target, "non_critical_melee", dealt);
        }
        return success;
    }

    /**
     * This mixin is used to force the sweeping attack effect when the AlwaysSweepingPower is active.
     * Used in ocelot_3 form.
     */
    @ModifyVariable(
            method = {"attack"},
            at = @At("STORE"),
            ordinal = 3
    )
    private boolean forceSweeping(boolean value) {

        PowerHolderComponent component = PowerHolderComponent.KEY.get(this);

        for (AlwaysSweepingPower power : component.getPowers(AlwaysSweepingPower.class)) {
            if (power.isActive()) {
                return true;
            }
        }
        return value;
    }

    // 直接改暴击的常量岂不是兼容性更好 对了 之前的版本会导致攻击附魔暴击伤害计算错误
    // 修改常量容易出现量子态(你观测(打断点 打日志(这个不是100%))就能生效 不观测就不生效) 而且日志里的值是正确的 但就是没生效(可能和常量优化有关系) 调了快1个小时了
    // @ModifyExpressionValue(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "CONSTANT", args = {"floatValue=1.5F"}))
    @ModifyVariable(method = "attack(Lnet/minecraft/entity/Entity;)V", ordinal = 0, at = @At(value = "STORE", ordinal = 2))
    private float modifyCritMultiplier(float critMultiplier, @Local(ordinal = 0, argsOnly = true) Entity target) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        float finalMultiplier = critMultiplier;
        List<CriticalDamageModifierPower> critModifierPowers = PowerHolderComponent.getPowers(player, CriticalDamageModifierPower.class);
        List<EnhancedFallingAttackPower> fallingAttackPowers = PowerHolderComponent.getPowers(player, EnhancedFallingAttackPower.class);
        for (CriticalDamageModifierPower power : critModifierPowers) {
            finalMultiplier *= power.getMultiplier();
            power.executeAction();
        }
        if (!fallingAttackPowers.isEmpty()) {
            // Multiple instances still apply the bonus once, using the strongest active power.
            float fallMultiplier = 1.0f;
            for (EnhancedFallingAttackPower power : fallingAttackPowers) {
                fallMultiplier = Math.max(fallMultiplier, power.getFallMultiplier(player.fallDistance));
            }
            finalMultiplier *= fallMultiplier;
            for (EnhancedFallingAttackPower power : fallingAttackPowers) {
                power.executeTargetAction(target);
                power.executeSelfAction();
            }
        }
        return finalMultiplier;
    }
}
