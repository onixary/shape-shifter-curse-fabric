package net.onixary.shapeShifterCurseFabric.networking;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnJumpPower;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnSprintingToSneakingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;
import net.onixary.shapeShifterCurseFabric.additional_power.JumpEventCondition;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.skin.PlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;

import java.util.UUID;

import static net.onixary.shapeShifterCurseFabric.networking.ModPackets.*;

// 应仅在服务器端注册
// This class should only be registered on the server side
public class ModPacketsC2S {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                ModPackets.VALIDATE_START_BOOK_BUTTON,
                net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::onPressStartBookButton);
        ServerPlayNetworking.registerGlobalReceiver(
                new Identifier(ShapeShifterCurseFabric.MOD_ID, "update_skin_setting"),
                (server, player, handler, buf, responseSender) -> {
                    boolean keepOriginalSkin = buf.readBoolean();
                    server.execute(() -> {
                        PlayerSkinComponent skinComp = RegPlayerSkinComponent.SKIN_SETTINGS.get(player);
                        skinComp.setKeepOriginalSkin(keepOriginalSkin);
                        // 同步到所有客户端，包括发送者自己
                        RegPlayerSkinComponent.SKIN_SETTINGS.sync(player);
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(JUMP_DETACH_REQUEST_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                BatBlockAttachPower attachPower = PowerHolderComponent.getPowers(player, BatBlockAttachPower.class)
                        .stream()
                        .filter(BatBlockAttachPower::isAttached)
                        .findFirst()
                        .orElse(null);

                if (attachPower != null) {
                    attachPower.handleJump(player);
                }
            });
        });

        // jump_event condition handle
        ServerPlayNetworking.registerGlobalReceiver(JUMP_EVENT_ID, (server, player, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();

            server.execute(() -> {
                // 在服务器端设置跳跃状态
                if (player.getUuid().equals(playerUuid)) {
                    JumpEventCondition.setJumping(player, true);
                }

                PowerHolderComponent.getPowers(player, ActionOnJumpPower.class)
                        .forEach(ActionOnJumpPower::executeAction);
            });
        });

        // SPRINTING_TO_SNEAKING_EVENT condition handle
        ServerPlayNetworking.registerGlobalReceiver(SPRINTING_TO_SNEAKING_EVENT_ID, (server, player, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();

            server.execute(() -> {
                // 在服务器端处理疾跑转潜行事件
                if (player.getUuid().equals(playerUuid)) {
                    PowerHolderComponent.getPowers(player, ActionOnSprintingToSneakingPower.class)
                            .forEach(ActionOnSprintingToSneakingPower::executeAction);
                }
            });
        });
    }

    private static void onPressStartBookButton(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender){
        UUID playerUuid = packetByteBuf.readUuid();
        minecraftServer.execute(() -> {
            // 通过 UUID 获取玩家实例
            ServerPlayerEntity targetPlayer = minecraftServer.getPlayerManager().getPlayer(playerUuid);
            if (targetPlayer != null) {
                TransformManager.handleDirectTransform(targetPlayer, RegPlayerForms.ORIGINAL_SHIFTER, false);
                // 触发自定义成就
                ShapeShifterCurseFabric.ON_ENABLE_MOD.trigger(targetPlayer);
                // info
                targetPlayer.sendMessage(Text.translatable("info.shape-shifter-curse.on_enable_mod").formatted(Formatting.LIGHT_PURPLE));
            }
        });
    }

    public static void sendDetachRequest(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        // 不需要额外数据，只是一个解除吸附的信号

        ServerPlayNetworking.send(player, JUMP_DETACH_REQUEST_ID, buf);
    }
}
