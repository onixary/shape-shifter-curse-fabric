package net.onixary.shapeShifterCurseFabric.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// 酿造配方实际上不属于原版配方系统 但是都叫Recipe了 还是放到RecipePackage里吧
public class BrewingRecipeUtils {
    public static class DynamicRecipe<T> extends BrewingRecipeRegistry.Recipe<T> {  // 可以用 InstanceOf 来区分是否为动态配方
        public @Nullable Identifier targetForm;

        public DynamicRecipe(T input, Ingredient ingredient, T output, Identifier targetForm) {
            super(input, ingredient, output);
            this.targetForm = targetForm;
        }
    }

    private static final List<DynamicRecipe<Potion>> POTION_RECIPES = Lists.newArrayList();
    private static final List<DynamicRecipe<Item>> ITEM_RECIPES = Lists.newArrayList();

    public static void onLoadDynamicBrewingRecipesStart() {
        POTION_RECIPES.clear();
        ITEM_RECIPES.clear();
        BrewingRecipeRegistry.POTION_RECIPES.removeIf(recipe -> recipe instanceof DynamicRecipe);
        BrewingRecipeRegistry.ITEM_RECIPES.removeIf(recipe -> recipe instanceof DynamicRecipe);
    }

    public static void onLoadDynamicBrewingRecipesEnd() {
        BrewingRecipeRegistry.POTION_RECIPES.addAll(POTION_RECIPES);
        BrewingRecipeRegistry.ITEM_RECIPES.addAll(ITEM_RECIPES);
    }

    /*
    ```json
    {
      "type": "potion",
      "input": "shape-shifter-curse:moondust_potion",
      "ingredient": "minecraft:bone",
      "output": "shape-shifter-curse:to_anubis_wolf_0_potion",
      "target_form": "example_namespace:example"
    }
    ```
    ```json
    {
      "type": "item",
      "input": "minecrafte:potion",
      "ingredient": "minecraft:gunpowder",
      "output": "minecraft:splash_potion"
    }
    ```
     */

    public static void registerPotionRecipe(JsonObject recipeJson) {
        if (recipeJson == null) {
            ShapeShifterCurseFabric.LOGGER.error("recipe json is null");
            return;
        }
        if (!recipeJson.has("type")) {
            ShapeShifterCurseFabric.LOGGER.error("recipe json has no type");
            return;
        }
        if (!recipeJson.has("input") || !recipeJson.has("ingredient") || !recipeJson.has("output")) {
            ShapeShifterCurseFabric.LOGGER.error("recipe json has no input or ingredient or output");
            return;
        }
        Identifier input = Identifier.tryParse(recipeJson.get("input").getAsString());
        Identifier ingredient = Identifier.tryParse(recipeJson.get("ingredient").getAsString());
        Identifier output = Identifier.tryParse(recipeJson.get("output").getAsString());
        Identifier targetForm = null;
        if (recipeJson.has("target_form")) {
            targetForm = Identifier.tryParse(recipeJson.get("target_form").getAsString());
        }
        if (input == null || ingredient == null || output == null) {
            ShapeShifterCurseFabric.LOGGER.error("recipe json has invalid input or ingredient or output");
            return;
        }
        Item ingredientItem = Registries.ITEM.get(ingredient);
        Ingredient ingredientObject = Ingredient.ofItems(new ItemConvertible[]{ingredientItem});
        switch (recipeJson.get("type").getAsString()) {
            case "potion" -> {
                Potion inputPotion = Registries.POTION.get(input);
                Potion outputPotion = Registries.POTION.get(output);
                POTION_RECIPES.add(new DynamicRecipe<>(inputPotion, ingredientObject, outputPotion, targetForm));
            }
            case "item" -> {
                Item inputItem = Registries.ITEM.get(input);
                Item outputItem = Registries.ITEM.get(output);
                ITEM_RECIPES.add(new DynamicRecipe<>(inputItem, ingredientObject, outputItem, targetForm));
            }
        }
        return;
    }
}
