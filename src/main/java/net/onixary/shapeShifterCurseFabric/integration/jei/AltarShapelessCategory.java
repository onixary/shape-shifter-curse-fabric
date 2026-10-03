package net.onixary.shapeShifterCurseFabric.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.recipes.altar.AltarShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public class AltarShapelessCategory extends AbstractRecipeCategory<AltarShapelessRecipe> {
    private static final Identifier TEXTURE = ShapeShifterCurseFabric.identifier("textures/gui/altar_craft_ui.png");

    private final IDrawable background;
    private final IDrawable arrow;

    public AltarShapelessCategory(IGuiHelper guiHelper) {
        super(SSC_JEI_Plugin.ALTAR_SHAPELESS,
                Text.translatable("gui.shape_shifter_curse.category.altar_shapeless"),
                guiHelper.createDrawableItemLike(RegCustomBlock.ALTAR_BLOCK),
                174, 79);
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 174, 79);
        this.arrow = guiHelper.createDrawable(TEXTURE, 174, 0, 43, 9);
    }

    @Override
    public boolean needsRecipeBorder() {
        return false;
    }

    @Override
    public void draw(@NotNull AltarShapelessRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull DrawContext drawContext, double mouseX, double mouseY) {
        background.draw(drawContext, 0, 0);
        arrow.draw(drawContext, 84, 39);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull AltarShapelessRecipe recipe, @NotNull IFocusGroup focuses) {
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;
            int x = 26 + col * 18;
            int y = 17 + row * 18;
            if (i < recipe.input.size()) {
                Ingredient ing = recipe.input.get(i);
                if (!ing.isEmpty()) {
                    builder.addInputSlot(x, y).addIngredients(ing);
                    continue;
                }
            }
            builder.addInputSlot(x, y);
        }

        if (recipe.catalyst != null) {
            builder.addInputSlot(97, 22).addIngredients(recipe.catalyst);
        } else {
            builder.addInputSlot(97, 22);
        }

        if (recipe.fuelUsage() > 0) {
            builder.addInputSlot(84, 53).addItemStack(new ItemStack(RegCustomItem.UNTREATED_MOONDUST));
        } else {
            builder.addInputSlot(84, 53);
        }

        DynamicRegistryManager drm = MinecraftClient.getInstance().world != null
                ? MinecraftClient.getInstance().world.getRegistryManager()
                : DynamicRegistryManager.EMPTY;
        builder.addOutputSlot(134, 35).addItemStack(recipe.getOutput(drm));
    }

    @Override
    public void getTooltip(@NotNull ITooltipBuilder tooltip, @NotNull AltarShapelessRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 84 && mouseX <= 127 && mouseY >= 39 && mouseY <= 48) {
            tooltip.add(Text.translatable("gui.shape_shifter_curse.jei.altar.recipe_id", recipe.getId().toString()));
            if (recipe.requireAdvancement != null) {
                tooltip.add(Text.translatable("gui.shape_shifter_curse.jei.altar.requires_advancement", getAdvancementName(recipe.requireAdvancement)));
            }
        }
    }

    private Text getAdvancementName(Identifier id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null) {
            Advancement adv = client.getNetworkHandler().getAdvancementHandler().getManager().get(id);
            if (adv != null && adv.getDisplay() != null) {
                return adv.getDisplay().getTitle();
            }
        }
        return Text.literal(id.toString());
    }
}