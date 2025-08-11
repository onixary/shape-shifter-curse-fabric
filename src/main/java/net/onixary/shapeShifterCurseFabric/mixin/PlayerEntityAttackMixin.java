package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.AlwaysSweepingPower;
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
        ShapeShifterCurseFabric.LOGGER.info("1: ");
        if (!isCrit) {
            return target.damage(source, amount);
        }

        PlayerEntity player = (PlayerEntity) (Object) this;
        ShapeShifterCurseFabric.LOGGER.info("2: ");
        List<EnhancedFallingAttackPower> powers = PowerHolderComponent.getPowers(player, EnhancedFallingAttackPower.class);
        if (powers.stream().noneMatch(p -> p.isActive())) {
            return target.damage(source, amount);
        }

        float minFall = 1.0f;
        float maxFall = 2.0f;
        float minMultiplier = 1.0f;
        float maxMultiplier = 2.0f;

        float multiplier;
        if (player.fallDistance <= minFall) {
            multiplier = minMultiplier;
        } else if (player.fallDistance >= maxFall) {
            multiplier = maxMultiplier;
        } else {
            // 在 [minFall, maxFall] 区间内进行线性插值
            float progress = (player.fallDistance - minFall) / (maxFall - minFall);
            multiplier = minMultiplier + (maxMultiplier - minMultiplier) * progress;
        }

        powers.forEach(power -> {
            if (power.isActive()) {
                power.executeTargetAction(target);
                power.executeSelfAction();
            }
        });

        float baseDamage = amount / 1.5f;
        //ShapeShifterCurseFabric.LOGGER.info("baseDamage: " + baseDamage);
        //ShapeShifterCurseFabric.LOGGER.info("fall distance: " + player.fallDistance);
        //ShapeShifterCurseFabric.LOGGER.info("enhanced crit damage: " + baseDamage * 1.5f * multiplier);

        return target.damage(source, baseDamage * 1.5f * multiplier);
    }
}
