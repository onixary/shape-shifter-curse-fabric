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

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAttackMixin {

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
            float minFall = 1.0f;
            float maxFall = 2.0f;
            float minMultiplier = 1.0f;
            float maxMultiplier = 2.0f;
            float fallMultiplier;
            if (player.fallDistance <= minFall) {
                fallMultiplier = minMultiplier;
            } else if (player.fallDistance >= maxFall) {
                fallMultiplier = maxMultiplier;
            } else {
                float progress = (player.fallDistance - minFall) / (maxFall - minFall);
                fallMultiplier = minMultiplier + (maxMultiplier - minMultiplier) * progress;
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
