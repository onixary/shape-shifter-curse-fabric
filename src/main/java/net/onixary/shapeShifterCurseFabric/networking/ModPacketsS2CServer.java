package net.onixary.shapeShifterCurseFabric.networking;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.VirtualTotemPower;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import org.jetbrains.annotations.Nullable;
import net.onixary.shapeShifterCurseFabric.util.PatronUtils;

import java.util.HashMap;
import java.util.UUID;

import static net.onixary.shapeShifterCurseFabric.networking.ModPackets.UPDATE_POWER_ANIM_DATA_TO_CLIENT;
import static net.onixary.shapeShifterCurseFabric.networking.ModPackets.UPDATE_POWER_ANIM_DATA_TO_SERVER;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    /* 重构后不需要了 仅用于参考旧实现逻辑
    public static void sendSyncEffectAttachment(ServerPlayerEntity player, PlayerEffectAttachment attachment) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeNbt(attachment.toNbt());
        //ShapeShifterCurseFabric.LOGGER.info("Attachment sent, nbt: " + attachment.toNbt());
        ServerPlayNetworking.send(player, ModPackets.SYNC_EFFECT_ATTACHMENT, buf);
    }
     */

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

    // 仅在获取到 Patron 数据后调用 玩家登录由 updateDynamicForm 负责
    public static void updatePatronForms(ServerPlayerEntity player, List<Identifier> patronForms) {
        int MaxFormPerPacket = 63;
        HashMap<Identifier, PlayerFormDynamic> forms = new HashMap<>();
        for (Identifier formId : patronForms) {
            PlayerFormBase form = RegPlayerForms.getPlayerForm(formId);
            if (form instanceof PlayerFormDynamic pfd) {
                forms.put(formId, pfd);
            }
        }
        int NowPacket = 0;
        int RemainPacket = forms.size();
        JsonObject jsonForms = new JsonObject();
        for (Identifier formId : forms.keySet()) {
            jsonForms.add(formId.toString(), forms.get(formId).save());
            NowPacket ++;
            RemainPacket --;
            if (NowPacket % MaxFormPerPacket == 0) {
                sendUpdateDynamicForm(player, jsonForms);
                jsonForms = new JsonObject();
            }
        }
        if (RemainPacket > 0) {
            sendUpdateDynamicForm(player, jsonForms);
        }
    }

    public static void updatePatronLevel(MinecraftServer server) {
        HashMap<UUID, Integer> patronLevels = PatronUtils.PatronLevels;
        int PairCount = patronLevels.size();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(PairCount);
            for (Map.Entry<UUID, Integer> entry : patronLevels.entrySet()) {
                buf.writeUuid(entry.getKey());
                buf.writeInt(entry.getValue());
            }
            ServerPlayNetworking.send(player, ModPackets.UPDATE_PATRON_LEVEL, buf);
        }
    }

    public static void OpenPatronFormSelectMenu(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, ModPackets.OPEN_PATRON_FORM_SELECT_MENU, buf);
    }

    public static void sendActiveVirtualTotem(ServerPlayerEntity player, VirtualTotemPower virtualTotemPower) {
        player.getServerWorld().getPlayers(near_player -> near_player.squaredDistanceTo(player) <= 64 * 64).forEach(
                nearPlayer -> {
                    PacketByteBuf buf = virtualTotemPower.create_packet_byte_buf();
                    if (buf != null) {
                        ServerPlayNetworking.send(nearPlayer, ModPackets.ACTIVE_VIRTUAL_TOTEM, buf);
                    }
                }
        );
    }

    public static void sendPowerAnimationDataToClient(ServerPlayerEntity player, UUID PlayerUUID, @Nullable Identifier animationId, int animationCount, int animationLength) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(PlayerUUID);
        if (animationId != null) {
            buf.writeBoolean(true);
            buf.writeIdentifier(animationId);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeInt(animationCount);
        buf.writeInt(animationLength);
        ServerPlayNetworking.send(player, UPDATE_POWER_ANIM_DATA_TO_CLIENT, buf);
    }

    public static void sendPowerAnimationDataToNearPlayer(ServerPlayerEntity player, @Nullable Identifier animationId, int animationCount, int animationLength) {
        player.getServerWorld().getPlayers(near_player -> near_player.squaredDistanceTo(player) <= 128 * 128).forEach(
                nearPlayer -> {
                    sendPowerAnimationDataToClient(nearPlayer, player.getUuid(), animationId, animationCount, animationLength);
                }
        );
    }
}
