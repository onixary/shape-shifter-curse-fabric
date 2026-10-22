package net.onixary.shapeShifterCurseFabric.mixin.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.AdditionalPowers;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.util.ArrowAimUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(AbstractSkeletonEntity.class)
public abstract class ScareSkeletonMixin extends HostileEntity implements RangedAttackMob {
    protected ScareSkeletonMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(DDDFF)V"))
    private void aimAtFeralBody(PersistentProjectileEntity arrow, double x, double y, double z,
                               float speed, float divergence, LivingEntity target, float pullProgress) {
        if (target instanceof PlayerEntity player
                && FormUtils.getPlayerForm(player).getBodyType() == PlayerFormBodyType.FERAL) {
            var direction = ArrowAimUtils.launchDirection(target.getBoundingBox().getCenter().subtract(arrow.getPos()), speed);
            if (direction != null) {
                x = direction.x;
                y = direction.y;
                z = direction.z;
            }
        }
        arrow.setVelocity(x, y, z, speed, divergence);
    }

    @Inject(at = @At("TAIL"), method = "initGoals")
    private void addGoals(CallbackInfo info) {
        Goal goal = new FleeEntityGoal<>(this, PlayerEntity.class, AdditionalPowers.SCARE_SKELETON::isActive, 3.0F, 1.0D, 1.2D, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
        this.goalSelector.add(3, goal);
        Set<PrioritizedGoal> goals = this.targetSelector.getGoals();
        for (PrioritizedGoal prioritizedGoal : goals) {
            if (prioritizedGoal.getGoal() instanceof ActiveTargetGoal<?> atg && prioritizedGoal.getPriority() == 2 && atg.targetClass == PlayerEntity.class) {
                Predicate<LivingEntity> targetPredicate = atg.targetPredicate.predicate;
                if (targetPredicate == null) {
                    targetPredicate = e -> !AdditionalPowers.SCARE_SKELETON.isActive(e);
                } else {
                    targetPredicate = targetPredicate.and(e -> !AdditionalPowers.SCARE_SKELETON.isActive(e));
                }
                atg.targetPredicate.setPredicate(targetPredicate);
            }
        }
    }
}
