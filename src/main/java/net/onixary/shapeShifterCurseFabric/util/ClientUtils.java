package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

// 服务端不能访问这个class里的任何函数 有随机崩溃问题 所以现在所有函数加了一个检查 防止测试时测不出问题 发布时随机崩溃
public class ClientUtils {
    public static boolean isOpenInventoryScreen = false;

    public static PlayerEntity getPlayer() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new RuntimeException("Cannot invoke this method in a non-client environment");
            // return null;
        }
        return MinecraftClient.getInstance().player;
    }

    public static boolean CanDisplayGUI() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            throw new RuntimeException("Cannot invoke this method in a non-client environment");
            // return true;
        }
        return !MinecraftClient.getInstance().options.hudHidden;
    }
}
