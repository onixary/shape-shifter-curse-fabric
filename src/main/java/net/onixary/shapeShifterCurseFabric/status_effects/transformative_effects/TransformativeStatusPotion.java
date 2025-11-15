package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;
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
            PlayerEffectAttachment attachment = EffectManager.getOrCreateAttachment(player);

            // 安全获取 currentToForm，为空则设为原始形态
            PlayerFormBase curToForm = attachment.currentToForm;
            if (curToForm == null) {
                curToForm = RegPlayerForms.ORIGINAL_SHIFTER;
                attachment.currentToForm = curToForm; // 同步回附件，避免后续重复为空
            }

            // 补充 FormAbilityManager.getForm(player) 的判空（防止返回 null）
            PlayerFormBase playerCurrentForm = FormAbilityManager.getForm(player);
            if (playerCurrentForm == null) {
                playerCurrentForm = RegPlayerForms.ORIGINAL_SHIFTER;
            }

            // （现在所有变量都非空，）
            if (!RegPlayerForms.IsPlayerFormEqual(curToForm, this.TransformativeStatusEffect.getToForm())
                    && playerCurrentForm.equals(RegPlayerForms.ORIGINAL_SHIFTER)) {
                EffectManager.overrideEffect(player, this.TransformativeStatusEffect);
            }
        }
    }
}
