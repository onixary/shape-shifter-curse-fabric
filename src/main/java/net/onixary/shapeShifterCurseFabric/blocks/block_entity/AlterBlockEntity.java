package net.onixary.shapeShifterCurseFabric.blocks.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.recipes.alter.AlterRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class AlterBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider {
    // 进度锁是个不错的设计 能降低难度(毕竟之前做限制进度使用得上对应阶段的材料 有些材料是真不好量产 有这个就能用便宜材料了)
    public UUID lastUser;
    public AlterRecipe nowRecipe;
    public int progress = 0;

    public final int[] TOP = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public final int[] SIDE = {9};
    public final int[] BOTTOM = {10};
    public final DefaultedList<ItemStack> inventory;

    public static final HashMap<Item, Integer> fuelTime = new HashMap<>();

    private final RecipeManager.MatchGetter<SidedInventory, ? extends AlterRecipe> matchGetter;

    static {
        fuelTime.put(RegCustomItem.UNTREATED_MOONDUST, 800);
    }

    public static boolean canFuel(ItemStack stack) {
        return fuelTime.containsKey(stack.getItem());
    }

    public static int getFuelTime(ItemStack stack) {
        return fuelTime.getOrDefault(stack.getItem(), 0);
    }

    public AlterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RegCustomBlock.ALTER_BLOCK_ENTITY, blockPos, blockState);
        this.inventory = DefaultedList.ofSize(11, ItemStack.EMPTY);
        this.matchGetter = RecipeManager.createCachedMatchGetter(AlterRecipe.ALTER_RECIPE);
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

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return switch (slot) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8 -> true;
            case 9 -> canFuel(stack);
            case 10 -> false;
            default -> false;
        };
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
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
        this.checkRecipe();
        this.markDirty();
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

    public void checkRecipe() {
        PlayerEntity playerEntity = null;
        World world = this.getWorld();
        if (world != null && this.lastUser != null) {
            playerEntity = world.getPlayerByUuid(this.lastUser);
        }
        if (this.nowRecipe != null) {
            if (this.nowRecipe.canCraft(playerEntity) && this.nowRecipe.matches(this, world)) {
                return;
            }
        }
        Optional<? extends AlterRecipe> alterRecipe = this.matchGetter.getFirstMatch(this, world);
        if (alterRecipe.isPresent() && alterRecipe.get().canCraft(playerEntity)) {
            this.nowRecipe = alterRecipe.get();
        } else {
            this.nowRecipe = null;
        }
        this.progress = 0;
    }
}
