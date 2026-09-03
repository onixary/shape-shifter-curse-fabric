package net.onixary.shapeShifterCurseFabric.blocks.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AlterBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider {
    // 进度锁是个不错的设计 能降低难度(毕竟之前做限制进度使用得上对应阶段的材料 有些材料是真不好量产 有这个就能用便宜材料了)
    public UUID lastUser;

    public int[] TOP = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public int[] SIDE = {9};
    public int[] BOTTOM = {10};
    public final DefaultedList<ItemStack> inventory;

    public AlterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RegCustomBlock.ALTER_BLOCK_ENTITY, blockPos, blockState);
        this.inventory = DefaultedList.ofSize(11, ItemStack.EMPTY);
    }

    @Override
    protected Text getContainerName() {
        return null;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return switch (side) {
            case UP -> TOP;
            case DOWN -> BOTTOM;
            case EAST, WEST, NORTH, SOUTH -> SIDE;
        };
    }

    // TODO 先得确定燃料是什么
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemStack : this.inventory) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = (ItemStack)this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && ItemStack.canCombine(itemStack, stack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        // TODO 更新配方
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        for(ItemStack itemStack : this.inventory) {
            finder.addInput(itemStack);
        }
    }

    @Override
    public void setLastRecipe(@Nullable Recipe<?> recipe) {

    }

    @Override
    public @Nullable Recipe<?> getLastRecipe() {
        return null;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}
