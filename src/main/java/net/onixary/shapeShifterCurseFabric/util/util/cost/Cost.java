package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class Cost {
    private ICostType<?> type;
    private int amount;

    public Cost() {
        this(RegCostType.NO_COST, 0);
    }

    public Cost(ICostType<?> type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public ICostType<?> getType() {
        return type;
    }


    public int getAmount() {
        return amount;
    }


    public void writeToNBT(NbtCompound nbt) {
        nbt.putString("type", type.getID().toString());
        nbt.putInt("amount", amount);
    }


    public void readFromNBT(NbtCompound nbt) {
        try {
            type = RegCostType.getCostType(Identifier.tryParse(nbt.getString("type")));
            amount = nbt.getInt("amount");
        } catch (Exception e) {
            type = RegCostType.NO_COST;
            amount = 0;
        }
    }

    public static @NotNull Cost fromNBT(NbtCompound nbt) {
        Cost cost = new Cost();
        cost.readFromNBT(nbt);
        return cost;
    }
}
