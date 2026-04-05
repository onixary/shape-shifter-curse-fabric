package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.TStatusApplier;

import java.util.Optional;

public interface ITMob {
    public float getStatusChance();
    public BaseTransformativeStatusEffect getStatusEffect();
    public void TickCooldown();
    public void ApplyCooldown();
    public boolean IsInCooldown();

    public default void TMob_Tick(MobEntity TMob) {
        TickCooldown();

        LivingEntity target = TMob.getTarget();
        if (target instanceof PlayerEntity && !this.IsInCooldown()) {
            PlayerEntity player = (PlayerEntity) target;

            double distance = TMob.squaredDistanceTo(player);
            if (distance <= StaticParams.CUSTOM_MOB_DEFAULT_ATTACK_RANGE * StaticParams.CUSTOM_MOB_DEFAULT_ATTACK_RANGE) {
                TMob.tryAttack(player);
                TStatusApplier.applyStatusByChance(this.getStatusChance(), player, this.getStatusEffect());
                this.ApplyCooldown();
            }
        }

        // 生成粒子效果
        if (TMob.getWorld().isClient) {
            for (int i = 0; i < 1; i++) {
                TMob.getWorld().addParticle(StaticParams.CUSTOM_MOB_DEFAULT_PARTICLE,
                        TMob.getX() + (TMob.getRandom().nextDouble() - 0.5) * 0.5,
                        TMob.getY() + TMob.getRandom().nextDouble() * 0.5,
                        TMob.getZ() + (TMob.getRandom().nextDouble() - 0.5) * 0.5,
                        0, 0, 0);
            }
        }
    }

    public default Optional<Boolean> TMob_TryAttack(MobEntity TMob, Entity target) {
        if(target instanceof PlayerEntity) {
            PlayerFormBase currentForm = target.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
            if (currentForm.equals(RegPlayerForms.ORIGINAL_SHIFTER)) {
                boolean attacked = target.damage(TMob.getDamageSources().mobAttack(TMob), (float)TMob.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
                if (attacked) {
                    TMob.applyDamageEffects(TMob, target);
                }
                return Optional.of(attacked);
            }
            return Optional.of(false);
        }
        return Optional.empty();
    }
}
