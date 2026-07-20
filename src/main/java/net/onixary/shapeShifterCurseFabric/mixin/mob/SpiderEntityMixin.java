package net.onixary.shapeShifterCurseFabric.mixin.mob;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.AdditionalPowers;
import net.onixary.shapeShifterCurseFabric.util.ActiveTargetGoalWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpiderEntity.class)
public class SpiderEntityMixin extends HostileEntity {
    protected SpiderEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 7), method = "initGoals")
    private void modifyTargetGoal(GoalSelector goalSelector, int priority, Goal goal, Operation<Void> original) {
        Goal newGoal = new ActiveTargetGoalWithCondition<>(this, PlayerEntity.class, 10, true, false, e -> !AdditionalPowers.SPIDER_FRIENDLY.isActive(e), e -> e.getBrightnessAtEyes() < 0.5f);
        original.call(goalSelector, priority, newGoal);
    }
}
