package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.AlterBlockEntity;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AlterCraftUI extends HandledScreen<AlterCraftUIHandler> {

    private static final Identifier BACKGROUND = new Identifier(MOD_ID,"textures/gui/alter_craft_ui.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;
    private int baseX;
    private int baseY;

    // 90,60,54,5
    // 90,65,54,5

    public AlterCraftUI(AlterCraftUIHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
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
        this.drawBar(context);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(BACKGROUND, baseX, baseY, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
    }

    public void drawBar(DrawContext context) {
        AlterCraftUIHandler uiHandler = this.getScreenHandler();
        int maxProgress = uiHandler.getMaxProgress();
        if (maxProgress > 0) {
            int ProcessWidth = (int) (54 * ((float) uiHandler.getNowProgress() / (float) maxProgress));
            context.fill(baseX + 90, baseY + 65, baseX + 90 + ProcessWidth, baseY + 65 + 5, 0xFF00FF00);
        }
        int maxFuel = AlterBlockEntity.maxFuel;
        if (maxFuel > 0) {
            int FuelWidth = (int) (54 * ((float) uiHandler.getNowFuel() / (float) maxFuel));
            context.fill(baseX + 90, baseY + 60, baseX + 90 + FuelWidth, baseY + 60 + 5, 0xFFFF00FF);
        }
    }
}
