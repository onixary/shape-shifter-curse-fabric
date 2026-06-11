package net.onixary.shapeShifterCurseFabric.player_form.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.util.UIPositionUtils;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class InstinctBarRenderer {
    private static final Identifier instinctBarID = new Identifier(MOD_ID, "textures/gui/instinct_bar.png");
    private static final float increase1Threshold = StaticParams.INSTINCT_INCREASE_RATE + 0.005f;
    private static final float increase2Threshold = StaticParams.INSTINCT_INCREASE_RATE + 0.01f;
    private static final float increase3Threshold = StaticParams.INSTINCT_INCREASE_RATE + 0.1f;
    private static int currentBarY = 0;

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public void render(DrawContext context, float tickDelta) {
        if (MinecraftClient.getInstance().player == null) return;
        PlayerEntity player = MinecraftClient.getInstance().player;
        IForm curForm = FormUtils.getPlayerForm(player);
        boolean showInstinctBar = !FormUtils.NoInstinct.hasFlag(curForm);
        if (!mc.options.hudHidden && mc.interactionManager != null && mc.interactionManager.hasStatusBars() && showInstinctBar) {
            Pair<Integer, Integer> pos = UIPositionUtils.getCorrectPosition(ShapeShifterCurseFabric.clientConfig.instinctBarPosType, ShapeShifterCurseFabric.clientConfig.instinctBarPosOffsetX, ShapeShifterCurseFabric.clientConfig.instinctBarPosOffsetY);
            updateBarTextures(player);
            renderInstinctBar(context, tickDelta, pos.getLeft(), pos.getRight(), player);
        }
    }

    public void updateBarTextures(PlayerEntity player) {
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        float rate = component.instinctRate;
        float baseRate = InstinctUtils.getBaseInstinctRate(player);
        if (rate > increase3Threshold) {
            currentBarY = 30;
        } else if (rate > increase2Threshold) {
            currentBarY = 25;
        } else if (rate > increase1Threshold) {
            currentBarY = 20;
        } else if (rate > baseRate) {
            currentBarY = 15;
        } else if (rate < 0) {
            currentBarY = 0;
        } else {
            currentBarY = 5;
        }
    }

    private void renderInstinctBar(DrawContext context, float tickDelta, int x, int y, PlayerEntity player) {
        float instinctValue = InstinctUtils.getNowInstinct();
        float currentInstinct = Math.max(0.0f, Math.min(instinctValue, StaticParams.INSTINCT_MAX));
        float instinctProportion;
        IForm curForm = FormUtils.getPlayerForm(player);
        boolean isInstinctLock = FormUtils.LockInstinct.hasFlag(curForm);
        instinctProportion = currentInstinct / StaticParams.INSTINCT_MAX;
        int instinctWidth = (int) Math.ceil(80 * instinctProportion);
        context.drawTexture(instinctBarID, x, y, 0, currentBarY, 80 - instinctWidth, 5, 160, 40);
        context.drawTexture(instinctBarID, x + 80 - instinctWidth, y, 160 - instinctWidth, currentBarY, instinctWidth, 5, 160, 40);
        if (isInstinctLock) {
            context.drawTexture(instinctBarID, x, y, 0, 35, 80, 5, 160, 40);
        }
    }
}
