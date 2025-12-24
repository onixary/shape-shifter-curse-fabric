package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class ClientUtils {
    public static boolean IsNowPlayingPlayer(PlayerEntity player) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return false;
        }
        // 如果服务器端跑到这里会直接崩溃
        return player == MinecraftClient.getInstance().player;
    }
}
