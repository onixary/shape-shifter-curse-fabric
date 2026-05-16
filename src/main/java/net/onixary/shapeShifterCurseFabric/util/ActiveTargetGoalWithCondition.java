package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ActiveTargetGoalWithCondition<T extends LivingEntity> extends ActiveTargetGoal<T> {
    public Predicate<MobEntity> condition;

    public ActiveTargetGoalWithCondition(MobEntity mob, Class<T> targetClass, boolean checkVisibility, Predicate<MobEntity> condition) {
        super(mob, targetClass, checkVisibility);
        this.condition = condition;
    }

    public ActiveTargetGoalWithCondition(MobEntity mob, Class<T> targetClass, boolean checkVisibility, Predicate targetPredicate, Predicate<MobEntity> condition) {
        super(mob, targetClass, checkVisibility, targetPredicate);
        this.condition = condition;
    }

    public ActiveTargetGoalWithCondition(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate, Predicate<MobEntity> condition) {
        super(mob, targetClass, checkVisibility, checkCanNavigate);
        this.condition = condition;
    }

    public ActiveTargetGoalWithCondition(MobEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate, Predicate<MobEntity> condition) {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
        this.condition = condition;
    }

    @Override
    public boolean canStart() {
        if (this.condition.test(this.mob)) {
            return super.canStart();
        }
        return false;
    }
}
