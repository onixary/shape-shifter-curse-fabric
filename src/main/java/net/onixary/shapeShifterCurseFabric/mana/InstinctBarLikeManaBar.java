package net.onixary.shapeShifterCurseFabric.mana;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.UIPositionUtils;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

@Environment(EnvType.CLIENT)
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
        if (!mc.options.hudHidden) {
            Pair<Integer, Integer> pos = UIPositionUtils.getCorrectPosition(ShapeShifterCurseFabric.clientConfig.instinctBarPosType, ShapeShifterCurseFabric.clientConfig.instinctBarPosOffsetX, ShapeShifterCurseFabric.clientConfig.instinctBarPosOffsetY);
            this.renderBar(context, tickDelta, pos.getLeft(), pos.getRight());
        }
    }

    private void renderBar(DrawContext context, float tickDelta, int x, int y) {
        double mana = ManaUtils.getPlayerMana(mc.player);
        double maxMana = ManaUtils.getPlayerMaxMana(mc.player);
        double manaRegen = ManaUtils.getPlayerManaRegen(mc.player);
        int remainTicks = -1;
        if (manaRegen > 0) {
            remainTicks = (int) Math.ceil((maxMana - mana) / manaRegen);
        }
        int instinctWidth = (int) Math.ceil(80 * ManaUtils.getManaPercent(mana, maxMana, 0.0d));
        context.drawTexture(BarTexEmptyID, x, y, 0, 0, 80, 5, 80, 5);
        context.drawTexture(BarTexFullID, x, y, 0, 0, instinctWidth, 5, 80, 5);
        StringBuilder manaString = new StringBuilder();
        manaString.append((int) mana).append("/").append((int) maxMana);
        if (remainTicks > 0) {
            manaString.append(" (").append(remainTicks).append(")");
        } else if (remainTicks < 0) {
            manaString.append(" (").append("?").append(")");
        }
        Text manaText = Text.literal(manaString.toString());
        int manaTextWidth = mc.textRenderer.getWidth(manaText);
        context.drawText(mc.textRenderer, manaText, x + (80 - manaTextWidth) / 2, y - 2, 0xFFFFFF, false);
    }
}
