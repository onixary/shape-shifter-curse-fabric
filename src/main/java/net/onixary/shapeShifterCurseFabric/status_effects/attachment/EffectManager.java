package net.onixary.shapeShifterCurseFabric.status_effects.attachment;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects.TransformativeStatusInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.*;

public class EffectManager {
    // public static final AttachmentType<PlayerEffectAttachment> EFFECT_ATTACHMENT =
    //         AttachmentRegistry.create(new Identifier(MOD_ID, "effect_data"));

    // 移除静态的 currentRegEffect，因为它可能与多个玩家的状态冲突
    // 应该只使用 attachment 中的 currentRegEffect

    // 覆盖新的效果
    /*

    public static void overrideEffect(PlayerEntity player, BaseTransformativeStatusEffect regEffect) {
        LOGGER.info("Applying new transformative effect to player: {}", player.getName().getString());

        PlayerEffectAttachment attachment = getOrCreateAttachment(player);

        // 移除旧效果
        if (attachment.currentEffect != null && attachment.currentEffect.getToForm() != regEffect.getToForm()) {
            // attachment.currentEffect.onEffectCanceled(player);
        }

        // 应用新效果
        attachment.currentRegEffect = regEffect;
        attachment.currentToForm = regEffect.getToForm();
        attachment.remainingTicks = StaticParams.T_EFFECT_DEFAULT_DURATION;
        attachment.currentEffect = regEffect;

        // 添加原版药水效果
        player.addStatusEffect(new StatusEffectInstance(regEffect, StaticParams.T_EFFECT_DEFAULT_DURATION));

        // 触发自定义成就
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ON_GET_TRANSFORM_EFFECT.trigger(serverPlayer);
            ModPacketsS2CServer.sendSyncEffectAttachment(serverPlayer, attachment);
        }

        LOGGER.info("Successfully applied effect: {}", regEffect.getToForm());
    }


    public static void loadEffect(PlayerEntity player, PlayerEffectAttachment loadedAttachment) {
        if (loadedAttachment.currentRegEffect != null) {
            player.addStatusEffect(new StatusEffectInstance(
                    loadedAttachment.currentRegEffect,
                    loadedAttachment.remainingTicks
            ));
        }

        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModPacketsS2CServer.sendSyncEffectAttachment(serverPlayer, loadedAttachment);
        }
    }

    // 强制应用当前效果
    public static void applyEffect(PlayerEntity player) {
        PlayerEffectAttachment attachment = getOrCreateAttachment(player);
        if (attachment.currentEffect != null) {
            LOGGER.info("Applying effect for player: {}", player.getName().getString());
            attachment.currentEffect.ActiveEffect(player);
            clearAttachmentData(attachment);
        }
    }

    // 强制结束当前效果
    public static void cancelEffect(PlayerEntity player) {
        PlayerEffectAttachment attachment = getOrCreateAttachment(player);
        if (attachment.currentEffect != null) {
            LOGGER.info("Canceling effect for player: {}", player.getName().getString());
            // attachment.currentEffect.onEffectCanceled(player);
            clearAttachmentData(attachment);

            // 同步到客户端
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ModPacketsS2CServer.sendSyncEffectAttachment(serverPlayer, attachment);
            }
        }
    }

    public static PlayerEffectAttachment getCurrentEffectAttachment(PlayerEntity player) {
        return getOrCreateAttachment(player);
    }

    public static void saveCurrentAttachment(ServerWorld world, PlayerEntity player) {
        PlayerEffectAttachment attachment = getOrCreateAttachment(player);
        saveAttachment(world, player.getUuid().toString(), attachment);

        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModPacketsS2CServer.sendSyncEffectAttachment(serverPlayer, attachment);
        }
    }

    public static boolean loadCurrentAttachment(ServerWorld world, PlayerEntity player) {
        PlayerEffectAttachment attachment = loadAttachment(world, player.getUuid().toString());
        player.setAttached(EffectManager.EFFECT_ATTACHMENT, attachment);

        if (attachment == null) {
            LOGGER.info("No attachment found for player: {}", player.getName().getString());
            return false;
        } else if (attachment.currentToForm != null) {
            LOGGER.info("Loaded attachment for player: {}, form: {}",
                    player.getName().getString(), attachment.currentToForm);
            loadEffect(player, attachment);
            return true;
        } else {
            LOGGER.info("Loaded empty attachment for player: {}, resetting", player.getName().getString());
            return false;
        }
    }

    public static void safeResetAttachment(PlayerEntity player) {
        PlayerEffectAttachment attachment = getCurrentEffectAttachment(player);

        // 检查是否需要清理
        boolean hasTransformEffect = hasTransformativeEffect(player);
        boolean hasAttachmentData = attachment.currentEffect != null || attachment.currentRegEffect != null;

        if (!hasTransformEffect && hasAttachmentData) {
            LOGGER.info("Safely resetting effect attachment for player: {}", player.getName().getString());
            resetAttachment(player);

            // 如果是服务端玩家，同步到客户端
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ModPacketsS2CServer.sendSyncEffectAttachment(serverPlayer, getCurrentEffectAttachment(player));
            }
        }
    }

    public static void resetAttachment(PlayerEntity player) {
        PlayerEffectAttachment attachment = getOrCreateAttachment(player);
        boolean hadEffect = attachment.currentEffect != null;

        // 清空附件数据
        clearAttachmentData(attachment);

        // 移除相关的原版状态效果
        removeTransformativeEffects(player);
        removeVisualEffects(player);

        // 重新设置清理后的附件
        player.setAttached(EffectManager.EFFECT_ATTACHMENT, attachment);

        if (hadEffect) {
            LOGGER.info("Successfully reset effect attachment for player: {}", player.getName().getString());
        }
    }

    public static void verifyAndCleanAttachment(PlayerEntity player) {
        boolean hasTransformEffect = hasTransformativeEffect(player);
        PlayerEffectAttachment attachment = getCurrentEffectAttachment(player);
        boolean hasAttachmentData = attachment.currentEffect != null || attachment.currentRegEffect != null;

        // 如果数据不一致，进行清理
        if (!hasTransformEffect && hasAttachmentData) {
            LOGGER.warn("Detected inconsistent effect state for player: {}, cleaning up",
                    player.getName().getString());
            safeResetAttachment(player);
        }
    }
     */

