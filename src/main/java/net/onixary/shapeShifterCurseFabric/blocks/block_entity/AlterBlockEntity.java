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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
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
    public int fuelTime = 0;
    public final DefaultedList<ItemStack> inventory;

    public static final int[] TOP = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final int[] SIDE = {9};
    public static final int[] BOTTOM = {10};

    public static final HashMap<Item, Integer> fuelTimeMap = new HashMap<>();

    private final RecipeManager.MatchGetter<SidedInventory, ? extends AlterRecipe> matchGetter;

    static {
        fuelTimeMap.put(RegCustomItem.UNTREATED_MOONDUST, 800);
    }

    public static boolean canFuel(ItemStack stack) {
        return fuelTimeMap.containsKey(stack.getItem());
    }

    public static int getFuelTime(ItemStack stack) {
        return fuelTimeMap.getOrDefault(stack.getItem(), 0);
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
        this.checkRecipe();
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        this.checkRecipe();
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

    private boolean canCraftRecipe(DynamicRegistryManager registryManager) {
        if (this.nowRecipe == null) {
            return false;
        }
        PlayerEntity playerEntity = null;
        World world = this.getWorld();
        if (world != null && this.lastUser != null) {
            playerEntity = world.getPlayerByUuid(this.lastUser);
        }
        if (!nowRecipe.canCraft(playerEntity)) {
            return false;
        }
        if (!nowRecipe.matches(this, world) && !nowRecipe.InputsCountEnough(this)) {
            return false;
        }
        ItemStack output = this.nowRecipe.getOutput(registryManager);
        if (output.isEmpty() || this.inventory.get(10).isEmpty()) {
            return true;
        }
        ItemStack outputSlot = this.inventory.get(10);
        if (!ItemStack.canCombine(output, outputSlot)) {
            return false;
        }
        if (outputSlot.getCount() + output.getCount() <= outputSlot.getMaxCount()) {
            return true;
        }
        return outputSlot.getCount() + output.getCount() <= this.getMaxCountPerStack();
    }

    private boolean craftRecipe(DynamicRegistryManager registryManager) {
        if (canCraftRecipe(registryManager)) {
            ItemStack output = this.nowRecipe.getOutput(registryManager);
            ItemStack outputSlot = this.inventory.get(10);
            if (outputSlot.isEmpty()) {
                this.inventory.set(10, output.copy());
            } else if (ItemStack.canCombine(output, outputSlot)) {
                outputSlot.increment(output.getCount());
            } else {
                return false;
            }
            this.nowRecipe.consumeInputs(this);
            return true;
        } else {
            return false;
        }
    }

    public void tick(World world, BlockPos pos, BlockState state, AlterBlockEntity blockEntity) {
        boolean itemChanged = false;
        boolean hasRecipe = this.nowRecipe != null;
        boolean hasFuel = this.fuelTime > 0;
        if (hasRecipe && !hasFuel) {
            ItemStack fuel = this.inventory.get(9);
            if (!fuel.isEmpty()) {
                int fuelRealTime = getFuelTime(fuel);
                if (fuelRealTime > 0) {
                    this.fuelTime = fuelRealTime;
                    fuel.decrement(1);
                    itemChanged = true;
                }
            }
        }
        hasFuel = this.fuelTime > 0;
        if (hasRecipe && hasFuel) {
            this.progress++;
            this.fuelTime--;
        } else {
            this.progress = 0;
            if (hasFuel) {
                this.fuelTime--;
            }
        }
        if (hasRecipe && this.progress >= this.nowRecipe.recipeTime()) {
            if (craftRecipe(world.getRegistryManager())) {
                blockEntity.setLastRecipe(this.nowRecipe);
            }
            this.progress = 0;
            itemChanged = true;
        }
        if (itemChanged) {
            this.markDirty();
        }
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        if (nbt.contains("LastUser")) {
            this.lastUser = nbt.getUuid("LastUser");
        } else {
            this.lastUser = null;
        }
        this.fuelTime = nbt.getInt("FuelTime");
        this.progress = nbt.getInt("Process");
        this.checkRecipe();
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        if (this.lastUser != null) {
            nbt.putUuid("LastUser", this.lastUser);
        }
        nbt.putInt("FuelTime", this.fuelTime);
        nbt.putInt("Process", this.progress);
    }
}
