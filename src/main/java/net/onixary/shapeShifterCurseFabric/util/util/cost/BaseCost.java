package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class BaseCost implements ICost {
    public static final Identifier id = ShapeShifterCurseFabric.identifier("base");
    private ICostType<?> type;
    private int amount;

    public BaseCost() {
        this(RegCostType.NO_COST, 0);
    }

    public BaseCost(NbtCompound nbt) {
        this();
        readFromNBT(nbt);
    }

    public BaseCost(ICostType<?> type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public ICostType<?> getType() {
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
        try {
            type = RegCostType.getCostType(Identifier.tryParse(nbt.getString("type")));
            amount = nbt.getInt("amount");
        } catch (Exception e) {
            type = RegCostType.NO_COST;
            amount = 0;
        }
    }
}
