package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
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
            /* 重构后不需要了 仅用于参考旧实现逻辑
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
             */
            // 从TStatusApplier中提取的代码
            TransformativeStatusInstance instance = EffectManager.getTransformativeEffect(player);
            if (instance == null || instance.getTransformativeEffectType() == null || !instance.getTransformativeEffectType().getToForm(player).equals(this.TransformativeStatusEffect.getToForm(player))) {  // 如果当前效果的形态与regStatusEffect不同
                if (RegPlayerForms.ORIGINAL_SHIFTER.equals(FormAbilityManager.getForm(player))) {
                    EffectManager.overrideEffect(player, this.TransformativeStatusEffect);
                }
            }
        }
    }
}
