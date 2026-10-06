package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.util.BaseSprite;
import net.onixary.shapeShifterCurseFabric.util.util.ISprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptyCostType implements IFUSDrawableCostType<EmptyCostType> {
    private static final Identifier id = ShapeShifterCurseFabric.identifier("empty");
    public static final Identifier TEXTURE = ShapeShifterCurseFabric.identifier("textures/gui/shape_shifter_tuner_ui.png");
    public static final int TEXTURE_WIDTH = 454;
    public static final int TEXTURE_HEIGHT = 190;
    private static final ISprite xpIconSprite = new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 434, 35, 18, 18);

    @Override
    public Identifier getID() {
        return id;
    }

    @Override
    public void drawIcon(DrawContext context, @NotNull ICost costObject, @Nullable PlayerEntity player, int x, int y, int z) {
        xpIconSprite.draw(context, x, y, z, 0, 0, 18, 18);
    }

    @Override
    public void drawOnHover(DrawContext context, @NotNull ICost costObject, @Nullable PlayerEntity player, int x, int y, int z, int mouseX, int mouseY) {
        // NOP
    }

    @Override
    public Text getAmountText(@NotNull ICost costObject, @Nullable PlayerEntity player) {
        return Text.literal("");
    }

    @Override
    public boolean canPay(@NotNull ICost costObject, @Nullable PlayerEntity player) {
        return true;
    }

    @Override
    public void pay(@NotNull ICost costObject, @NotNull PlayerEntity player) {
        return;
    }
}