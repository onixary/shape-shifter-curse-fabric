package net.onixary.shapeShifterCurseFabric.networking;

import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.FoodComponent;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;
import net.onixary.shapeShifterCurseFabric.util.CustomEdibleUtils;

import java.util.HashMap;
import java.util.List;

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
        buf.writeUuid(player.getUuid());
        buf.writeBoolean(isTransforming);
        buf.writeString(fromForm != null ? fromForm : "");
        buf.writeString(toForm != null ? toForm : "");
//        ServerPlayNetworking.send(player, ModPackets.SYNC_TRANSFORM_STATE, buf);
        // 广播给所有玩家 用于同步动作
        for (ServerPlayerEntity p : player.getServerWorld().getPlayers()) {
            ServerPlayNetworking.send(p, ModPackets.SYNC_TRANSFORM_STATE, buf);
        }
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

    // 广播给附近其他玩家的蝙蝠吸附状态
    public static void broadcastBatAttachState(ServerPlayerEntity targetPlayer, boolean isAttached,
                                               int attachType, BlockPos attachedPos, Direction attachedSide) {
        // 获取附近的所有玩家（64格范围内）
        targetPlayer.getServerWorld().getPlayers(player ->
                player != targetPlayer &&
                        player.squaredDistanceTo(targetPlayer) <= 64 * 64
        ).forEach(nearbyPlayer -> {
            // 发送目标玩家的吸附状态给附近玩家
            sendOtherPlayerBatAttachState(nearbyPlayer, targetPlayer.getUuid(),
                    isAttached, attachType, attachedPos, attachedSide);
        });
    }

    // 发送其他玩家的蝙蝠吸附状态
    public static void sendOtherPlayerBatAttachState(ServerPlayerEntity receiver, java.util.UUID targetPlayerUuid,
                                                     boolean isAttached, int attachType,
                                                     BlockPos attachedPos, Direction attachedSide) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(targetPlayerUuid);
        buf.writeBoolean(isAttached);
        buf.writeInt(attachType);

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

        ServerPlayNetworking.send(receiver, ModPackets.SYNC_OTHER_PLAYER_BAT_ATTACH_STATE, buf);
    }

    // 发送强制潜行状态同步包
    public static void sendForceSneakState(ServerPlayerEntity player, boolean shouldForceSneak) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(shouldForceSneak);
        ServerPlayNetworking.send(player, ModPackets.SYNC_FORCE_SNEAK_STATE, buf);
    }

    private static void sendRemoveDynamicFormExcept(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(RegPlayerForms.dynamicPlayerForms.size());
        for (Identifier formId : RegPlayerForms.dynamicPlayerForms) {
            buf.writeString(formId.toString());
        }
        ServerPlayNetworking.send(player, ModPackets.REMOVE_DYNAMIC_FORM_EXCEPT, buf);
    }

    // 发送动态Form同步包 旧的最大32K 本来以为挺多的，结果发现单个就快4K
    public static void sendUpdateDynamicForm(ServerPlayerEntity player, JsonObject forms) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(forms.size()); // 发送动态Form数量
        for (String formName : forms.keySet()) {
            buf.writeString(formName);
            buf.writeString(forms.get(formName).toString());
        }
        ServerPlayNetworking.send(player, ModPackets.UPDATE_DYNAMIC_FORM, buf);
    }

    // 现在理论 单包32K Form数量无限
    public static void updateDynamicForm(ServerPlayerEntity player) {
        int MaxFormPerPacket = 63;  // 2M / 32K - 1
        HashMap<Identifier, PlayerFormDynamic> forms = RegPlayerForms.DumpDynamicPlayerForms();
        sendRemoveDynamicFormExcept(player);
        for (int i = 0; i < forms.size(); i += MaxFormPerPacket) {
            JsonObject jsonForms = new JsonObject();
            for (int j = 0; j < MaxFormPerPacket && i + j < forms.size(); j++) {
                Identifier formId = RegPlayerForms.dynamicPlayerForms.get(i + j);
                jsonForms.add(formId.toString(), forms.get(formId).save());
            }
            sendUpdateDynamicForm(player, jsonForms);
        }
    }

    // 我暂时没找到玩家进入服务去时的Hook，所以暂时由服务器询问来代替
    public static void sendPlayerLogin(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, ModPackets.LOGIN_PACKET, buf);
    }

    public static void sendCustomEdibleList(ServerPlayerEntity player, List<Identifier> Items, FoodComponent foodComponent) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(1);  // 添加CustomEdible
        buf.writeInt(Items.size());
        for (Identifier id : Items) {
            buf.writeIdentifier(id);
        }
        CustomEdibleUtils.WriteFoodComponent(buf, foodComponent);
        ServerPlayNetworking.send(player, ModPackets.UPDATE_CUSTOM_EDIBLE_DATA, buf);
    }

    public static void sendClearEdibleList(ServerPlayerEntity player, List<Identifier> Items) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(0);  // 清空CustomEdible
        buf.writeInt(Items.size());
        for (Identifier id : Items) {
            buf.writeIdentifier(id);
        }
        ServerPlayNetworking.send(player, ModPackets.UPDATE_CUSTOM_EDIBLE_DATA, buf);
    }
}
