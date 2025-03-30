package net.onixary.shapeShifterCurseFabric.item;

import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class RegCustomPotions {
    public static final Potion MOONDUST_POTION =
            Registry.register(Registries.POTION, new Identifier(MOD_ID, "moondust_potion"),
                    new Potion());

    public static void registerPotions(){

    }

    public static void registerPotionsRecipes(){
        // awkward + moondust_matrix = moondust_potion
        BrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD, RegCustomItem.MOONDUST_MATRIX, RegCustomPotions.MOONDUST_POTION);
    }
}
