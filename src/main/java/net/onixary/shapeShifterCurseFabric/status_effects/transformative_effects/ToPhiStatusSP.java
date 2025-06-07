package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.removeVisualEffects;

public class ToPhiStatusSP extends BaseTransformativeStatusEffect {
    public ToPhiStatusSP() {
        super(PlayerForms.PHI_SP, StatusEffectCategory.NEUTRAL, 0xFFFFFF, false);
    }

    @Override
    public void onEffectApplied(PlayerEntity player) {
        TransformManager.handleDirectTransform(player, PlayerForms.PHI_SP, false);
        removeVisualEffects(player);
    }

    @Override
    public void onEffectCanceled(PlayerEntity player) {
        removeVisualEffects(player);
    }

}
