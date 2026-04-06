package net.onixary.shapeShifterCurseFabric.render.tech;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.additional_power.ItemStorePower;
import net.onixary.shapeShifterCurseFabric.util.UIPositionUtils;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.widget.ClickableWidget.WIDGETS_TEXTURE;

public class ItemStorePowerRender {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final List<ItemStorePower> tempPower = new ArrayList<ItemStorePower>();
    private static int timer = 0;
    private static final int MaxSlot = 12;
    private static final int SlotPerRow = 4;
    private static int NowCol = 0;
    private static int NowRow = 0;

    static {
        for (int i = 0; i < MaxSlot; i++) {
            tempPower.add(null);
        }
    }

    private static void timerTick() {
        if (timer > 60) {
            timer = 0;
            NowCol = 0;
            NowRow = 0;
            List<ItemStorePower> powers = PowerHolderComponent.getPowers(mc.player, ItemStorePower.class);
            for (ItemStorePower power : powers) {
                tempPower.set(power.Slot, power);
                NowCol = Math.max(NowCol, (power.Slot % SlotPerRow) + 1);
                NowRow = Math.max(NowRow, (power.Slot / SlotPerRow) + 1);
            }
        }
        timer++;
    }

    private static void renderSlot(DrawContext context, float tickDelta, ItemStorePower power) {
        Pair<Integer, Integer> SlotBegin = UIPositionUtils.getCorrectPosition(8, -120 - (NowCol * 20), -2 - (NowRow * 20));
        int SlotX = power.Slot % SlotPerRow;
        int SlotY = power.Slot / SlotPerRow;
        int SlotXFinal = SlotBegin.getLeft() + SlotX * 20;
        int SlotYFinal = SlotBegin.getRight() + SlotY * 20;
        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, -90.0f);
        context.drawTexture(WIDGETS_TEXTURE, SlotXFinal - 3, SlotYFinal - 4, 24, 22, 29, 24);
        context.getMatrices().pop();
        ItemStack stack = power.storedItem;
        if (stack.isEmpty()) {
            return;
        }
        float g = power.bobbingAnimationTime - tickDelta;
        if (g > 0.0f) {
            float h = 1.0f + g / 5.0f;
            context.getMatrices().push();
            context.getMatrices().translate(SlotXFinal + 8, SlotYFinal + 12, 0.0f);
            context.getMatrices().scale(1.0f / h, (h + 1.0f) / 2.0f, 1.0f);
            context.getMatrices().translate(-(SlotXFinal + 8), -(SlotYFinal + 12), 0.0f);
        }
        context.drawItem(mc.player, stack, SlotXFinal, SlotYFinal, power.Slot);
        if (g > 0.0f) {
            context.getMatrices().pop();
        }
        context.drawItemInSlot(mc.textRenderer, stack, SlotXFinal, SlotYFinal);
    }

    public static void render(DrawContext context, float tickDelta) {
        timerTick();
        if (!mc.options.hudHidden) {
            RenderSystem.enableBlend();
            for (ItemStorePower power : tempPower) {
                if (power != null) {
                    renderSlot(context, tickDelta, power);
                }
            }
            RenderSystem.disableBlend();
        }
    }
}
