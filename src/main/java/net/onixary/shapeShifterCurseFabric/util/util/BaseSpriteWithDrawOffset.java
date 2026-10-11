package net.onixary.shapeShifterCurseFabric.util.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class BaseSpriteWithDrawOffset extends BaseSprite {
    public final int drawOffsetX;
    public final int drawOffsetY;

    public BaseSpriteWithDrawOffset(Identifier textureID, int textureImgWidth, int textureImgHeight, int drawOffsetX, int drawOffsetY, int textureX, int textureY, int textureWidth, int textureHeight) {
        super(textureID, textureImgWidth, textureImgHeight, textureX, textureY, textureWidth, textureHeight);
        this.drawOffsetX = drawOffsetX;
        this.drawOffsetY = drawOffsetY;
    }

    @Override
    public void draw(DrawContext context, int x, int y, int z, int u, int v, int width, int height) {
        context.drawTexture(getTextureID(), x + drawOffsetX, y + drawOffsetY, z, getTextureX() + u, getTextureY() + v, width, height, getTextureImgWidth(), getTextureImgHeight());
    }
}
