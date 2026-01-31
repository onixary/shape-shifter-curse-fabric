package net.onixary.shapeShifterCurseFabric.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;

@Environment(EnvType.CLIENT)
public class UIPositionUtils {
    private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    // 矫正点
    // 1 2 3
    // 4 5 6
    // 7 8 9
    // 额外XY偏移量

    public static Pair<Integer, Integer> getCorrectPosition(int positionType, int extraX, int extraY) {
        int PosX = 0;
        int PosY = 0;
        switch (positionType) {
            case 1 -> {
                PosX = 0;
                PosY = 0;
            }
            case 2 -> {
                PosX = minecraftClient.getWindow().getScaledWidth() / 2;
                PosY = 0;
            }
            case 3 -> {
                PosX = minecraftClient.getWindow().getScaledWidth();
                PosY = 0;
            }
            case 4 -> {
                PosX = 0;
                PosY = minecraftClient.getWindow().getScaledHeight() / 2;
            }
            case 5 -> {
                PosX = minecraftClient.getWindow().getScaledWidth() / 2;
                PosY = minecraftClient.getWindow().getScaledHeight() / 2;
            }
            case 6 -> {
                PosX = minecraftClient.getWindow().getScaledWidth();
                PosY = minecraftClient.getWindow().getScaledHeight() / 2;
            }
            case 7 -> {
                PosX = 0;
                PosY = minecraftClient.getWindow().getScaledHeight();
            }
            case 8 -> {
                PosX = minecraftClient.getWindow().getScaledWidth() / 2;
                PosY = minecraftClient.getWindow().getScaledHeight();
            }
            case 9 -> {
                PosX = minecraftClient.getWindow().getScaledWidth();
                PosY = minecraftClient.getWindow().getScaledHeight();
            }
            default -> {
                PosX = minecraftClient.getWindow().getScaledWidth() / 2;
                PosY = minecraftClient.getWindow().getScaledHeight() / 2;
            }
        }
        return new Pair<>(PosX + extraX, PosY + extraY);
    }
}
