package net.onixary.shapeShifterCurseFabric.custom_ui.util;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ScaledLabelComponent extends LabelComponent {
    public float textScale = 0.5f;
    public ScaledLabelComponent(Text text, float textScale) {
        super(text);
        this.textScale = textScale;
    }

    /*@Override
    public void inflate(Size space) {
        wrapLinesWithScale();
        super.inflate(space);
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        int widestText = 0;
        for (var line : this.wrappedText) {
            int width = this.textRenderer.getWidth(line);
            if (width > widestText) widestText = width;
        }

        if (widestText > (int)(this.maxWidth / textScale)) {
            wrapLinesWithScale();
            return this.determineHorizontalContentSize(sizing);
        } else {
            return widestText;
        }
    }

    private void wrapLinesWithScale() {
        this.wrappedText = this.textRenderer.wrapLines(this.text, this.horizontalSizing.get().isContent() ? (int)(this.maxWidth / textScale) : this.width);
    }*/

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var matrices = context.getMatrices();

        matrices.push();
        // 计算缩放后的偏移量
        float scaledX = x / textScale;
        float scaledY = y / textScale;

        // 先平移到正确位置，再进行缩放
        matrices.translate(x, y, 0);
        matrices.scale(textScale, textScale, 1.0f);
        matrices.translate(-scaledX, -scaledY, 0);
        matrices.translate(0, 1 / MinecraftClient.getInstance().getWindow().getScaleFactor(), 0);

        int x = (int) scaledX;
        int y = (int) scaledY;

        if (this.horizontalSizing.get().isContent()) {
            x += this.horizontalSizing.get().value;
        }
        if (this.verticalSizing.get().isContent()) {
            y += this.verticalSizing.get().value;
        }

        switch (this.verticalTextAlignment) {
            case CENTER -> y += (this.height - ((this.wrappedText.size() * (this.lineHeight() + 2)) - 2)) / 2;
            case BOTTOM -> y += this.height - ((this.wrappedText.size() * (this.lineHeight() + 2)) - 2);
        }

        final int lambdaX = x;
        final int lambdaY = y;

        context.draw(() -> {
            for (int i = 0; i < this.wrappedText.size(); i++) {
                var renderText = this.wrappedText.get(i);
                int renderX = lambdaX;

                switch (this.horizontalTextAlignment) {
                    case CENTER -> renderX += (this.width - this.textRenderer.getWidth(renderText)) / 2;
                    case RIGHT -> renderX += this.width - this.textRenderer.getWidth(renderText);
                }

                int renderY = lambdaY + i * (this.lineHeight() + 2);
                renderY += this.lineHeight() - this.textRenderer.fontHeight;

                context.drawText(this.textRenderer, renderText, renderX, renderY, this.color.get().argb(), this.shadow);
            }
        });

        matrices.pop();
    }
}
