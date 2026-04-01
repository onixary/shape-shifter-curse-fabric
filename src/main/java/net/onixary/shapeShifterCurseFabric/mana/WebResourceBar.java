package net.onixary.shapeShifterCurseFabric.mana;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.UIPositionUtils;

public class WebResourceBar implements IManaRender {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier BarTexID = ShapeShifterCurseFabric.identifier("textures/gui/web_bar.png");

    @Override
    public boolean OverrideInstinctBar() {
        return false;
    }

    public void render(DrawContext context, float tickDelta) {
        if (!mc.options.hudHidden) {
            Pair<Integer, Integer> pos = UIPositionUtils.getCorrectPosition(ShapeShifterCurseFabric.clientConfig.manaBarPosType, ShapeShifterCurseFabric.clientConfig.manaBarPosOffsetX, ShapeShifterCurseFabric.clientConfig.manaBarPosOffsetY);
            this.renderBar(context, tickDelta, (Integer)pos.getLeft(), (Integer)pos.getRight());
        }
    }

    public int getChargeLevel() {
        // TODO 之后写完蓄力power后补上
        return 0;
    }

    private void renderBar(DrawContext context, float tickDelta, int x, int y) {
        if (mc.player == null) {
            return;
        }
        double mana = ManaUtils.getPlayerMana(mc.player);
        double maxMana = ManaUtils.getPlayerMaxMana(mc.player);
        double manaRegen = ManaUtils.getPlayerManaRegen(mc.player);
        int remainTicks = -1;

        if (manaRegen > (double)0.0F) {
            remainTicks = (int)Math.ceil((maxMana - mana) / manaRegen);
        } else if (manaRegen < (double)0.0F) {
            remainTicks = (int)Math.ceil((mana) / (-manaRegen));
        } else {
            remainTicks = 0;
        }

        int manaWidth = (int)Math.ceil((double)80.0F * ManaUtils.getManaPercent(mana, maxMana, (double)0.0F));
        context.drawTexture(BarTexID, x, y, 0.0f, 0, 80, 5, 80, 18);
        context.drawTexture(BarTexID, x, y, 0.0f, 5, manaWidth, 5, 80, 18);
        StringBuilder manaString = new StringBuilder();
        manaString.append((int)mana).append("/").append((int)maxMana);
        if (remainTicks > 0) {
            manaString.append(" (").append(remainTicks).append(")");
        } else if (remainTicks < 0) {
            manaString.append(" (").append("?").append(")");
        } else {
            manaString.append(" (").append("∞").append(")");
        }

        Text manaText = Text.literal(manaString.toString());
        context.drawText(mc.textRenderer, manaText, x + 10, y - 8, manaRegen == 0 ? 0xFF7F7F7F : 0xFF00CFFF, false);

        int chargeLevel = this.getChargeLevel();
        context.drawTexture(BarTexID, x, y - 8, chargeLevel * 8f, 10f, 8, 8, 80, 18);
    }
}
