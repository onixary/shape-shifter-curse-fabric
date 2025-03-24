package net.onixary.shapeShifterCurseFabric.networking;

import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.advancement.OnEnableMod;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;

import java.util.UUID;

public class ModPacketsC2S {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                ModPackets.VALIDATE_START_BOOK_BUTTON,
                net.onixary.shapeShifterCurseFabric.networking.ModPacketsC2S::onPressStartBookButton);
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
