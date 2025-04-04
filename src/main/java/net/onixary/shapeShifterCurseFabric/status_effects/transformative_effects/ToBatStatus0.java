package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.removeEffects;
import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.overrideEffect;

public class ToBatStatus0 extends BaseTransformativeStatusEffect {
    public ToBatStatus0() {
        super(PlayerForms.BAT_0, StatusEffectCategory.NEUTRAL, 0xFFFFFF, false);
    }

    @Override
    public void onEffectApplied(PlayerEntity player) {
        ShapeShifterCurseFabric.LOGGER.info("ToBatStatus0 onEffect Applied");
        //FormAbilityManager.applyForm(player, PlayerForms.BAT_0);
        TransformManager.handleDirectTransform(player, PlayerForms.BAT_0, false);
        removeEffects(player);
    }

    @Override
    public void onEffectCanceled(PlayerEntity player) {
        ShapeShifterCurseFabric.LOGGER.info("ToBatStatus0 effect Canceled");
        removeEffects(player);
    }

}
