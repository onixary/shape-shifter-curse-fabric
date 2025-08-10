package net.onixary.shapeShifterCurseFabric.status_effects.other_effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public class FeedEffect extends StatusEffect {
    public FeedEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x9ace67);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        // 检查目标是否为玩家
        if (target instanceof PlayerEntity player) {
            int foodToAdd = 8;
            float saturationToAdd = 0.6f;
            player.getHungerManager().add(foodToAdd, saturationToAdd);
        }
        super.applyInstantEffect(source, attacker, target, amplifier, proximity);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}
