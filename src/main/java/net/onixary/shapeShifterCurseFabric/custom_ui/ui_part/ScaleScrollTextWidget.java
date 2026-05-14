package net.onixary.shapeShifterCurseFabric.custom_ui.ui_part;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScaleScrollTextWidget extends MultilineTextWidget implements WidgetEXUtils.IWidgetEX {
    private final float Scale;
    private boolean shadow;


    private int realWidth;
    private int realHeight;
    private int MaxWidth;
    private int MaxRows;

    private boolean textDone = false;

    private final List<WidgetEXUtils.IWidgetEX> widgetList = List.of();
    private WidgetEXUtils.WidgetRect rect;

    private List<OrderedText> texts = new ArrayList<>();
    private List<OrderedText> currentTexts = new ArrayList<>();

    public boolean enableScrollableIconRender = false;
    public int IconSize = 8;
    public Identifier IconTexID = ShapeShifterCurseFabric.identifier("textures/gui/scrollable_icon.png");

    public int textsLineCount = 0;
    public int scroll = 0;

    public ScaleScrollTextWidget(int x, int y, int width, int maxRow, float Scale, Text message, TextRenderer textRenderer) {
        super(x, y, message, textRenderer);
        this.Scale = Scale;
        this.rect = new WidgetEXUtils.WidgetRect(x, y, width, maxRow * 9);
        assert width > 0;
        assert maxRow > 0;
        this.setMaxWidth(width);
        this.setMaxRows(maxRow);
        this.calculateText();
    }

    @Override
    public WidgetEXUtils.WidgetRect getRect() {
        return this.rect;
    }

    @Override
    public List<WidgetEXUtils.IWidgetEX> getWidgetList() {
        return this.widgetList;
    }


    private double deltaYTotal = 0;
    private double scrollZTotal = 0;

    @Override
    public void onClickWidget(double mouseX, double mouseY, int button) {
        if (this.enableScrollableIconRender) {
            if (mouseX >= this.realWidth - IconSize && mouseX <= this.realWidth && mouseY >= 0 && mouseY < IconSize) {
                this.scroll(-this.MaxRows);
            }
            if (mouseX >= this.realWidth - IconSize && mouseX <= this.realWidth && mouseY >= this.realHeight - IconSize && mouseY < this.realHeight) {
                this.scroll(this.MaxRows);
            }
        }
    }

    @Override
    public void onDragWidget(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.enableScrollableIconRender && mouseX >= this.realWidth) {
            return;
        }
        deltaYTotal += deltaY;
        if (deltaYTotal > 9 || deltaYTotal < -9) {
            int amount = (int) (deltaYTotal / 9);
            deltaYTotal -= amount * 9;
            this.scroll(-amount);
        }
    }

    @Override
    public void onScrollWidget(double mouseX, double mouseY, double mouseZ) {
        if (this.enableScrollableIconRender && mouseX >= this.realWidth) {
            return;
        }
        scrollZTotal += mouseZ;
        if (scrollZTotal > 0.5f || scrollZTotal < -0.5f) {
            int amount = (int) (scrollZTotal * 2);
            scrollZTotal -= amount * 0.5f;
            this.scroll(-amount);
        }
    }

    private void calculateCurrentText() {
        if (this.texts.size() < this.scroll + this.MaxRows) {
            this.currentTexts = this.texts.subList(this.scroll, this.texts.size());
        } else {
            this.currentTexts = this.texts.subList(this.scroll, this.scroll + this.MaxRows);
        }
    }

    private void calculateText() {
        try {
            this.texts = this.getTextRenderer().wrapLines(this.getMessage(), this.getTextWidth());
            this.textsLineCount = this.texts.size();
            this.calculateCurrentText();
            this.textDone = true;
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Error while calculating text", e);
        }
    }

    public ScaleScrollTextWidget shadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public ScaleScrollTextWidget setEnableScrollableIconRender(boolean enableScrollableIconRender) {
        if (this.enableScrollableIconRender != enableScrollableIconRender) {
            if (enableScrollableIconRender) {
                this.modMaxWidth(-IconSize);
            } else {
                this.modMaxWidth(0);
            }
            this.enableScrollableIconRender = enableScrollableIconRender;
            this.reloadText();
        }
        return this;
    }

    public void reloadText() {
        this.textDone = false;
        this.calculateText();
        this.scroll = 0;
    }

    public void reloadText(Text message) {
        this.setMessage(message);
        this.reloadText();
    }

    public void scroll(int amount) {
        if (!this.textDone) {
            this.calculateText();
        }
        this.scroll += amount;
        if (this.scroll > this.texts.size() - this.MaxRows) {
            this.scroll = this.texts.size() - this.MaxRows;
        }
        if (this.scroll < 0) {
            this.scroll = 0;
        }
        this.calculateCurrentText();
    }

    public int modMaxWidth = 0;

    public void modMaxWidth(int value) {
        this.modMaxWidth = value;
        super.setMaxWidth(this.MaxWidth + this.modMaxWidth);
    }

    @Override
    public MultilineTextWidget setMaxWidth(int maxWidth) {
        this.realWidth = maxWidth;
        this.MaxWidth = Math.round(maxWidth * (1 / this.Scale));
        super.setMaxWidth(this.MaxWidth + this.modMaxWidth);
        return this;
    }

    @Override
    public MultilineTextWidget setMaxRows(int maxRows) {
        this.realHeight = maxRows * 9;
        this.MaxRows = Math.round(maxRows * (1 / this.Scale));
        super.setMaxRows(this.MaxRows);
        return this;
    }

    @Override
    public int getWidth() {
        return (int) ((this.MaxWidth + this.modMaxWidth) * this.Scale);
    }

    public int getTextWidth() {
        return (int) (this.MaxWidth + this.modMaxWidth);
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
        if (!this.textDone) {
            this.calculateText();
        }
        int i = this.getX();
        int j = this.getY();
        if (this.enableScrollableIconRender) {
            if (this.scroll > 0) {
                context.drawTexture(IconTexID, i + realWidth - IconSize, j, 0, 0, IconSize, IconSize, IconSize, IconSize * 2);
            }
            if (this.scroll < this.texts.size() - this.MaxRows) {
                context.drawTexture(IconTexID, i + realWidth - IconSize, j + realHeight - IconSize, 0, IconSize, IconSize, IconSize, IconSize, IconSize * 2);
            }
        }
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
