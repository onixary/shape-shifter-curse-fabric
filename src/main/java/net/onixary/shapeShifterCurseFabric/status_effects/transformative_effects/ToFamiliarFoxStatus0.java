package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.removeVisualEffects;

public class ToFamiliarFoxStatus0 extends BaseTransformativeStatusEffect {
    public ToFamiliarFoxStatus0() {
        super(PlayerForms.FAMILIAR_FOX_0, StatusEffectCategory.NEUTRAL, 0xFFFFFF, false);
    }

    @Override
    public void onEffectApplied(PlayerEntity player) {
        ShapeShifterCurseFabric.LOGGER.info("ToFamiliarFoxStatus0 onEffect Applied");
        //FormAbilityManager.applyForm(player, PlayerForms.BAT_0);
        TransformManager.handleDirectTransform(player, PlayerForms.FAMILIAR_FOX_0, false);
        removeVisualEffects(player);
    }

    @Override
    public void onEffectCanceled(PlayerEntity player) {
        ShapeShifterCurseFabric.LOGGER.info("ToFamiliarFoxStatus0 effect Canceled");
        removeVisualEffects(player);
    }

}
