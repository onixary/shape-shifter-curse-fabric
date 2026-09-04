package net.onixary.shapeShifterCurseFabric.recipes;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.recipes.alter.AlterRecipe;

public class RecipeUtils {
    public static final RecipeType<AlterRecipe> ALTER_RECIPE = registerRecipeType(ShapeShifterCurseFabric.identifier("alter"));

    public static void register() {
        // 用于加载静态注册
    };

    public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(Identifier id) {
        return Registry.register(Registries.RECIPE_TYPE, id, new RecipeType<T>() {
            public String toString() {
                return id.toString();
            }
        });
    }
}
