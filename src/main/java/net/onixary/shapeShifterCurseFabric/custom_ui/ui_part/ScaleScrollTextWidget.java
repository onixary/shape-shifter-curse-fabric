package net.onixary.shapeShifterCurseFabric.custom_ui.ui_part;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;

public class ScaleScrollTextWidget extends MultilineTextWidget {
    private final float Scale;
    private boolean shadow;

    private int MaxWidth;
    private int MaxRows;

    private boolean scrollable = false;
    private List<OrderedText> texts;
    private List<OrderedText> currentTexts;
    private int scroll = 0;

    public ScaleScrollTextWidget(int x, int y, int width, int maxRow, float Scale, Text message, TextRenderer textRenderer) {
        super(x, y, message, textRenderer);
        this.Scale = Scale;
        this.setMaxWidth(width);
        this.setMaxRows(maxRow);
        this.calculateText();
    }

    @Override
    public void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
        // TODO 完成onDrag
    }

    public void onScroll(double mouseX, double mouseY, double amount) {
        // TODO 完成onScroll(需要在Screen里挂载)
    }

    private void calculateCurrentText() {
        this.currentTexts = this.texts.subList(this.scroll, this.scroll + this.MaxRows);
    }

    private void calculateText() {
        this.texts = this.getTextRenderer().wrapLines(this.getMessage(), this.getWidth());
        if (this.texts.size() > this.MaxRows) {
            this.scrollable = true;
        }
        this.calculateCurrentText();
    }

    public boolean shadow() {
        return this.shadow;
    }

    public void reloadText(Text message) {
        this.setMessage(message);
        this.calculateText();
        this.scroll = 0;
    }

    public void scroll(int amount) {
        this.scroll += amount;
        if (this.scroll < 0) {
            this.scroll = 0;
        }
        if (this.scroll > this.texts.size() - this.MaxRows) {
            this.scroll = this.texts.size() - this.MaxRows;
        }
        this.calculateCurrentText();
    }

    @Override
    public MultilineTextWidget setMaxWidth(int maxWidth) {
        this.MaxWidth = Math.round(maxWidth * (1 / this.Scale));
        super.setMaxWidth(this.MaxWidth);
        return this;
    }

    @Override
    public MultilineTextWidget setMaxRows(int maxRows) {
        this.MaxRows = Math.round(maxRows * (1 / this.Scale));
        super.setMaxRows(this.MaxRows);
        return this;
    }

    @Override
    public int getWidth() {
        return (int) (super.getWidth() * this.Scale);
    }

    @Override
    public int getHeight() {
        return (int) (super.getHeight() * this.Scale);
    }

    private int drawCenterWithShadow(DrawContext context, List<OrderedText> lines, int x, int y, int lineHeight, int color) {
        int i = y;
        TextRenderer textRenderer = this.getTextRenderer();
        for(OrderedText line : lines) {
            context.drawTextWithShadow(textRenderer, line, x - textRenderer.getWidth(line) / 2, i, color);
            i += lineHeight;
        }
        return i;
    }

    public int drawWithShadow(DrawContext context, List<OrderedText> lines, int x, int y, int lineHeight, int color) {
        int i = y;
        TextRenderer textRenderer = this.getTextRenderer();
        for(OrderedText line : lines) {
            context.drawTextWithShadow(textRenderer, line, x, i, color);
            i += lineHeight;
        }

        return i;
    }

    public int drawWithOutShadow(DrawContext context, List<OrderedText> lines, int x, int y, int lineHeight, int color) {
        int i = y;
        TextRenderer textRenderer = this.getTextRenderer();
        for(OrderedText line : lines) {
            context.drawText(textRenderer, line, x, i, color, false);
            i += lineHeight;
        }

        return i;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = this.getX();
        int j = this.getY();
        Objects.requireNonNull(this.getTextRenderer());
        int k = Math.round(9 * this.Scale);
        int l = this.getTextColor();
        if (this.centered) {
            this.drawCenterWithShadow(context, this.currentTexts, i + this.getWidth() / 2, j, k, l);
        } else {
            if(this.shadow){
                this.drawWithShadow(context, this.currentTexts, i, j, k, l);
            }
            else{
                this.drawWithOutShadow(context, this.currentTexts, i, j, k, l);
            }
        }
    }
}
