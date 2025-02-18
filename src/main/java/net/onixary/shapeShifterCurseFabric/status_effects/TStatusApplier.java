package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.TO_BAT_0_EFFECT;
import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.EFFECT_ATTACHMENT;

public class TStatusApplier {
    private TStatusApplier() {}

    public static float T_BAT_STATUS_CHANCE = 0.5f;

    public static void applyStatusFromTMob(MobEntity fromMob, PlayerEntity player) {
        if (fromMob instanceof TransformativeBatEntity) {
            applyStatusByChance(T_BAT_STATUS_CHANCE, player, TO_BAT_0_EFFECT);
        }
    }

    private static void applyStatusByChance(float chance, PlayerEntity player, BaseTransformativeStatusEffect regStatusEffect) {
        // 只有不同种类的效果才会互相覆盖
        if (Math.random() < chance && player.getAttached(EFFECT_ATTACHMENT).currentToForm != regStatusEffect.getToForm()) {
            ShapeShifterCurseFabric.LOGGER.info("TStatusApplier applyStatusByChance");
            EffectManager.overrideEffect(player, regStatusEffect);
        }
    }
}
