package net.onixary.shapeShifterCurseFabric.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import org.jetbrains.annotations.NotNull;

public class WebComposterCategory extends AbstractRecipeCategory<WebComposterRecipe> {
    public WebComposterCategory(IGuiHelper guiHelper) {
        super(SSC_JEI_Plugin.WEB_COMPOSTING, Text.translatable("gui.shape_shifter_curse.category.web_compostable"), guiHelper.createDrawableItemLike(RegCustomBlock.WEB_COMPOSTER), 120, 18);
    }

    public void setRecipe(IRecipeLayoutBuilder builder, WebComposterRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addInputSlot(1, 1).setStandardSlotBackground().addItemStacks(recipe.getInputs());
        builder.addOutputSlot(103, 1).setStandardSlotBackground().addItemStack(new ItemStack(RegCustomItem.SPIDER_FLUID_COCOON, 1));
    }

    public void createRecipeExtras(IRecipeExtrasBuilder builder, WebComposterRecipe recipe, @NotNull IFocusGroup focuses) {
        float chance = recipe.getChance();
        int chancePercent = (int)Math.floor((double)(chance * 100.0F));
        Text text = Text.translatable("gui.jei.category.compostable.chance", new Object[]{chancePercent});
        ((ITextWidget)builder.addText(text, this.getWidth() - 40, this.getHeight()).setPosition(12, 0)).setTextAlignment(HorizontalAlignment.CENTER).setTextAlignment(VerticalAlignment.CENTER).setColor(-8355712);
    }

    public Identifier getRegistryName(WebComposterRecipe recipe) {
        return recipe.getUid();
    }
}
