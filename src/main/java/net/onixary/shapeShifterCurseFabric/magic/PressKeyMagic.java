package net.onixary.shapeShifterCurseFabric.magic;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.util.util.ISprite;

public class PressKeyMagic implements IMagic {
    public final Identifier id;
    public final ISprite sprite;
    public final String KeyBindID;

    public PressKeyMagic(Identifier id, ISprite sprite, String KeyBindID) {
        this.id = id;
        this.sprite = sprite;
        this.KeyBindID = KeyBindID;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public ISprite getSprite() {
        return sprite;
    }

    public String getKeyBindID() {
        return this.KeyBindID;
    }
}
