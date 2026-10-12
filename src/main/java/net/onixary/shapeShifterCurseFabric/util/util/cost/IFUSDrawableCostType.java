package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IFUSDrawableCostType<T extends ICostType<T>> extends ICostType<T> {
    // 1s查询一次 如果getAmountText还是对性能影响较大 可以改成-1来禁用自动查询 仅在需要更新数据时查询(比如切换选中Perk)
    public default int checkInterval() {
        return 20;
    }

    public void drawIcon(DrawContext context, @NotNull ICost costObject, @Nullable PlayerEntity player, int x, int y, int z);

    public void drawOnHover(DrawContext context, @NotNull ICost costObject, @Nullable PlayerEntity player, int x, int y, int z, int mouseX, int mouseY);

    public default Text getAmountText(@NotNull ICost costObject, @Nullable PlayerEntity player) {
        return Text.of(String.valueOf(costObject.getAmount()));
    }

    public default Text getResourceNotEnoughText(@NotNull ICost costObject, @Nullable PlayerEntity player) {
        return Text.translatable("gui.shape-shifter-curse.perk.unavailable");
    }
}
