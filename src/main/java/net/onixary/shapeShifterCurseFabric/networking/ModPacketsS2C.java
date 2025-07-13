package net.onixary.shapeShifterCurseFabric.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;

// 应仅在客户端注册
// This class should only be registered on the client side
public class ModPacketsS2C {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.TRANSFORM_EFFECT_ID, ModPacketsS2C::receiveTransformEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.INSTINCT_THRESHOLD_EFFECT_ID, ModPacketsS2C::receiveInstinctThresholdEffect);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_CURSED_MOON_DATA, ModPacketsS2C::receiveCursedMoonData);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_FORM_CHANGE, ModPacketsS2C::receiveFormChange);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_TRANSFORM_STATE, ModPacketsS2C::receiveTransformState);
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

    public static void sendSyncEffectAttachment(ServerPlayerEntity player, PlayerEffectAttachment attachment) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeNbt(attachment.toNbt());
        //ShapeShifterCurseFabric.LOGGER.info("Attachment sent, nbt: " + attachment.toNbt());
        ServerPlayNetworking.send(player, ModPackets.SYNC_EFFECT_ATTACHMENT, buf);
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

    public static void sendCursedMoonData(ServerPlayerEntity player, long dayTime, int day, boolean isCursedMoon, boolean isNight) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeLong(dayTime);
        buf.writeInt(day);
        buf.writeBoolean(isCursedMoon);
        buf.writeBoolean(isNight);
        ServerPlayNetworking.send(player, ModPackets.SYNC_CURSED_MOON_DATA, buf);
    }

    // 发送形态变化同步包
    public static void sendFormChange(ServerPlayerEntity player, String newFormName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(newFormName);
        ServerPlayNetworking.send(player, ModPackets.SYNC_FORM_CHANGE, buf);
        ShapeShifterCurseFabric.LOGGER.info("Sent form change to client: " + newFormName);
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

    // 发送变身状态同步包
    public static void sendTransformState(ServerPlayerEntity player, boolean isTransforming,
                                        String fromForm, String toForm) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(isTransforming);
        buf.writeString(fromForm != null ? fromForm : "");
        buf.writeString(toForm != null ? toForm : "");
        ServerPlayNetworking.send(player, ModPackets.SYNC_TRANSFORM_STATE, buf);
        ShapeShifterCurseFabric.LOGGER.info("Sent transform state to client: isTransforming=" + isTransforming);
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
}
