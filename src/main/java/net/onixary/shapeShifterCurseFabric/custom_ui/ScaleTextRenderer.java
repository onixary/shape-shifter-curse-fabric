package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;


@Environment(EnvType.CLIENT)
public class ScaleTextRenderer extends TextRenderer {
    public float Scale = 1.0f;
    private static final Vector3f FORWARD_SHIFT = new Vector3f(0.0F, 0.0F, 0.03F);

    public ScaleTextRenderer(@NotNull TextRenderer textRenderer) {
        super(textRenderer.fontStorageAccessor, textRenderer.validateAdvance);
    }

    public int draw(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        return this.draw(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light, this.isRightToLeft());
    }

    public int draw(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light, boolean rightToLeft) {
        return this.drawInternal(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light, rightToLeft);
    }

    public int draw(Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        return this.draw(text.asOrderedText(), x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light);
    }

    public int draw(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        return this.drawInternal(text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light);
    }

    private int drawInternal(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light, boolean mirror) {
        float RScale = 1.0f / this.Scale;
        if (mirror) {
            text = this.mirror(text);
        }
        color = tweakTransparency(color);
        Matrix4f matrix4f = new Matrix4f(matrix);
        matrix.scale(this.Scale);
        matrix4f.scale(this.Scale);
        if (shadow) {
            this.drawLayer(text, x * RScale, y * RScale, color, true, matrix, vertexConsumers, layerType, backgroundColor, light);
            matrix4f.translate(FORWARD_SHIFT);
        }

        x = this.drawLayer(text, x * RScale, y * RScale, color, false, matrix4f, vertexConsumers, layerType, backgroundColor, light);
        matrix.scale(RScale);
        matrix4f.scale(RScale);
        return (int)x + (shadow ? 1 : 0);
    }

    private int drawInternal(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextLayerType layerType, int backgroundColor, int light) {
        float RScale = 1.0f / this.Scale;
        color = tweakTransparency(color);
        Matrix4f matrix4f = new Matrix4f(matrix);
        matrix.scale(this.Scale);
        matrix4f.scale(this.Scale);
        if (shadow) {
            this.drawLayer(text, x * RScale, y * RScale, color, true, matrix, vertexConsumerProvider, layerType, backgroundColor, light);
            matrix4f.translate(FORWARD_SHIFT);
        }

        x = this.drawLayer(text, x * RScale, y * RScale, color, false, matrix4f, vertexConsumerProvider, layerType, backgroundColor, light);
        matrix.scale(RScale);
        matrix4f.scale(RScale);
        return (int)x + (shadow ? 1 : 0);
    }

    public int getWrappedLinesHeight(String text, int maxWidth) {
        return Math.round(9 * this.Scale) * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
    }

    public int getWrappedLinesHeight(StringVisitable text, int maxWidth) {
        return Math.round(9 * this.Scale) * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
    }

    public int getWidth(String text) {
        return MathHelper.ceil(this.handler.getWidth(text) * this.Scale);
    }

    public int getWidth(StringVisitable text) {
        return MathHelper.ceil(this.handler.getWidth(text) * this.Scale);
    }

    public int getWidth(OrderedText text) {
        return MathHelper.ceil(this.handler.getWidth(text) * this.Scale);
    }

    public String trimToWidth(String text, int maxWidth, boolean backwards) {
        return backwards ? this.handler.trimToWidthBackwards(text, (int) (maxWidth * (1.0f / this.Scale)), Style.EMPTY) : this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
    }

    public String trimToWidth(String text, int maxWidth) {
        return this.handler.trimToWidth(text, (int) (maxWidth * (1.0f / this.Scale)), Style.EMPTY);
    }

    public StringVisitable trimToWidth(StringVisitable text, int width) {
        return this.handler.trimToWidth(text, width, Style.EMPTY);
    }
}
