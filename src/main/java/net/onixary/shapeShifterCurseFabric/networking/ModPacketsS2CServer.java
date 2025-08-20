package net.onixary.shapeShifterCurseFabric.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;

// 纯服务端类，所有send方法都只在这里调用
// This is a pure server-side class, all send methods are called only here
public class ModPacketsS2CServer {

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

    public static void sendSyncEffectAttachment(ServerPlayerEntity player, PlayerEffectAttachment attachment) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeNbt(attachment.toNbt());
        //ShapeShifterCurseFabric.LOGGER.info("Attachment sent, nbt: " + attachment.toNbt());
        ServerPlayNetworking.send(player, ModPackets.SYNC_EFFECT_ATTACHMENT, buf);
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

    // 发送蝙蝠吸附状态同步包
    public static void sendBatAttachState(ServerPlayerEntity player, boolean isAttached,
                                          int attachType, BlockPos attachedPos, Direction attachedSide) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(isAttached);
        buf.writeInt(attachType); // AttachType枚举的ordinal值

        if (attachedPos != null) {
            buf.writeBoolean(true);
            buf.writeBlockPos(attachedPos);
        } else {
            buf.writeBoolean(false);
        }

        if (attachedSide != null) {
            buf.writeBoolean(true);
            buf.writeInt(attachedSide.getId());
        } else {
            buf.writeBoolean(false);
        }

        ServerPlayNetworking.send(player, ModPackets.SYNC_BAT_ATTACH_STATE, buf);
    }
}
