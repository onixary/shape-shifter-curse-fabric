package net.onixary.shapeShifterCurseFabric.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.advancement.OnEnableMod;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.PlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;

import java.util.UUID;

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
    }

    private static void onPressStartBookButton(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender){
        UUID playerUuid = packetByteBuf.readUuid();
        minecraftServer.execute(() -> {
            // 通过 UUID 获取玩家实例
            ServerPlayerEntity targetPlayer = minecraftServer.getPlayerManager().getPlayer(playerUuid);
            if (targetPlayer != null) {
                TransformManager.handleDirectTransform(targetPlayer, PlayerForms.ORIGINAL_SHIFTER, false);
                // 触发自定义成就
                ShapeShifterCurseFabric.ON_ENABLE_MOD.trigger(targetPlayer);
                // info
                targetPlayer.sendMessage(Text.translatable("info.shape-shifter-curse.on_enable_mod").formatted(Formatting.LIGHT_PURPLE));
            }
        });
    }
}
