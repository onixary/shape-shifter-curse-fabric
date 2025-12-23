package net.onixary.shapeShifterCurseFabric.mana;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class InstinctBarLikeManaBar implements IManaRender{
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final Identifier BarTexFullID = new Identifier(MOD_ID, "textures/gui/instinct_bar_full.png");
    private static final Identifier BarTexEmptyID = new Identifier(MOD_ID, "textures/gui/instinct_bar_empty.png");

    @Override
    public boolean OverrideInstinctBar() {
        return true;
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (!mc.options.hudHidden && mc.interactionManager != null && mc.interactionManager.hasStatusBars()) {
            int width = mc.getWindow().getScaledWidth();
            int height = mc.getWindow().getScaledHeight();
            //float x = (float) width / 2 + 11;
            float x = (float)width / 2 - 40;
            // 39 is the height of the health bar
            float y = height - 39;
            y += 16;
            this.renderBar(context, tickDelta, (int) x, (int) y);
        }
    }

    private void renderBar(DrawContext context, float tickDelta, int x, int y) {
        int instinctWidth = (int) Math.ceil(80 * ManaUtils.getPlayerManaPercent(mc.player));
        context.drawTexture(BarTexEmptyID, x, y, 0, 0, 80 - instinctWidth, 5, 80, 5);
        context.drawTexture(BarTexFullID, x + 80 - instinctWidth, y, 80 - instinctWidth, 0, instinctWidth, 5, 80, 5);
    }
}
