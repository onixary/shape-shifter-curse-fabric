package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class InstinctBarRenderer{
    private static final Identifier instinctBarTexFullID = new Identifier(MOD_ID, "textures/gui/instinct_bar_full.png");
    private static final Identifier instinctBarTexEmptyID = new Identifier(MOD_ID, "textures/gui/instinct_bar_empty.png");
    private static final Identifier instinctBarTexIncrease0ID = new Identifier(MOD_ID, "textures/gui/instinct_bar_increase0.png");
    private static final Identifier instinctBarTexIncrease0EmptyID = new Identifier(MOD_ID, "textures/gui/instinct_bar_increase0_empty.png");
    private static final Identifier instinctBarTexIncrease1ID = new Identifier(MOD_ID, "textures/gui/instinct_bar_increase1.png");
    private static final Identifier instinctBarTexIncrease1EmptyID = new Identifier(MOD_ID, "textures/gui/instinct_bar_increase1_empty.png");
    private static final Identifier instinctBarTexIncrease2ID = new Identifier(MOD_ID, "textures/gui/instinct_bar_increase2.png");
    private static final Identifier instinctBarTexIncrease2EmptyID = new Identifier(MOD_ID, "textures/gui/instinct_bar_increase2_empty.png");
    private static final Identifier instinctBarTexIncrease3ID = new Identifier(MOD_ID, "textures/gui/instinct_bar_increase3.png");
    private static final Identifier instinctBarTexIncrease3EmptyID = new Identifier(MOD_ID, "textures/gui/instinct_bar_increase3_empty.png");
    private static final Identifier instinctBarTexDecreaseID = new Identifier(MOD_ID, "textures/gui/instinct_bar_decrease.png");
    private static final Identifier instinctBarTexDecreaseEmptyID = new Identifier(MOD_ID, "textures/gui/instinct_bar_decrease_empty.png");
    private static final Identifier instinctBarTexLockID = new Identifier(MOD_ID, "textures/gui/instinct_bar_lock.png");
    private static final Identifier cursedMoonLogoTexID = new Identifier(MOD_ID, "textures/gui/cursed_moon_logo.png");

    private static Identifier currentBarID = instinctBarTexFullID;
    private static Identifier currentBarEmptyID = instinctBarTexEmptyID;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final float increase1Threshold = StaticParams.INSTINCT_INCREASE_RATE_0 + 0.005f;
    private static final float increase2Threshold = StaticParams.INSTINCT_INCREASE_RATE_0 + 0.01f;
    private static final float increase3Threshold = StaticParams.INSTINCT_INCREASE_RATE_0 + 0.1f;

    public void render(DrawContext context, float tickDelta) {
        if (MinecraftClient.getInstance().player == null) return;

        PlayerEntity player = MinecraftClient.getInstance().player;
        PlayerForms curForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        boolean showInstinctBar = !(curForm == PlayerForms.ORIGINAL_BEFORE_ENABLE || curForm == PlayerForms.ORIGINAL_SHIFTER);

        if (!mc.options.hudHidden
                && mc.interactionManager != null
                && mc.interactionManager.hasStatusBars()
                && showInstinctBar) {
            int width = mc.getWindow().getScaledWidth();
            int height = mc.getWindow().getScaledHeight();
            float x = (float) width / 2 + 11;
            // 39 is the height of the health bar
            float y = height - 39;
            y -= 8;
            updateBarTextures(player);
            renderInstinctBar(context, tickDelta, (int) x, (int) y, player);
        }
    }

    public void updateBarTextures(PlayerEntity player) {
        if (!InstinctTicker.isInstinctIncreasing && !InstinctTicker.isInstinctDecreasing) {
            currentBarID = instinctBarTexFullID;
            currentBarEmptyID = instinctBarTexEmptyID;
        }
        else if (InstinctTicker.isInstinctIncreasing && !InstinctTicker.isInstinctDecreasing) {
            // 判断增长速率并应用不同的贴图
            if (InstinctTicker.currentInstinctRate < increase1Threshold) {
                currentBarID = instinctBarTexIncrease0ID;
                currentBarEmptyID = instinctBarTexIncrease0EmptyID;
            }
            else if (InstinctTicker.currentInstinctRate < increase2Threshold) {
                currentBarID = instinctBarTexIncrease1ID;
                currentBarEmptyID = instinctBarTexIncrease1EmptyID;
            }
            else if (InstinctTicker.currentInstinctRate < increase3Threshold) {
                currentBarID = instinctBarTexIncrease2ID;
                currentBarEmptyID = instinctBarTexIncrease2EmptyID;
            }
            else {
                currentBarID = instinctBarTexIncrease3ID;
                currentBarEmptyID = instinctBarTexIncrease3EmptyID;
            }
        }
        else{
            currentBarID = instinctBarTexDecreaseID;
            currentBarEmptyID = instinctBarTexDecreaseEmptyID;
        }
    }


    private void renderInstinctBar(DrawContext context, float tickDelta, int x, int y, PlayerEntity player) {
        float maxInstinct = StaticParams.INSTINCT_MAX;
        float currentInstinct = Math.min(InstinctTicker.currentInstinctValue, maxInstinct);
        // Calculate bar proportions
        float instinctProportion;

        //if (healthProportion > 1) healthProportion = 1F;
        instinctProportion = currentInstinct / maxInstinct;
        //if (healthProportion + intermediateProportion > 1) intermediateProportion = 1 - healthProportion;
        int instinctWidth = (int) Math.ceil(80 * instinctProportion);
        // Display empty part
        context.drawTexture(currentBarEmptyID,
                x, y,
                0, 0,
                80 - instinctWidth, 5,
                80, 5);
        context.drawTexture(currentBarID,
                x + 80 - instinctWidth, y,
                80 - instinctWidth, 0,
                instinctWidth, 5,
                80, 5);
        if(InstinctTicker.isInstinctLock){
            context.drawTexture(instinctBarTexLockID,
                    x, y,
                    0, 0,
                    80, 5,
                    80, 5);
        }
    }

}
