package net.onixary.shapeShifterCurseFabric.integration.jei;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.vanilla.IJeiCompostingRecipe;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.plugins.vanilla.compostable.CompostingRecipe;
import mezz.jei.library.util.ResourceLocationUtil;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.blocks.WebComposterBlock;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WebComposterRecipe {
    private final List<ItemStack> inputs;
    private final float chance;
    private final Identifier uid;

    public WebComposterRecipe(ItemStack input, float chance, Identifier uid) {
        Preconditions.checkArgument(chance > 0.0F, "web_composting chance must be greater than 0");
        this.inputs = List.of(input);
        this.chance = chance;
        this.uid = uid;
    }


    public List<ItemStack> getInputs() {
        return this.inputs;
    }

    public float getChance() {
        return this.chance;
    }

    public Identifier getUid() {
        return this.uid;
    }

    public static List<WebComposterRecipe> getRecipes(IIngredientManager ingredientManager) {
        Collection<ItemStack> allIngredients = ingredientManager.getAllItemStacks();
        IIngredientHelper<ItemStack> ingredientHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
        return allIngredients.stream()
                .filter(WebComposterBlock::canIncrease)
                .map(itemStack -> {
                    float chance = WebComposterBlock.getIncreaseChance(itemStack);
                    String ingredientUid = ingredientHelper.getUniqueId(itemStack, UidContext.Recipe);
                    String ingredientUidPath = ResourceLocationUtil.sanitizePath(ingredientUid);
                    Identifier recipeUid = ShapeShifterCurseFabric.identifier("jei/web_composting/" + ingredientUidPath);
                    return new WebComposterRecipe(itemStack, chance, recipeUid);
                })
                .sorted(Comparator.comparingDouble(WebComposterRecipe::getChance))
                .collect(Collectors.toList());
    }
}
