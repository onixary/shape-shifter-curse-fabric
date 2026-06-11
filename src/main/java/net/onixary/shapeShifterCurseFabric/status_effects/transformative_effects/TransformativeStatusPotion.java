package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import org.jetbrains.annotations.Nullable;

public class TransformativeStatusPotion extends StatusEffect {
    public BaseTransformativeStatusEffect TransformativeStatusEffect;

    public TransformativeStatusPotion(BaseTransformativeStatusEffect TransformativeStatusEffect) {
        super(StatusEffectCategory.NEUTRAL, 0xFFFFFF);
        this.TransformativeStatusEffect = TransformativeStatusEffect;
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration >= 1;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        this.applyInstantEffect(null, null, entity, amplifier, 0);
        entity.removeStatusEffect(this);
    }

    @Override
    public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        if (!target.getWorld().isClient() && target instanceof ServerPlayerEntity player) {
            TransformativeStatusInstance instance = EffectManager.getTransformativeEffect(player);
            if (instance == null || instance.getTransformativeEffectType() == null || !instance.getTransformativeEffectType().getToForm(player).equals(this.TransformativeStatusEffect.getToForm(player))) {  // 如果当前效果的形态与regStatusEffect不同
                if (RegPlayerForms.ORIGINAL_SHIFTER.isPlayerForm(player)) {
                    EffectManager.overrideEffect(player, this.TransformativeStatusEffect);
                }
            }
        }
    }
}
