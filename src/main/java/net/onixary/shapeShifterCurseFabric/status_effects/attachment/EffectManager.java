package net.onixary.shapeShifterCurseFabric.status_effects.attachment;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.*;
import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.removeEffects;
import static net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage.loadAttachment;
import static net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage.saveAttachment;

public class EffectManager {
    // 注册玩家附身
    public static final AttachmentType<PlayerEffectAttachment> EFFECT_ATTACHMENT =
            AttachmentRegistry.create(new Identifier(MOD_ID, "effect_data"));

    //static String testUUID = "testUUID-3d9ab571-1ea5-360b-bc9d-77cd0b2f72a9";
    public static BaseTransformativeStatusEffect currentRegEffect;

    // 覆盖新的效果
    public static void overrideEffect(PlayerEntity player, BaseTransformativeStatusEffect regEffect) {
        LOGGER.info("get attach here");
        PlayerEffectAttachment attachment = player.getAttached(EFFECT_ATTACHMENT);
        LOGGER.info("remove old effect here");
        // 移除旧效果
        if (attachment.currentEffect != null && attachment.currentEffect.getToForm() != regEffect.getToForm()) {
            attachment.currentEffect.onEffectCanceled(player);
        }
        LOGGER.info("apply new effect here");
        // 应用新效果
        currentRegEffect = attachment.currentRegEffect = regEffect;
        attachment.currentToForm = regEffect.getToForm();
        attachment.remainingTicks = StaticParams.T_EFFECT_DEFAULT_DURATION;
        attachment.currentEffect = regEffect;
        LOGGER.info("apply potion effect here");
        // 添加原版药水效果（用于渲染）
        // 呈现必须使用注册过的类，因此将其手动传入
        player.addStatusEffect(new StatusEffectInstance(regEffect, StaticParams.T_EFFECT_DEFAULT_DURATION));
    }

    // 强制结束当前效果
    public static void applyEffect(PlayerEntity player) {
        PlayerEffectAttachment attachment = player.getAttached(EFFECT_ATTACHMENT);
        LOGGER.info(attachment == null? "attachment is null" : "attachment is not null");
        if (attachment != null && attachment.currentEffect != null) {
            currentRegEffect = attachment.currentRegEffect = null;
            attachment.currentEffect.onEffectApplied(player);
            attachment.currentToForm = null;
            attachment.remainingTicks = 0;
            attachment.currentEffect = null;
        }
    }

    public PlayerEffectAttachment getCurrentEffectAttachment(PlayerEntity player) {
        return player.getAttached(EFFECT_ATTACHMENT);
    }

    public static boolean saveCurrentAttachment(ServerWorld world, PlayerEntity player) {
        PlayerEffectAttachment attachment = player.getAttached(EFFECT_ATTACHMENT);
        if(attachment != null) {
            // todo: 构建环境中UUID会变化，测试时使用固定的testUUID，发布时要改回
            //saveAttachment(String.valueOf((player.getUuid())), attachment);
            saveAttachment(world, DEBUG_UUID == null? player.getUuid().toString() : DEBUG_UUID, attachment);
            LOGGER.info("save attachment success, currentToForm: " + attachment.currentToForm);
            return true;
        }
        else{
            LOGGER.info("save attachment failed");
            return false;
        }
    }

    public static boolean loadCurrentAttachment(ServerWorld world, PlayerEntity player) {
        // todo: 构建环境中UUID会变化，测试时使用固定的testUUID，发布时要改回
        PlayerEffectAttachment attachment = loadAttachment(world, DEBUG_UUID == null? player.getUuid().toString() : DEBUG_UUID);
        player.setAttached(EffectManager.EFFECT_ATTACHMENT, attachment);
        if(attachment == null){
            LOGGER.info("no attachment found in file");
            return false;
        }
        return true;
    }

    public static void resetAttachment(PlayerEntity player) {
        player.setAttached(EffectManager.EFFECT_ATTACHMENT, new PlayerEffectAttachment());
        removeEffects(player);
    }
}
