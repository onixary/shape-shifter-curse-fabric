package net.onixary.shapeShifterCurseFabric.util.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

// 拓展的护盾画法 为了极致的速度 尽量少添加贴图
public interface ISprite {
    public Identifier getTextureID();

    public int getTextureImgWidth();

    public int getTextureImgHeight();

    public int getTextureX();

    public int getTextureY();

    public int getTextureWidth();

    public int getTextureHeight();

    public default void draw(DrawContext context, int x, int y) {
        this.draw(context, x, y, 0, 0, 0, getTextureWidth(), getTextureHeight());
    }

    public default void draw(DrawContext context, int x, int y, int z, int u, int v, int width, int height) {
        context.drawTexture(getTextureID(), x, y, z, getTextureX() + u, getTextureY() + v, width, height, getTextureImgWidth(), getTextureImgHeight());
    }
}
