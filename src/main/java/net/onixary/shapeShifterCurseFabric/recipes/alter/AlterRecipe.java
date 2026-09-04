package net.onixary.shapeShifterCurseFabric.recipes.alter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.onixary.shapeShifterCurseFabric.recipes.RecipeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AlterRecipe implements Recipe<SidedInventory> {

    @Override
    public RecipeType<?> getType() {
        return RecipeUtils.ALTER_RECIPE;
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
            inventory.getStack(i).decrement(1);
        }
    }

    public List<ItemStack> getExtraOutput(SidedInventory inventory) {
        return List.of();
    }
}
