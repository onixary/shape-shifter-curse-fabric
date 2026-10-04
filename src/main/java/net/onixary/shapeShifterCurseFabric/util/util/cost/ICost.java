package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ICost {
    public Identifier getId();

    public ICostType<?> getType();

    public int getAmount();


    public void writeToNBT(NbtCompound nbt);

    public void readFromNBT(NbtCompound nbt);

    public default void __writeToNBT(NbtCompound nbt) {
        nbt.putString("COST_ID", getId().toString());
        this.writeToNBT(nbt);
    }

    public static @NotNull ICost fromNBT(NbtCompound nbt) {
        if (!nbt.contains("COST_ID")) {
            throw new IllegalArgumentException("NBT does not contain a cost id");
        }
        Identifier id = new Identifier(nbt.getString("COST_ID"));
        Function<NbtCompound, ICost> factory = RegCostType.costs.get(id);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown cost type: " + id);
        }
        return factory.apply(nbt);
    }
}
