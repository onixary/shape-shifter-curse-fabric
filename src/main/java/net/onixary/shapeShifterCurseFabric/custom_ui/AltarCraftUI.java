package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.AltarBlockEntity;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AltarCraftUI extends HandledScreen<AltarCraftUIHandler> {

    private static final Identifier BACKGROUND = new Identifier(MOD_ID,"textures/gui/altar_craft_ui.png");
    private static final int WIDTH = 174;
    private static final int HEIGHT = 166;
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int PROCESS_BAR_ORIG_X = 84;
    private static final int PROCESS_BAR_ORIG_Y = 39;
    private static final int PROCESS_BAR_FULL_X = 174;
    private static final int PROCESS_BAR_FULL_Y = 0;
    private static final int PROCESS_BAR_W = 43;
    private static final int PROCESS_BAR_H = 9;
    private static final int FUEL_BAR_ORIG_X = 84;
    private static final int FUEL_BAR_ORIG_Y = 49;
    private static final int FUEL_BAR_FULL_X = 174;
    private static final int FUEL_BAR_FULL_Y = 9;
    private static final int FUEL_BAR_W = 43;
    private static final int FUEL_BAR_H = 3;
    private int baseX;
    private int baseY;

    // 90,60,54,10

    public AltarCraftUI(AltarCraftUIHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        // 藏的还挺深 要不是我修槽位偏移我都不知道这个
        this.backgroundWidth = WIDTH;
        this.backgroundHeight = HEIGHT;
    }

    protected void init() {
        super.init();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        baseX = width / 2 - WIDTH / 2;
        baseY = height / 2 - HEIGHT / 2;
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        this.drawProcess(context);
        this.drawFuel(context);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(BACKGROUND, baseX, baseY, 0, 0, WIDTH, HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    public void drawProcess(DrawContext context) {
        AltarCraftUIHandler uiHandler = this.getScreenHandler();
        int maxProgress = uiHandler.getMaxProgress();
        if (maxProgress > 0) {
            int ProcessWidth = (int) (PROCESS_BAR_W * ((float) uiHandler.getNowProgress() / (float) maxProgress));
            context.drawTexture(BACKGROUND, baseX + PROCESS_BAR_ORIG_X, baseY + PROCESS_BAR_ORIG_Y, PROCESS_BAR_FULL_X, PROCESS_BAR_FULL_Y, ProcessWidth, PROCESS_BAR_H, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    public void drawFuel(DrawContext context) {
        AltarCraftUIHandler uiHandler = this.getScreenHandler();
        int maxFuel = AltarBlockEntity.maxFuel;
        if (maxFuel > 0) {
            int FuelWidth = (int) (FUEL_BAR_W * ((float) uiHandler.getNowFuel() / (float) maxFuel));
            context.drawTexture(BACKGROUND, baseX + FUEL_BAR_ORIG_X, baseY + FUEL_BAR_ORIG_Y, FUEL_BAR_FULL_X, FUEL_BAR_FULL_Y, FuelWidth, FUEL_BAR_H, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }
}
