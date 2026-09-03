package net.onixary.shapeShifterCurseFabric.recipes.alter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class AlterShapedRecipe extends AlterRecipe {
    public final int recipeTime;

    public final int width;
    public final int height;

    public final DefaultedList<Ingredient> input;
    public final ItemStack output;
    public final Identifier id;

    public AlterShapedRecipe(Identifier id, int width, int height, DefaultedList<Ingredient> input, ItemStack output, int recipeTime) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.input = input;
        this.output = output;
        this.recipeTime = recipeTime;
    }

    @Override
    public int recipeTime() {
        return recipeTime;
    }

    @Override
    public boolean canCraft(PlayerEntity player) {
        return true;
    }

    private boolean matchesPattern(RecipeInputInventory inv, int offsetX, int offsetY, boolean flipped) {
        for(int i = 0; i < inv.getWidth(); ++i) {
            for(int j = 0; j < inv.getHeight(); ++j) {
                int k = i - offsetX;
                int l = j - offsetY;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (flipped) {
                        ingredient = (Ingredient)this.input.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = (Ingredient)this.input.get(k + l * this.width);
                    }
                }

                if (!ingredient.test(inv.getStack(i + j * inv.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        for(int i = 0; i <= recipeInputInventory.getWidth() - this.width; ++i) {
            for(int j = 0; j <= recipeInputInventory.getHeight() - this.height; ++j) {
                if (this.matchesPattern(recipeInputInventory, i, j, true)) {
                    return true;
                }

                if (this.matchesPattern(recipeInputInventory, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        return this.getOutput(registryManager).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.output;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
