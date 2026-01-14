package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;

public class TransformativeStatus extends BaseTransformativeStatusEffect {
    public TransformativeStatus(PlayerFormBase toForm) {
        super(toForm, StatusEffectCategory.NEUTRAL, 0xFFFFFF, false);
    }

    @Override
    public void ActiveEffect(ServerPlayerEntity player) {
        TransformManager.handleDirectTransform(player, this.getToForm(player), false);
    }
}
