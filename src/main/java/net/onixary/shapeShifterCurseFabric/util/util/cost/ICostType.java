package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICostType<T extends ICostType<T>> {
    public Identifier getID();

    public boolean canPay(@NotNull Cost costObject, @Nullable PlayerEntity player);

    public default boolean canPay_CLIENT(@NotNull Cost costObject, @Nullable PlayerEntity player) {
        return canPay(costObject, player);
    }

    public void pay(@NotNull Cost costObject, @NotNull PlayerEntity player);
}
