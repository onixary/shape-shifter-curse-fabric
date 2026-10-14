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
    public void draw(DrawContext context, int x1, int x2, int y1, int y2, int z, int regionWidth, int regionHeight, float u, float v) {
        super.draw(context, x1 + drawOffsetX, x2 + drawOffsetX, y1 + drawOffsetY, y2 + drawOffsetY, z, regionWidth, regionHeight, u, v);
    }
}
