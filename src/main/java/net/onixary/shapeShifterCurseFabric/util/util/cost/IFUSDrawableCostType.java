package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IFUSDrawableCostType<T extends ICostType<T>> extends ICostType<T> {
    public void drawIcon(DrawContext context, @NotNull ICost<T> costObject, @Nullable PlayerEntity player, int x, int y, int z);

    public void drawOnHover(DrawContext context, @NotNull ICost<T> costObject, @Nullable PlayerEntity player, int x, int y, int z, int mouseX, int mouseY);

    public default Text getAmountText(@NotNull ICost<T> costObject, @Nullable PlayerEntity player) {
        return Text.of(String.valueOf(costObject.getAmount()));
    }
}
