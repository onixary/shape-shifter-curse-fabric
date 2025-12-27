package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TransformativeAxolotlEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot.TransformativeOcelotEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.wolf.TransformativeWolfEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.TransformativeStatusInstance;

import java.util.Objects;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.*;

public class TStatusApplier {
    private TStatusApplier() {}

    public static float T_BAT_STATUS_CHANCE = 0.5f;
    public static float T_AXOLOTL_STATUS_CHANCE = 0.7f;
    public static float T_OCELOT_STATUS_CHANCE = 0.5f;
    public static float T_WOLF_STATUS_CHANCE = 0.5f;

    public static void applyStatusFromTMob(MobEntity fromMob, PlayerEntity player) {
        // 过段时间我把这个逻辑重构进fromMob里
        if (fromMob instanceof TransformativeBatEntity) {
            applyStatusByChance(T_BAT_STATUS_CHANCE, player, TO_BAT_0_EFFECT);
        }
        else if (fromMob instanceof TransformativeAxolotlEntity) {
            applyStatusByChance(T_AXOLOTL_STATUS_CHANCE, player, TO_AXOLOTL_0_EFFECT);
        }
        else if (fromMob instanceof TransformativeOcelotEntity) {
            applyStatusByChance(T_OCELOT_STATUS_CHANCE, player, TO_OCELOT_0_EFFECT);
        }
        else if (fromMob instanceof TransformativeWolfEntity) {
            applyStatusByChance(T_WOLF_STATUS_CHANCE, player, TO_ANUBIS_WOLF_0_EFFECT);
        }
    }

    private static void applyStatusByChance(float chance, PlayerEntity player, BaseTransformativeStatusEffect regStatusEffect) {
        /* 重构后不需要了 仅用于参考旧实现逻辑
        PlayerFormBase curToForm = getOrCreateAttachment(player).currentToForm;
        ShapeShifterCurseFabric.LOGGER.info("current dest form: {} when applyStatusByChance", curToForm);
        // 只有不同种类的效果才会互相覆盖
        if (Math.random() < chance &&
                !RegPlayerForms.IsPlayerFormEqual(curToForm, regStatusEffect.getToForm()) &&
                FormAbilityManager.getForm(player).equals(RegPlayerForms.ORIGINAL_SHIFTER)) {
            ShapeShifterCurseFabric.LOGGER.info("TStatusApplier applyStatusByChance");
            EffectManager.overrideEffect(player, regStatusEffect);
        }
         */
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
