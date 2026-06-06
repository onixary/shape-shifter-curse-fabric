package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Arrays;

public class CursedMoon {
    // Server Side
    public static long day = -1;  // 用于实现进入新的日期时自动同步诅咒之月 要不是CommonConfig客户端和服务器端可能不同 连同步都不需要
    // Client Side
    public static boolean isCursedMoon = false;  // 由同步包更新
    public static boolean middayMessageSent = false;  // 接收到同步包时自动置为 false

    public static boolean isCursedMoonByPhase(int moonPhase) {
        int[] curseMoonPhase = ShapeShifterCurseFabric.commonConfig.curseMoonPhase;
        return Arrays.stream(curseMoonPhase).anyMatch(phase -> phase == moonPhase);
    }

    public static boolean isCursedMoonDay(World world) {
        if (world.isClient) {
            return isCursedMoon;
        }
        int moonPhase = world.getMoonPhase();
        return isCursedMoonByPhase(moonPhase);
    }

    public static void clientTick(World world) {
        if (!isCursedMoonDay(world)) { return; }
        long dayTime = world.getTimeOfDay() % 24000;
        if (dayTime >= 6000L && dayTime < 12500L && !middayMessageSent) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                if (player.getWorld().getRegistryKey() != World.OVERWORLD) {
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.before_cursed_moon_nether").formatted(Formatting.LIGHT_PURPLE));
                } else {
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.before_cursed_moon").formatted(Formatting.LIGHT_PURPLE));
                }
            }
            middayMessageSent = true;
        }
    }

    public static void applyStartCursedMoonEffect(World world, PlayerEntity player) {
        // 先检查flag 后执行逻辑
        // TODO
    }

    public static void applyEndCursedMoonEffect(World world, PlayerEntity player) {
        // 先检查flag 后执行逻辑
        // TODO
    }

    public static void serverTick(World world) {
        if (world.isClient) return;
        long timeOfDay = world.getTimeOfDay();
        long nowDay = timeOfDay / 24000;
        long dayTime = timeOfDay % 24000;
        if (nowDay != day) {
            day = nowDay;
            // TODO 同步数据给全部在线玩家
        }
        if (dayTime >= 12500L && dayTime < 23000L && isCursedMoonDay(world)) {
            for (PlayerEntity player : world.getPlayers()) {
                applyStartCursedMoonEffect(world, player);
            }
        } else {
            for (PlayerEntity player : world.getPlayers()) {
                applyEndCursedMoonEffect(world, player);
            }
        }
    }
}
