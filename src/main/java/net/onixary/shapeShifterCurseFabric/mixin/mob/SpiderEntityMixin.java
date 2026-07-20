package net.onixary.shapeShifterCurseFabric.mixin.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.AdditionalPowers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(SpiderEntity.class)
public class SpiderEntityMixin extends HostileEntity {
    protected SpiderEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "initGoals")
    private void addGoals(CallbackInfo info) {
        Set<PrioritizedGoal> goals = this.targetSelector.getGoals();
        for (PrioritizedGoal prioritizedGoal : goals) {
            if (prioritizedGoal.getGoal() instanceof ActiveTargetGoal<?> atg && prioritizedGoal.getPriority() == 2 && atg.targetClass == PlayerEntity.class) {
                Predicate<LivingEntity> targetPredicate = atg.targetPredicate.predicate;
                if (targetPredicate == null) {
                    targetPredicate = e -> !AdditionalPowers.SPIDER_FRIENDLY.isActive(e);
                } else {
                    targetPredicate = targetPredicate.and(e -> !AdditionalPowers.SPIDER_FRIENDLY.isActive(e));
                }
                atg.targetPredicate.setPredicate(targetPredicate);
            }
        }
    }
}
