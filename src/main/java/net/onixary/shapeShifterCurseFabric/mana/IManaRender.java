package net.onixary.shapeShifterCurseFabric.mana;

import net.minecraft.client.gui.DrawContext;

public interface IManaRender {
    default boolean OverrideInstinctBar() {
        return false;
    }
    void render(DrawContext context, float tickDelta);
}