    /**
     * 获取玩家的 PlayerEffectAttachment，如果不存在则创建新的
     */
    /*
    public static PlayerEffectAttachment getOrCreateAttachment(PlayerEntity player) {
        PlayerEffectAttachment attachment = player.getAttached(EFFECT_ATTACHMENT);
        if (attachment == null) {
            attachment = new PlayerEffectAttachment();
            player.setAttached(EFFECT_ATTACHMENT, attachment);
            LOGGER.debug("Created new EffectAttachment for player: {}", player.getName().getString());
        }
        return attachment;
    }
     */

    // ========== 私有辅助方法 ==========

    /**
     * 检查玩家是否有变形效果
     */
    // private static boolean hasTransformativeEffect(PlayerEntity player) {
    //     return player.getStatusEffects().stream()
    //             .anyMatch(effect -> effect.getEffectType() instanceof BaseTransformativeStatusEffect);
    // }

    /**
     * 清空附件数据
     */
    // private static void clearAttachmentData(PlayerEffectAttachment attachment) {
    //     attachment.currentToForm = RegPlayerForms.ORIGINAL_SHIFTER;
    //     attachment.remainingTicks = 0;
    //     attachment.currentEffect = null;
    //     attachment.currentRegEffect = null;
    // }

    /**
     * 移除所有变形相关的原版状态效果
     */
    // private static void removeTransformativeEffects(PlayerEntity player) {
    //     player.getStatusEffects().stream()
    //             .filter(effect -> effect.getEffectType() instanceof BaseTransformativeStatusEffect)
    //             .forEach(effect -> player.removeStatusEffect(effect.getEffectType()));
    // }

    // 从这里开始重构
    // 客户端+服务端
    public static boolean clearTransformativeEffect(PlayerEntity player) {
        if (player == null) {
            ShapeShifterCurseFabric.LOGGER.error("Attempted to clear effect with null player");
            return false;
        }
        Iterator<StatusEffectInstance> iterator = player.getStatusEffects().iterator();
        boolean hasEffect = false;
        for(hasEffect = false; iterator.hasNext(); hasEffect = true) {
            StatusEffectInstance effectInstance = iterator.next();
            if (effectInstance instanceof TransformativeStatusInstance || effectInstance.getEffectType() instanceof BaseTransformativeStatusEffect) {
                player.onStatusEffectRemoved(effectInstance);
                iterator.remove();
            }
        }
        return hasEffect;
    }

    // 客户端+服务端 但应该在服务器端调用
    public static void overrideEffect(PlayerEntity player, BaseTransformativeStatusEffect regEffect) {
        if (player == null || regEffect == null) {
            ShapeShifterCurseFabric.LOGGER.error("Attempted to override effect with null player or effect");
            return;
        }
        clearTransformativeEffect(player);
        TransformativeStatusInstance instance = new TransformativeStatusInstance(regEffect, StaticParams.T_EFFECT_DEFAULT_DURATION);
        player.addStatusEffect(instance);
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ON_GET_TRANSFORM_EFFECT.trigger(serverPlayer);
        }
    }

    // 服务端
    public static @Nullable TransformativeStatusInstance getTransformativeEffect(ServerPlayerEntity player) {
        if (player == null) {
            ShapeShifterCurseFabric.LOGGER.error("Attempted to check effect with null player");
            return null;
        }
        for (StatusEffectInstance effectInstance : player.getStatusEffects()) {
            if (effectInstance instanceof TransformativeStatusInstance) {
                return (TransformativeStatusInstance) effectInstance;
            }
        }
        return null;
    }

    // 客户端+服务端 不太推荐这种方式 getTransformativeEffect(ServerPlayerEntity player)慢
    public static @Nullable TransformativeStatusInstance getTransformativeEffect(PlayerEntity player) {
        if (player == null) {
            ShapeShifterCurseFabric.LOGGER.error("Attempted to check effect with null player");
            return null;
        }
        if (player instanceof ServerPlayerEntity) {
            return getTransformativeEffect((ServerPlayerEntity) player);
        }
        for (StatusEffectInstance effectInstance : player.getStatusEffects()) {
            if (effectInstance.getEffectType() instanceof BaseTransformativeStatusEffect) {
                return TransformativeStatusInstance.formStatusEffectInstance(effectInstance);
            }
        }
        return null;
    }

    // 客户端+服务端
    public static boolean hasTransformativeEffect(PlayerEntity player) {
        if (player == null) {
            ShapeShifterCurseFabric.LOGGER.error("Attempted to check effect with null player");
            return false;
        }
        return player.getStatusEffects().stream().anyMatch(effect -> effect.getEffectType() instanceof BaseTransformativeStatusEffect);
    }

    // 服务端
    public static boolean ActiveTransformativeEffect(ServerPlayerEntity player) {
        TransformativeStatusInstance transformativeStatusInstance = getTransformativeEffect(player);
        if (transformativeStatusInstance == null) {
            return false;
        }
        transformativeStatusInstance.ActiveEffect(player);
        return true;
    }
}