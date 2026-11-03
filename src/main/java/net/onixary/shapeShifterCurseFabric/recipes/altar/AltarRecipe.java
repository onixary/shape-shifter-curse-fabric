package net.onixary.shapeShifterCurseFabric.recipes.altar;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.onixary.shapeShifterCurseFabric.recipes.RecipeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ArrayList;

// 这个AI写的什么玩意啊 和那个EyeScale一样 给我看的血压飙升 之后得重构(不用之后了 现在就得重构)
public abstract class AltarRecipe implements Recipe<SidedInventory> {
    @Override
    public RecipeType<?> getType() {
        return RecipeUtils.ALTAR_RECIPE;
    }

    public abstract int recipeTime();

    // 进度锁 虽然SSC目前没这个需求 但我的拓展有这个需求
    public boolean canCraft(@Nullable PlayerEntity player) {
        return true;
    }

    // 可以做到一个配方 消耗N个物品
    public boolean InputsCountEnough(SidedInventory inventory) {
        return true;
    }

    public void consumeInputs(SidedInventory inventory) {
        for (int i = 0; i < 9; i++) {
            ItemStack input = inventory.getStack(i);
            ItemStack remainder = inputRemainder(input);
            input.decrement(1);
            if (input.isEmpty() && !remainder.isEmpty()) {
                inventory.setStack(i, remainder);
            }
        }
    }

    public List<ItemStack> getExtraOutput(SidedInventory inventory) {
        List<ItemStack> remainders = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack input = inventory.getStack(i);
            ItemStack remainder = inputRemainder(input);
            if (input.getCount() > 1 && !remainder.isEmpty()) {
                remainders.add(remainder);
            }
        }
        return remainders;
    }

    public static ItemStack inputRemainder(ItemStack input) {
        // Unlike fluid buckets, vanilla's powder snow bucket declares no recipe remainder.
        if (input.isOf(Items.POWDER_SNOW_BUCKET)) {
            return new ItemStack(Items.BUCKET);
        }
        return input.getItem().hasRecipeRemainder()
                ? new ItemStack(input.getItem().getRecipeRemainder()) : ItemStack.EMPTY;
    }

    public int fuelUsage() {
        return 1;
    }

    // 辅助函数 不可重载 除非修改了燃料消耗逻辑
    public int totalFuelUsage() {
        return fuelUsage() * recipeTime();
    }
}
