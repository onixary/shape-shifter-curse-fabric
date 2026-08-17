package net.onixary.shapeShifterCurseFabric.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;

public class SSC_JEI_Plugin implements IModPlugin {
    public static final RecipeType<WebComposterRecipe> WEB_COMPOSTING = RecipeType.create(ShapeShifterCurseFabric.MOD_ID, "web_compostable", WebComposterRecipe.class);

    @Override
    public @NotNull Identifier getPluginUid() {
        return ShapeShifterCurseFabric.identifier("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new WebComposterCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IIngredientManager ingredientManager = registration.getIngredientManager();
        registration.addRecipes(WEB_COMPOSTING, WebComposterRecipe.getRecipes(ingredientManager));
    }
}
