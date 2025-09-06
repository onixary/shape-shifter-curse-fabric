package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.Text;

import java.util.Objects;

public class ScaleMultilineTextWidget extends MultilineTextWidget {
    private final float Scale;
    public ScaleMultilineTextWidget(int x, int y, Text message, TextRenderer textRenderer, float Scale) {
        super(x, y, message, textRenderer);
        this.Scale = Scale;
    }

    public MultilineTextWidget setMaxWidth(int maxWidth) {
        super.setMaxWidth(Math.round(maxWidth * (1 / this.Scale)));
        return this;
    }

    public int getWidth() {
        return (int) (super.getWidth() * this.Scale);
    }

    public int getHeight() {
        return (int) (super.getHeight() * this.Scale);
    }

    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        MultilineText multilineText = (MultilineText)this.cacheKeyToText.map(this.getCacheKey());
        int i = this.getX();
        int j = this.getY();
        Objects.requireNonNull(this.getTextRenderer());
        int k = Math.round(9 * this.Scale);
        int l = this.getTextColor();
        if (this.centered) {
            multilineText.drawCenterWithShadow(context, i + this.getWidth() / 2, j, k, l);
        } else {
            multilineText.drawWithShadow(context, i, j, k, l);
        }
    }
}
