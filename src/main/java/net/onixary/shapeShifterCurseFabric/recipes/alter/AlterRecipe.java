package net.onixary.shapeShifterCurseFabric.recipes.alter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.recipes.RecipeUtils;

public abstract class AlterRecipe implements Recipe<RecipeInputInventory> {
    public static final RecipeType<AlterRecipe> ALTER_RECIPE = RecipeUtils.registerRecipeType(ShapeShifterCurseFabric.identifier("alter"));

    @Override
    public RecipeType<?> getType() {
        return ALTER_RECIPE;
    }

    public abstract int recipeTime();

    public boolean needFuel() {
        return true;
    }

    // 进度锁 虽然SSC目前没这个需求 但我的拓展有这个需求
    public boolean canCraft(PlayerEntity player) {
        return true;
    }
}
