package net.onixary.shapeShifterCurseFabric.status_effects;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;

// 暂时弃用，描述放在书页UI中
public class TStatusHUDHandler {
    public static void register(){
        // 在原版效果列表下方添加描述
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof InventoryScreen) {
                ScreenEvents.afterRender(screen).register((_screen, context, mouseX, mouseY, delta) -> {
                    if (MinecraftClient.getInstance().player == null) return;

                    // 原版状态栏的起始坐标（需根据版本调整）
                    int baseX = width - 120;
                    int baseY = (int)(30 / 0.5);

                    // 遍历所有药水效果
                    int index = 0;
                    for (StatusEffectInstance effect : MinecraftClient.getInstance().player.getStatusEffects()) {
                        if (effect.getEffectType() instanceof BaseTransformativeStatusEffect) {
                            Text description = Text.translatable(effect.getTranslationKey() + ".description");

                            MatrixStack matrices = context.getMatrices();
                            matrices.push();
                            matrices.scale(0.5f, 0.5f, 1.0f); // 缩放为 75% 大小
                            // 计算 Y 坐标（每个效果间隔 20 像素）
                            int y = baseY + (index * (int)(20 / 0.5));

                            context.drawTextWithShadow(
                                    client.textRenderer,
                                    description,
                                    (int)(baseX / 0.5f),
                                    (int)(y / 0.5f),
                                    0xFFFFFF
                            );
                            matrices.pop();
                        }
                        index++;
                    }
                });
            }
        });
    }
}
