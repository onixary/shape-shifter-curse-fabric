package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.TO_ALPHA_0_EFFECT;
import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.TO_BETA_0_EFFECT;
import static net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager.EFFECT_ATTACHMENT;

public class ToBetaStatusPotion extends StatusEffect {
    public ToBetaStatusPotion() {
        super(StatusEffectCategory.NEUTRAL, 0xFFFFFF);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    @Override
    public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity)
    {
        if (source instanceof ServerPlayerEntity player) {
            PlayerForms curToForm = Objects.requireNonNull(player.getAttached(EFFECT_ATTACHMENT)).currentToForm;
            if (curToForm != TO_BETA_0_EFFECT.getToForm() && FormAbilityManager.getForm(player) == PlayerForms.ORIGINAL_SHIFTER){
                EffectManager.overrideEffect(player, TO_BETA_0_EFFECT);
            }
        }
    }

}
