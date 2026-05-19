package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class FormColorSelectMenu extends Screen {
    public static final Identifier texture = new Identifier(MOD_ID,"textures/gui/form_color_select_menu.png");
    private static final int BG_WIDTH = 470;
    private static final int BG_HEIGHT = 247;

    public FormColorSelectMenu(Text title) {
        super(title);
    }

    public void renderTextureBackground(DrawContext context) {
        int BG_X = width / 2 - BG_WIDTH / 2;
        int BG_Y = height / 2 - BG_HEIGHT / 2;
        context.drawTexture(texture, BG_X, BG_Y, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        this.renderTextureBackground(context);
    }
}
