package net.onixary.shapeShifterCurseFabric.custom_ui.ui_part;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class FCS_ButtonWidget extends ButtonWidget {
    public final Identifier WIDGETS_TEXTURE = ShapeShifterCurseFabric.identifier("textures/gui/form_color_select_menu_part.png");
    public int TEXTURE_X = 0;
    public int ButtonType = -1;
    public int Index = -1;


    public FCS_ButtonWidget(int x, int y, Text message, PressAction onPress, NarrationSupplier narrationSupplier, int TEXTURE_X) {
        super(x, y, 15, 15, message, onPress, narrationSupplier);
        this.TEXTURE_X = TEXTURE_X;
    }

    private int getTextureY() {
        int i = 0;
        if (!this.active) {
            i = 2;
        } else if (this.isSelected()) {
            i = 1;
        }
        return i * 15;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), TEXTURE_X, this.getTextureY(), 15, 15, 45, 45);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.active ? 16777215 : 10526880;
        this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
}
