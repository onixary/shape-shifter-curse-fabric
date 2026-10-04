package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.nbt.NbtCompound;

public interface ICost<T extends ICostType> {
    public T getType();

    public int getAmount();

    public void writeToNBT(NbtCompound nbt);

    public void readFromNBT(NbtCompound nbt);
}
