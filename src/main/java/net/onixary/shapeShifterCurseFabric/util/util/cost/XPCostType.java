package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XPCostType implements IFUSDrawableCostType<XPCostType> {
    @Override
    public Identifier getID() {
        return null;
    }

    @Override
    public void drawIcon(DrawContext context, @NotNull ICost<XPCostType> costObject, @Nullable PlayerEntity player, int x, int y, int z) {

    }

    @Override
    public void drawOnHover(DrawContext context, @NotNull ICost<XPCostType> costObject, @Nullable PlayerEntity player, int x, int y, int z, int mouseX, int mouseY) {

    }

    @Override
    public boolean canPay(@NotNull ICost<? extends XPCostType> costObject, @Nullable PlayerEntity player) {
        return false;
    }

    @Override
    public void pay(@NotNull ICost<? extends XPCostType> costObject, @Nullable PlayerEntity player) {

    }
}
