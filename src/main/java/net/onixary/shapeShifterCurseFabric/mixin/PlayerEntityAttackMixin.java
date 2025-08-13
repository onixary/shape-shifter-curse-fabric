package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.AlwaysSweepingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.CriticalDamageModifierPower;
import net.onixary.shapeShifterCurseFabric.additional_power.EnhancedFallingAttackPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

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


    /**
     * 精确注入到 attack 方法中，在原版暴击伤害计算之后修改伤害值。
     * local variable `f` (float) at index 2.
     */
    @Redirect(
            method = "attack(Lnet/minecraft/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    private boolean modifyCritDamage(Entity target, DamageSource source, float amount, @Local(ordinal = 2) boolean isCrit) {
        if (!isCrit) {
            return target.damage(source, amount);
        }

        PlayerEntity player = (PlayerEntity) (Object) this;

        // 获取相关的 Power
        List<CriticalDamageModifierPower> critModifierPowers = PowerHolderComponent.getPowers(player, CriticalDamageModifierPower.class);
        List<EnhancedFallingAttackPower> fallingAttackPowers = PowerHolderComponent.getPowers(player, EnhancedFallingAttackPower.class);

        // 计算基础伤害（去除原版1.5倍跳劈倍数）
        float baseDamage = amount / 1.5f;

        // 第一步：应用跳劈伤害提升
        float critMultiplier = 1.5f; // 原版跳劈倍数
        for (CriticalDamageModifierPower power : critModifierPowers) {
            if (power.isActive()) {
                critMultiplier *= power.getMultiplier();
                power.executeAction();
                break; // 只使用第一个激活的 power
            }
        }

        float enhancedCritDamage = baseDamage * critMultiplier;

        // 第二步：检查是否有下落增伤 power
        boolean hasFallingAttackPower = fallingAttackPowers.stream().anyMatch(p -> p.isActive());

        if (hasFallingAttackPower) {
            // 计算下落增伤倍数
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
                // 在 [minFall, maxFall] 区间内进行线性插值
                float progress = (player.fallDistance - minFall) / (maxFall - minFall);
                fallMultiplier = minMultiplier + (maxMultiplier - minMultiplier) * progress;
            }

            // 执行下落攻击的 action
            fallingAttackPowers.forEach(power -> {
                if (power.isActive()) {
                    power.executeTargetAction(target);
                    power.executeSelfAction();
                }
            });

            // 应用下落增伤到已经提升的跳劈伤害上
            enhancedCritDamage = enhancedCritDamage * fallMultiplier;
        }

        return target.damage(source, enhancedCritDamage);
    }
}
