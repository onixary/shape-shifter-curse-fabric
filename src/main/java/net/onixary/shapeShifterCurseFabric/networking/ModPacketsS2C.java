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
}
