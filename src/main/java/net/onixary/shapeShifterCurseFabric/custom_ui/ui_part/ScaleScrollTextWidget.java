package net.onixary.shapeShifterCurseFabric.custom_ui.ui_part;

import net.minecraft.client.font.MultilineText;
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
    private final float realScale;
    private boolean shadow;


    private int realWidth;
    private int MaxWidth;
    private int MaxRows;

    private int boxHeight = 0;

    private final List<WidgetEXUtils.IWidgetEX> widgetList = List.of();
    private WidgetEXUtils.WidgetRect rect;

    public boolean enableScrollableIconRender = false;
    public int IconSize = 8;
    public Identifier IconTexID = ShapeShifterCurseFabric.identifier("textures/gui/scrollable_icon.png");

    public int scroll = 0;  // 单位改成像素


    public ScaleScrollTextWidget(int x, int y, int width, int height, float Scale, Text message, TextRenderer textRenderer) {
        super(x, y, message, textRenderer);
        this.Scale = Scale;
        int textHeight = Math.round(textRenderer.fontHeight * Scale);
        this.realScale = (float) textHeight / (float) textRenderer.fontHeight;
        this.rect = new WidgetEXUtils.WidgetRect(x, y, width, height);
        assert width > 0;
        assert height > 0;
        this.setMaxWidth(width);
        this.setMaxRows(1_000_000_000);
        this.boxHeight = height;
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
                this.scroll(-this.boxHeight);
            }
            if (mouseX >= this.realWidth - IconSize && mouseX <= this.realWidth && mouseY >= this.boxHeight - IconSize && mouseY < this.boxHeight) {
                this.scroll(this.boxHeight);
            }
        }
    }

    @Override
    public void onDragWidget(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.enableScrollableIconRender && mouseX >= this.realWidth) {
            return;
        }
        deltaYTotal += deltaY;
        if (deltaYTotal > 1 || deltaYTotal < -1) {
            int amount = (int) (deltaYTotal);
            deltaYTotal -= amount;
            this.scroll(-amount);
        }
    }

    @Override
    public void onScrollWidget(double mouseX, double mouseY, double mouseZ) {
        if (this.enableScrollableIconRender && mouseX >= this.realWidth) {
            return;
        }
        scrollZTotal += mouseZ;
        if (scrollZTotal > 0.0625f || scrollZTotal < -0.0625f) {
            int amount = (int) (scrollZTotal * 16);
            scrollZTotal -= amount * 0.0625f;
            this.scroll(-amount);
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
            this.scroll = 0;
        }
        return this;
    }

    public void reloadText(Text message) {
        this.setMessage(message);
        this.scroll = 0;
    }

    public void scroll(int amount) {
        this.scroll += amount;
        if (this.scroll > this.getHeight() - this.boxHeight) {
            this.scroll = this.getHeight() - this.boxHeight;
        }
        if (this.scroll < 0) {
            this.scroll = 0;
        }
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
        return (int) (super.getHeight() * this.realScale);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = this.getX();
        int j = this.getY();
        if (this.enableScrollableIconRender) {
            if (this.scroll > 0) {
                context.drawTexture(IconTexID, i + realWidth - IconSize, j, 0, 0, IconSize, IconSize, IconSize, IconSize * 2);
            }
            if (this.scroll < this.getHeight() - this.boxHeight) {
                context.drawTexture(IconTexID, i + realWidth - IconSize, j + boxHeight - IconSize, 0, IconSize, IconSize, IconSize, IconSize, IconSize * 2);
            }
        }
        MultilineText multilineText = (MultilineText)this.cacheKeyToText.map(this.getCacheKey());
        Objects.requireNonNull(this.getTextRenderer());
        int k = Math.round(9 * this.Scale);
        int l = this.getTextColor();
        // 这API真好用 比我硬算剔除好写不止一点
        context.enableScissor(i, j, i + this.getWidth(), j + this.boxHeight);
        if (this.centered) {
            multilineText.drawCenterWithShadow(context, i + this.getWidth() / 2, j - scroll, k, l);
        } else {
            if(this.shadow){
                multilineText.drawWithShadow(context, i, j - scroll, k, l);
            }
            else{
                multilineText.draw(context, i, j - scroll, k, l);
            }
        }
        context.disableScissor();
    }
}
