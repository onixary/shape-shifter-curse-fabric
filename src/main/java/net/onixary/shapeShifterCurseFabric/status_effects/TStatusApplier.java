package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.old.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.TransformativeStatusInstance;

public class TStatusApplier {
    public static void applyStatusByChance(float chance, PlayerEntity player, BaseTransformativeStatusEffect regStatusEffect) {
        if (player instanceof ServerPlayerEntity playerEntity) {
            TransformativeStatusInstance instance = EffectManager.getTransformativeEffect(playerEntity);
            if (instance == null || instance.getTransformativeEffectType() == null || !instance.getTransformativeEffectType().getToForm(player).equals(regStatusEffect.getToForm(player))) {  // 如果当前效果的形态与regStatusEffect不同
                if (Math.random() < chance && RegPlayerForms.ORIGINAL_SHIFTER.equals(FormAbilityManager.getForm(player))) {
                    EffectManager.overrideEffect(player, regStatusEffect);
                }
            }
        }
    }
}
