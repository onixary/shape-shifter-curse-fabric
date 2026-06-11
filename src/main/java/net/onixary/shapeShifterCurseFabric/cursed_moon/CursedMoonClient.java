package net.onixary.shapeShifterCurseFabric.cursed_moon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import static net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon.isCursedMoonDay;

public class CursedMoonClient {
    // Client Side
    public static boolean isCursedMoon = false;  // 由同步包更新
    public static boolean middayMessageSent = false;  // 接收到同步包时自动置为 false

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
}
