package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class BaseCost<T extends ICostType<?>> implements ICost<T> {
    private T type;
    private int amount;

    public BaseCost(T type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public T getType() {
        return type;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void writeToNBT(NbtCompound nbt) {
        nbt.putString("type", type.getID().toString());
        nbt.putInt("amount", amount);
    }

    @Override
    public void readFromNBT(NbtCompound nbt) {
        // TODO 注册表
        // type = RegCostType.getCostType(nbt.getString("type"));
        amount = nbt.getInt("amount");
    }

    public static @NotNull BaseCost<?> fromNBT(NbtCompound nbt) {
        // TODO 注册表
        // return new BaseCost<>(RegCostType.getCostType(nbt.getString("type")), nbt.getInt("amount"));
        return null;
    }
}
