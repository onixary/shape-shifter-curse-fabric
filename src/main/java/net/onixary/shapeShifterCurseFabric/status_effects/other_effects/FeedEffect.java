package net.onixary.shapeShifterCurseFabric.status_effects.other_effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.mana.ManaRegistries;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
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
            // 基础数值
            int baseFoodToAdd = 8;
            float baseSaturationToAdd = 0.6f;

            // 根据proximity计算距离衰减
            // proximity: 1.0 = 爆炸中心，0.0 = 最远距离
            double distanceMultiplier = Math.max(0.5, proximity);

            // 应用距离衰减
            int foodToAdd = (int) Math.ceil(baseFoodToAdd * distanceMultiplier);
            float saturationToAdd = (float) (baseSaturationToAdd * distanceMultiplier);

            player.getHungerManager().add(foodToAdd, saturationToAdd);

            if (ManaRegistries.FAMILIAR_FOX_MANA.equals(ManaUtils.getPlayerManaTypeID(player))) {
                ManaUtils.gainPlayerMana(player, 50d * distanceMultiplier);
            }
        }
        // 原版会对 [瞬间恢复 瞬间伤害] 以外的的效果调用 this.applyUpdateEffect 导致重复加饱食度
        // super.applyInstantEffect(source, attacker, target, amplifier, proximity);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration >= 1;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            player.getHungerManager().add(8, 0.6f);
            if (ManaRegistries.FAMILIAR_FOX_MANA.equals(ManaUtils.getPlayerManaTypeID(player))) {
                ManaUtils.gainPlayerMana(player, 50d);
            }
        }
    }
}
