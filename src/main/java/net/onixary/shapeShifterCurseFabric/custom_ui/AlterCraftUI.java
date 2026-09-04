package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AlterCraftUI extends HandledScreen<AlterCraftUIHandler> {

    private static final Identifier BACKGROUND = new Identifier(MOD_ID,"textures/gui/alter_craft_ui.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public AlterCraftUI(AlterCraftUIHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    protected void init() {
        super.init();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int TexturePosX = width / 2 - WIDTH / 2;
        int TexturePosY = height / 2 - HEIGHT / 2;
        context.drawTexture(BACKGROUND, TexturePosX, TexturePosY, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
    }
}
