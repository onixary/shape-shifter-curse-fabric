package net.onixary.shapeShifterCurseFabric.mana;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.ChargePower;
import net.onixary.shapeShifterCurseFabric.util.UIPositionUtils;

public class WebResourceBar implements IManaRender {
    private ChargePower powerTemp;
    private int powerTempTimer = 0;

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
        // 每帧查一次有点费性能 还是每60帧查一次吧(渲染帧)
        if (powerTempTimer > 60) {
            powerTemp = null;
            for (ChargePower power : PowerHolderComponent.getPowers(mc.player, ChargePower.class)) {
                if (ShapeShifterCurseFabric.identifier("web_charge").equals(power.chargePowerID)) {
                    powerTemp = power;
                    break;
                }
            }
            powerTempTimer = 0;
        }
        powerTempTimer++;
        if (powerTemp != null) {
            return powerTemp.renderTier;
        }
        return 0;
    }

    private void renderBar(DrawContext context, float tickDelta, int x, int y) {
        if (mc.player == null) {
            return;
        }
        double mana = ManaUtils.getPlayerMana(mc.player);
        double maxMana = ManaUtils.getPlayerMaxMana(mc.player);
        double manaRegen = ManaUtils.getPlayerManaRegen(mc.player);

        int manaWidth = (int)Math.ceil((double)80.0F * ManaUtils.getManaPercent(mana, maxMana, (double)0.0F));
        context.drawTexture(BarTexID, x, y, 0.0f, 0, 80, 5, 80, 18);
        context.drawTexture(BarTexID, x, y, 0.0f, 5, manaWidth, 5, 80, 18);

        Text manaText = Text.literal((int) mana + "/" + (int) maxMana);
        context.drawText(mc.textRenderer, manaText, x + 10, y - 8, manaRegen == 0 ? 0xFF7F7F7F : 0xFF00CFFF, false);

        int chargeLevel = this.getChargeLevel();
        context.drawTexture(BarTexID, x, y - 8, chargeLevel * 8f, 10f, 8, 8, 80, 18);
    }
}
