package net.onixary.shapeShifterCurseFabric.util.util;

import net.minecraft.util.Identifier;

public class SpriteMap {
    public final Identifier TextureID;
    public final int TextureWidth;
    public final int TextureHeight;

    public final int TextureUVOffsetX;
    public final int TextureUVOffsetY;

    public final int SpriteWidth;
    public final int SpriteHeight;

    public SpriteMap(Identifier textureID, int textureWidth, int textureHeight, int spriteWidth, int spriteHeight) {
        this(textureID, textureWidth, textureHeight, 0, 0, spriteWidth, spriteHeight);
    }

    public SpriteMap(Identifier textureID, int textureWidth, int textureHeight, int textureUVOffsetX, int textureUVOffsetY, int spriteWidth, int spriteHeight) {
        this.TextureID = textureID;
        this.TextureWidth = textureWidth;
        this.TextureHeight = textureHeight;
        this.TextureUVOffsetX = textureUVOffsetX;
        this.TextureUVOffsetY = textureUVOffsetY;
        this.SpriteWidth = spriteWidth;
        this.SpriteHeight = spriteHeight;
    }

    // Build_By_Pixel 简写一下
    public ISprite bp(int x_pixel, int y_pixel) {
        return new BaseSprite(TextureID, TextureWidth, TextureHeight, TextureUVOffsetX + x_pixel, TextureUVOffsetY + y_pixel, SpriteWidth, SpriteHeight);
    }

    // Build_By_Index 一样
    public ISprite bi(int x_index, int y_index) {
        return new BaseSprite(TextureID, TextureWidth, TextureHeight, TextureUVOffsetX + x_index * SpriteWidth, TextureUVOffsetY + y_index * SpriteHeight, SpriteWidth, SpriteHeight);
    }
}
