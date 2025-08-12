package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.DamageTypeTags;
import net.onixary.shapeShifterCurseFabric.additional_power.BurnDamageModifierPower;
import net.onixary.shapeShifterCurseFabric.additional_power.EnhancedFallingAttackPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityBurnDamageMixin {

    @ModifyArg(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"), index = 1)
    private float modifyBurnDamage(DamageSource source, float amount) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.isOnFire() && (source.isIn(DamageTypeTags.IS_FIRE)
                && !entity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)))
        {
            List<BurnDamageModifierPower> powers = PowerHolderComponent.getPowers(entity, BurnDamageModifierPower.class);
            float totalModifier = powers
                    .stream()
                    .map(BurnDamageModifierPower::getDamageModifier)
                    .reduce(0f, Float::sum);

            powers.forEach(power -> {
                if (power.isActive()) {
                    power.executeAction(entity);
                }
            });

            return amount + totalModifier;
        }
        return amount;
    }
}
