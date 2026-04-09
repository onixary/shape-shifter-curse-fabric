package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public class EntangledEffectUtils {
    public static final int ENTANGLED_DURATION_PER_LEVEL = 20 * 5;
    public static final int ENTANGLED_MAX_LEVEL = 4;
    public static final int ENTANGLED_FULL_DURATION = 20 * 15;
    // PVP考虑，对玩家裹茧效果的时长缩短
    public static final int ENTANGLED_FULL_DURATION_PLAYER = 20 * 5;

    public static void applyEntangledEffect(LivingEntity target, int Time) {
        if (target.getStatusEffect(RegOtherStatusEffects.ENTANGLED_FULL_EFFECT) != null) {
            return;
        }
        StatusEffectInstance entangledEffect = target.getStatusEffect(RegOtherStatusEffects.ENTANGLED_EFFECT);
        if (entangledEffect == null) {
            target.addStatusEffect(new StatusEffectInstance(RegOtherStatusEffects.ENTANGLED_EFFECT, Time, Time / ENTANGLED_DURATION_PER_LEVEL));
        } else {
            int newDuration = entangledEffect.getDuration() + Time;
            int newAmplifier = Math.min(entangledEffect.getAmplifier() + 1, ENTANGLED_MAX_LEVEL);
            target.removeStatusEffect(RegOtherStatusEffects.ENTANGLED_EFFECT);
            target.addStatusEffect(new StatusEffectInstance(RegOtherStatusEffects.ENTANGLED_EFFECT, newDuration, newAmplifier));
        }
        entangledEffect = target.getStatusEffect(RegOtherStatusEffects.ENTANGLED_EFFECT);
        if (entangledEffect != null) {
            int NowDuration = entangledEffect.getDuration();
            if (NowDuration >= ENTANGLED_DURATION_PER_LEVEL * (ENTANGLED_MAX_LEVEL + 1)) {
                target.removeStatusEffect(RegOtherStatusEffects.ENTANGLED_EFFECT);
                if(target instanceof PlayerEntity){
                    target.addStatusEffect(new StatusEffectInstance(RegOtherStatusEffects.ENTANGLED_FULL_EFFECT, ENTANGLED_FULL_DURATION_PLAYER, 0));
                }
                else{
                    target.addStatusEffect(new StatusEffectInstance(RegOtherStatusEffects.ENTANGLED_FULL_EFFECT, ENTANGLED_FULL_DURATION, 0));
                }
            }
        }
    }
}
