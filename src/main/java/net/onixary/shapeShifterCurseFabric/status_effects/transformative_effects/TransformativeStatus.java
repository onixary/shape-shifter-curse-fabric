package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.removeVisualEffects;

public class TransformativeStatus extends BaseTransformativeStatusEffect {
    public TransformativeStatus(PlayerFormBase toForm) {
        super(toForm, StatusEffectCategory.NEUTRAL, 0xFFFFFF, false);
    }

    @Override
    public void onEffectApplied(PlayerEntity player) {
        TransformManager.handleDirectTransform(player, this.getToForm(), false);
        removeVisualEffects(player);
    }

    @Override
    public void onEffectCanceled(PlayerEntity player) {
        removeVisualEffects(player);
    }
}
