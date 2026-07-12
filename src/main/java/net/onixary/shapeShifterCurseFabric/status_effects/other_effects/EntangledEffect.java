package net.onixary.shapeShifterCurseFabric.status_effects.other_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.onixary.shapeShifterCurseFabric.status_effects.EntangledEffectUtils;
import net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects;

public class EntangledEffect extends StatusEffect {
    public EntangledEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration >= 1;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        StatusEffectInstance instance = entity.getStatusEffect(RegOtherStatusEffects.ENTANGLED_EFFECT);
        if (instance != null) {
            int NowDuration = instance.getDuration();
            int CurrentLevel = instance.getAmplifier();
            int TargetLevel = NowDuration / EntangledEffectUtils.ENTANGLED_DURATION_PER_LEVEL;
            if (CurrentLevel != TargetLevel) {
                entity.removeStatusEffect(RegOtherStatusEffects.ENTANGLED_EFFECT);
                entity.addStatusEffect(new StatusEffectInstance(RegOtherStatusEffects.ENTANGLED_EFFECT, NowDuration, TargetLevel));
            }
        }
    }
}
