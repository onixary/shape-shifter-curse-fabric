package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.onixary.shapeShifterCurseFabric.recipes.BrewingRecipeUtils;
import net.onixary.shapeShifterCurseFabric.status_effects.CTPUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {
    @Inject(method = "craft", at = @At("RETURN"))
    private static void craft(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        if (input.isEmpty()) {
            return;
        }
        ItemStack resultPotion = cir.getReturnValue();
        Potion potion = PotionUtil.getPotion(input);
        Item item = input.getItem();
        for(int i = 0, j = BrewingRecipeRegistry.ITEM_RECIPES.size(); i < j; ++i) {
            BrewingRecipeRegistry.Recipe<Item> recipe = BrewingRecipeRegistry.ITEM_RECIPES.get(i);
            if (recipe.input == item && recipe.ingredient.test(ingredient)) {
                if (recipe instanceof BrewingRecipeUtils.DynamicRecipe<Item> dRecipe && dRecipe.targetForm != null) {
                    CTPUtils.setCTPFormIDToNBT(resultPotion.getNbt(), dRecipe.targetForm);
                }
                return;
            }
        }
        for(int i = 0, j = BrewingRecipeRegistry.POTION_RECIPES.size(); i < j; ++i) {
            BrewingRecipeRegistry.Recipe<Potion> recipe = BrewingRecipeRegistry.POTION_RECIPES.get(i);
            if (recipe.input == potion && recipe.ingredient.test(ingredient)) {
                if (recipe instanceof BrewingRecipeUtils.DynamicRecipe<Potion> dRecipe && dRecipe.targetForm != null) {
                    CTPUtils.setCTPFormIDToNBT(resultPotion.getNbt(), dRecipe.targetForm);
                }
                return;
            }
        }
    }
}
