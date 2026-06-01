package net.onixary.shapeShifterCurseFabric.mixin.mob;

import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.AdditionalPowers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(CreeperEntity.class)
public abstract class ScareCreeperMixin extends HostileEntity implements SkinOverlayOwner {
    protected ScareCreeperMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "initGoals")
    private void addGoals(CallbackInfo info) {
        Goal goal = new FleeEntityGoal<>(this, PlayerEntity.class, AdditionalPowers.SCARE_CREEPERS::isActive, 3.0F, 1.0D, 1.2D, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
        this.goalSelector.add(3, goal);
        Set<PrioritizedGoal> goals = this.targetSelector.getGoals();
        for (PrioritizedGoal prioritizedGoal : goals) {
            if (prioritizedGoal.getGoal() instanceof ActiveTargetGoal<?> atg && prioritizedGoal.getPriority() == 1 && atg.targetClass == PlayerEntity.class) {
                Predicate<LivingEntity> targetPredicate = atg.targetPredicate.predicate;
                if (targetPredicate == null) {
                    targetPredicate = e -> !AdditionalPowers.SCARE_CREEPERS.isActive(e);
                } else {
                    targetPredicate = targetPredicate.and(e -> !AdditionalPowers.SCARE_CREEPERS.isActive(e));
                }
                atg.targetPredicate.setPredicate(targetPredicate);
            }
        }
    }
}
