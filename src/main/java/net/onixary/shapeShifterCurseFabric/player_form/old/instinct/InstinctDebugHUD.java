package net.onixary.shapeShifterCurseFabric.player_form.old.instinct;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public class InstinctDebugHUD {
    public static void register() {

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            if (MinecraftClient.getInstance().player == null) return;

            int baseX = 240;
            int baseY = 150;

            PlayerEntity player = MinecraftClient.getInstance().player;
            PlayerInstinctComponent comp = RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.get(player);
            String text = String.format("Instinct: %.3f (+%.3f/s)",
                    comp.instinctValue,
                    comp.currentInstinctRate * 20);

            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.scale(0.5f, 0.5f, 1.0f); // 缩放为 75% 大小


            context.drawTextWithShadow(
                    MinecraftClient.getInstance().textRenderer,
                    text,
                    (int) (baseX / 0.5f),
                    (int) (baseY / 0.5f),
                    0xFFFFFF
            );
            matrices.pop();

        });
    }
}
