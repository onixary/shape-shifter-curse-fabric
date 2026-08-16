package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.AdditionalPowers;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ITMob;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.TO_OCELOT_0_EFFECT;

public class TransformativeOcelotEntity extends OcelotEntity implements ITMob {
    public FleeGoalModified<PlayerEntity> fleeGoal;

    public TransformativeOcelotEntity(EntityType<? extends OcelotEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, StaticParams.CUSTOM_MOB_DEFAULT_DAMAGE)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3);
    }

    public static boolean canCustomSpawn(EntityType<TransformativeOcelotEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        float Chance = ShapeShifterCurseFabric.commonConfig.transformativeOcelotSpawnChance;
        if (Chance <= 0.0f) { return false; }
        if (Chance >= 1.0f) { return true; }
        return random.nextFloat() < Chance;
    }

    @Override
    public float getStatusChance() {
        return 0.5f;
    }

    @Override
    public BaseTransformativeStatusEffect getStatusEffect() {
        return TO_OCELOT_0_EFFECT;
    }

    @Override
    public void tick() {
        super.tick();
        this.TMob_Tick(this);
    }

    @Override
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        // 在applyStatusByChance里面已经判断形态了 无需在外面判断
        if (target instanceof PlayerEntity player) {
            ITMob.applyStatusByChance(this.getStatusChance(), player, this.getStatusEffect());
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = super.tryAttack(target);
        if (bl) {
            this.applyDamageEffects(this, target);
        }
        return bl;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, entity -> entity instanceof PlayerEntity player && RegPlayerForms.ORIGINAL_SHIFTER.isPlayerForm(player)));
    }

    public static final Predicate<LivingEntity> FLEE_PREDICATE = (entity) -> {
        if (entity instanceof PlayerEntity player) {
            if (RegPlayerForms.ORIGINAL_SHIFTER.isPlayerForm(player)) {
                return false;
            }
            if (AdditionalPowers.CAT_FRIENDLY.isActive(player)) {
                return false;
            }
        }
        return true;
    };

    @Override
    protected void updateFleeing() {
        if (this.fleeGoal == null) {
            this.fleeGoal = new FleeGoalModified<PlayerEntity>(this, PlayerEntity.class, 16.0F, 0.8, 1.33, FLEE_PREDICATE);
        }

        this.goalSelector.remove(this.fleeGoal);
        if (!this.isTrusting()) {
            this.goalSelector.add(4, this.fleeGoal);
        }
    }

    public static class FleeGoalModified<T extends LivingEntity> extends FleeEntityGoal<T> {
        private final TransformativeOcelotEntity ocelot;

        public FleeGoalModified(TransformativeOcelotEntity ocelot, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed, Predicate<LivingEntity> predicate) {
            super(ocelot, fleeFromType, distance, slowSpeed, fastSpeed, predicate);
            this.ocelot = ocelot;
        }

        public boolean canStart() {
            return !this.ocelot.isTrusting() && super.canStart();
        }

        public boolean shouldContinue() {
            return !this.ocelot.isTrusting() && super.shouldContinue();
        }
    }
}
