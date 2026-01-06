package net.onixary.shapeShifterCurseFabric.mana;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.UIPositionUtils;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

@Environment(EnvType.CLIENT)
public class FamiliarFoxManaBar implements IManaRender{
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final Identifier BarTexFullID = new Identifier(MOD_ID, "textures/gui/familiar_fox_mana_bar_full.png");
    private static final Identifier BarTexEmptyID = new Identifier(MOD_ID, "textures/gui/familiar_fox_mana_bar_empty.png");

    @Override
    public boolean OverrideInstinctBar() {
        return false;
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (!mc.options.hudHidden) {
            // int width = mc.getWindow().getScaledWidth();
            // int height = mc.getWindow().getScaledHeight();
            // //float x = (float) width / 2 + 11;
            // float x = (float)width / 2 + 100;
            // // 39 is the height of the health bar
            // float y = height - 39;
            // y += 22;
            // todo: instinct条的位置为 y += 30
            Pair<Integer, Integer> pos = UIPositionUtils.getCorrectPosition(ShapeShifterCurseFabric.clientConfig.familiarFoxManaBarPosType, ShapeShifterCurseFabric.clientConfig.familiarFoxManaBarPosOffsetX, ShapeShifterCurseFabric.clientConfig.familiarFoxManaBarPosOffsetY);
            this.renderBar(context, tickDelta, pos.getLeft(), pos.getRight());
        }
    }

    private void renderBar(DrawContext context, float tickDelta, int x, int y) {
        int instinctWidth = (int) Math.ceil(80 * ManaUtils.getPlayerManaPercent(mc.player, 0.0d));
        context.drawTexture(BarTexEmptyID, x, y, 0, 0, 80, 5, 80, 5);
        context.drawTexture(BarTexFullID, x, y, 0, 0, instinctWidth, 5, 80, 5);
    }
}
