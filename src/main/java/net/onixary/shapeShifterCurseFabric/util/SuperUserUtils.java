package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.util.util.CachedDataMap;

import java.util.UUID;

public class SuperUserUtils {
    // 用于在测试环境内使用命令 IDEA的服务器环境我之前用的是rcon给的OP 有点麻烦 还是加一个su命令吧 需要"enableDebugCommand==true"
    private static final CachedDataMap<UUID, PlayerEntity, Integer> superUserData = new CachedDataMap<>(player -> -1, Entity::getUuid);
    private static int clientSuperUserLevel = -1;

    public static int getCurrentPermissionLevel(PlayerEntity player) {
        return superUserData.get(player);
    }

    public static int getCurrentPermissionLevel(UUID playerUuid) {
        return superUserData.get(playerUuid, null);
    }

    public static int getClientPermissionLevel() {
        return clientSuperUserLevel;
    }

    public static void setSULevel(ServerPlayerEntity player, int level) {
        if (level < -1 || level > 4) {
            throw new IllegalArgumentException("level must be between -1 and 4");
        }
        superUserData.setA(player, level);
        ModPacketsS2CServer.sendSetSuperUserLevel(player, level);
    }

    public static void setClientSULevel(int level) {
        if (level < -1 || level > 4) {
            throw new IllegalArgumentException("level must be between -1 and 4");
        }
        clientSuperUserLevel = level;
    }
}
