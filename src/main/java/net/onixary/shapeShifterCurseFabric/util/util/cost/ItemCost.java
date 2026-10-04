package net.onixary.shapeShifterCurseFabric.util.util.cost;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ItemCost implements ICost {
    public static final Identifier id = ShapeShifterCurseFabric.identifier("item");
    private ICostType<?> type;
    private ItemStack exampleStack;
    private int amount;

    public ItemCost() {
        this(RegCostType.NO_COST, ItemStack.EMPTY, 0);
    }

    public ItemCost(NbtCompound nbt) {
        this();
        readFromNBT(nbt);
    }

    public ItemCost(ICostType<?> type, ItemStack exampleStack, int amount) {
        this.type = type;
        exampleStack.setCount(1);
        this.exampleStack = exampleStack;
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

    public ItemStack getExampleStack() {
        return exampleStack;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void writeToNBT(NbtCompound nbt) {
        nbt.putString("type", type.getID().toString());
        nbt.put("exampleStack", exampleStack.writeNbt(new NbtCompound()));
        nbt.putInt("amount", amount);
    }

    @Override
    public void readFromNBT(NbtCompound nbt) {
        type = RegCostType.getCostType(new Identifier(nbt.getString("type")));
        exampleStack = ItemStack.fromNbt(nbt.getCompound("exampleStack"));
        amount = nbt.getInt("amount");
    }
}
