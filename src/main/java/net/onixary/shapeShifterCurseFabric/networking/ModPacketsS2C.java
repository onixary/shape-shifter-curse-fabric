package net.onixary.shapeShifterCurseFabric.networking;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;
import net.onixary.shapeShifterCurseFabric.client.ClientPlayerStateManager;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;

import java.util.UUID;

// 应仅在客户端注册
// This class should only be registered on the client side
// 纯客户端类，所有的receive方法都只在这里调用
// This is a pure client-side class, all receive methods are called only here
public class ModPacketsS2C {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.TRANSFORM_EFFECT_ID, ModPacketsS2C::receiveTransformEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.INSTINCT_THRESHOLD_EFFECT_ID, ModPacketsS2C::receiveInstinctThresholdEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_CURSED_MOON_DATA, ModPacketsS2C::receiveCursedMoonData);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_FORM_CHANGE, ModPacketsS2C::receiveFormChange);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_TRANSFORM_STATE, ModPacketsS2C::receiveTransformState);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_BAT_ATTACH_STATE, ModPacketsS2C::receiveBatAttachState);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_EFFECT_ATTACHMENT, ModPacketsS2C::handleSyncEffectAttachment);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.UPDATE_OVERLAY_EFFECT, ModPacketsS2C::receiveUpdateOverlayEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.UPDATE_OVERLAY_FADE_EFFECT, ModPacketsS2C::receiveUpdateOverlayFadeEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.TRANSFORM_COMPLETE_EFFECT, ModPacketsS2C::receiveTransformCompleteEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.RESET_FIRST_PERSON, ModPacketsS2C::receiveResetFirstPerson);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_OTHER_PLAYER_BAT_ATTACH_STATE, ModPacketsS2C::receiveOtherPlayerBatAttachState);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_FORCE_SNEAK_STATE, ModPacketsS2C::receiveForceSneakState);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.UPDATE_DYNAMIC_FORM, ModPacketsS2C::handleUpdateDynamicForm);
    }

    public static void handleSyncEffectAttachment(
		MinecraftClient client,
		ClientPlayNetworkHandler handler,
		PacketByteBuf buf,
		PacketSender sender
	) {
        // 从数据包读取NBT
        NbtCompound nbt = buf.readNbt();
        client.execute(() -> {
            // 更新客户端缓存
            ClientEffectAttachmentCache.update(nbt);
        });
    }

    public static void receiveTransformEffect(MinecraftClient client, ClientPlayNetworkHandler handler,
                                              PacketByteBuf buf, PacketSender responseSender) {
        client.execute(() -> {
            // 当客户端收到这个数据包时，调用TransformManager中的新方法来播放特效
            TransformManager.playClientTransformEffect();
        });
    }

    public static void receiveInstinctThresholdEffect(MinecraftClient client, ClientPlayNetworkHandler handler,
                                                      PacketByteBuf buf, PacketSender responseSender) {
        client.execute(() -> {
            // 当客户端收到这个数据包时，调用TransformManager中的新方法来播放特效
            ShapeShifterCurseFabricClient.applyInstinctThresholdEffect();
        });
    }

    public static void receiveCursedMoonData(MinecraftClient client, ClientPlayNetworkHandler handler,
                                           PacketByteBuf buf, PacketSender responseSender) {
        long dayTime = buf.readLong();
        int day = buf.readInt();
        boolean isCursedMoon = buf.readBoolean();
        boolean isNight = buf.readBoolean();

        client.execute(() -> {
            // 更新客户端的CursedMoon状态
            net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon.day_time = dayTime;
            net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon.day = day;
            net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon.clientIsCursedMoon = isCursedMoon;
            net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon.clientIsNight = isNight;
        });
    }

    // 接收形态变化同步包
    public static void receiveFormChange(MinecraftClient client, ClientPlayNetworkHandler handler,
                                       PacketByteBuf buf, PacketSender responseSender) {
        String newFormName = buf.readString();

        client.execute(() -> {
            // 强制客户端重新注册动画（如果需要）
            if (client.player != null) {
                ShapeShifterCurseFabric.LOGGER.info("Client received form change: " + newFormName);
                // 触发动画重新初始化
                net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient.refreshPlayerAnimations();
            }
        });
    }

    // 接收变身状态同步包
    public static void receiveTransformState(MinecraftClient client, ClientPlayNetworkHandler handler,
                                           PacketByteBuf buf, PacketSender responseSender) {
        boolean isTransforming = buf.readBoolean();
        String fromForm = buf.readString();
        String toForm = buf.readString();

        client.execute(() -> {
            if (client.player != null) {
                ShapeShifterCurseFabric.LOGGER.info("Client received transform state: isTransforming=" + isTransforming);
                // 更新客户端的变身状态
                net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient.updateTransformState(
                    isTransforming, fromForm.isEmpty() ? null : fromForm, toForm.isEmpty() ? null : toForm);
            }
        });
    }

    // 接收Overlay效果更新包
    public static void receiveUpdateOverlayEffect(MinecraftClient client, ClientPlayNetworkHandler handler,
                                                 PacketByteBuf buf, PacketSender responseSender) {
        float nauseaStrength = buf.readFloat();
        int ticks = buf.readInt();

        client.execute(() -> {
            TransformManager.handleClientOverlayUpdate(nauseaStrength, ticks);
        });
    }

    // 接收Overlay淡出效果更新包
    public static void receiveUpdateOverlayFadeEffect(MinecraftClient client, ClientPlayNetworkHandler handler,
                                                     PacketByteBuf buf, PacketSender responseSender) {
        float nauseaStrength = buf.readFloat();
        int ticks = buf.readInt();

        client.execute(() -> {
            TransformManager.handleClientOverlayFadeUpdate(nauseaStrength, ticks);
        });
    }

    // 接收变身完成效果包
    public static void receiveTransformCompleteEffect(MinecraftClient client, ClientPlayNetworkHandler handler,
                                                     PacketByteBuf buf, PacketSender responseSender) {
        client.execute(TransformManager::executeClientTransformCompleteEffect);
    }

    // 接收FirstPerson重置包
    public static void receiveResetFirstPerson(MinecraftClient client, ClientPlayNetworkHandler handler,
                                             PacketByteBuf buf, PacketSender responseSender) {
        client.execute(TransformManager::executeClientFirstPersonReset);
    }

    // 接收蝙蝠吸附状态同步包
    public static void receiveBatAttachState(MinecraftClient client, ClientPlayNetworkHandler handler,
                                             PacketByteBuf buf, PacketSender responseSender) {
        boolean isAttached = buf.readBoolean();
        int attachTypeOrdinal = buf.readInt();

        BlockPos attachedPos;
        if (buf.readBoolean()) {
            attachedPos = buf.readBlockPos();
        } else {
            attachedPos = null;
        }

        Direction attachedSide;
        if (buf.readBoolean()) {
            attachedSide = Direction.byId(buf.readInt());
        } else {
            attachedSide = null;
        }

        client.execute(() -> {
            if (client.player != null) {
                // 获取客户端的BatBlockAttachPower并同步状态
                BatBlockAttachPower.syncClientState(client.player, isAttached, attachTypeOrdinal, attachedPos, attachedSide);
            }
        });
    }

    // 接收其他玩家的蝙蝠吸附状态同步包
    public static void receiveOtherPlayerBatAttachState(MinecraftClient client, ClientPlayNetworkHandler handler,
                                                        PacketByteBuf buf, PacketSender responseSender) {
        UUID targetPlayerUuid = buf.readUuid();
        boolean isAttached = buf.readBoolean();
        int attachType = buf.readInt();

        BlockPos attachedPos;
        Direction attachedSide;

        if (buf.readBoolean()) {
            attachedPos = buf.readBlockPos();
        } else {
            attachedPos = null;
        }

        if (buf.readBoolean()) {
            attachedSide = Direction.byId(buf.readInt());
        } else {
            attachedSide = null;
        }

        client.execute(() -> {
            ClientPlayerStateManager.updatePlayerAttachState(targetPlayerUuid, isAttached,
                    attachType, attachedPos, attachedSide);
        });
    }

    private static void receiveForceSneakState(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean shouldForce = buf.readBoolean();
        client.execute(() -> {
            ClientPlayerStateManager.shouldForceSneak = shouldForce;
        });
    }

    private static void handleUpdateDynamicForm(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // 读取String -> JsonObject
        String jsonStr = buf.readString();
        JsonObject jsonObject = new Gson().fromJson(jsonStr, JsonObject.class);
        client.execute(() -> {
            RegPlayerForms.ApplyDynamicPlayerForms(jsonObject);
        });
    }
}
