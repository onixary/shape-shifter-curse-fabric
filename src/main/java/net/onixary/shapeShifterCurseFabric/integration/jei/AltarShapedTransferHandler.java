package net.onixary.shapeShifterCurseFabric.integration.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.onixary.shapeShifterCurseFabric.custom_ui.AltarCraftUIHandler;
import net.onixary.shapeShifterCurseFabric.custom_ui.RegMenuType;
import net.onixary.shapeShifterCurseFabric.recipes.altar.AltarShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AltarShapedTransferHandler implements IRecipeTransferHandler<AltarCraftUIHandler, AltarShapedRecipe> {

    @Override
    public @NotNull Class<? extends AltarCraftUIHandler> getContainerClass() {
        return AltarCraftUIHandler.class;
    }

    @Override
    public @NotNull Optional<ScreenHandlerType<AltarCraftUIHandler>> getMenuType() {
        return Optional.of(RegMenuType.AltarCraftUI);
    }

    @Override
    public @NotNull RecipeType<AltarShapedRecipe> getRecipeType() {
        return SSC_JEI_Plugin.ALTAR_SHAPED;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(
            @NotNull AltarCraftUIHandler container,
            @NotNull AltarShapedRecipe recipe,
            @NotNull IRecipeSlotsView recipeSlots,
            @NotNull PlayerEntity player,
            boolean maxTransfer,
            boolean doTransfer) {

        if (!doTransfer) {
            return null;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager im = client.interactionManager;
        if (im == null) {
            return null;
        }

        int syncId = container.syncId;

        clearAltarSlots(container, im, player, syncId);

        List<Integer> targetSlots = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();
        for (int row = 0; row < recipe.height; row++) {
            for (int col = 0; col < recipe.width; col++) {
                targetSlots.add(col + row * 3);
                ingredients.add(recipe.input.get(col + row * recipe.width));
            }
        }

        int count = maxTransfer ? calcMaxCount(container, ingredients) : 1;

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ing = ingredients.get(i);
            if (!ing.isEmpty()) {
                moveNTo(container, im, player, syncId, ing, targetSlots.get(i), count);
            }
        }
        if (recipe.catalyst != null) {
            moveNTo(container, im, player, syncId, recipe.catalyst, 9, 1);
        }

        return null;
    }

    private void clearAltarSlots(AltarCraftUIHandler container, ClientPlayerInteractionManager im, PlayerEntity player, int syncId) {
        for (int i = 0; i <= 9; i++) {
            Slot slot = container.getSlot(i);
            if (slot.hasStack()) {
                im.clickSlot(syncId, i, 0, SlotActionType.PICKUP, player);
                int emptyPlayerSlot = findEmptyPlayerSlot(container);
                if (emptyPlayerSlot != -1) {
                    im.clickSlot(syncId, emptyPlayerSlot, 0, SlotActionType.PICKUP, player);
                } else {
                    im.clickSlot(syncId, -999, 0, SlotActionType.PICKUP, player);
                }
            }
        }
    }

    private int findEmptyPlayerSlot(AltarCraftUIHandler container) {
        for (int i = 12; i < 48; i++) {
            if (!container.getSlot(i).hasStack()) {
                return i;
            }
        }
        return -1;
    }

    private int calcMaxCount(AltarCraftUIHandler container, List<Ingredient> ingredients) {
        int min = 64;
        for (Ingredient ing : ingredients) {
            if (ing.isEmpty()) continue;
            min = Math.min(min, countInPlayer(container, ing));
        }
        return min;
    }

    private int countInPlayer(AltarCraftUIHandler container, Ingredient ing) {
        int count = 0;
        for (int i = 12; i < 48; i++) {
            ItemStack stack = container.getSlot(i).getStack();
            if (!stack.isEmpty() && ing.test(stack)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void moveNTo(AltarCraftUIHandler container, ClientPlayerInteractionManager im,
                         PlayerEntity player, int syncId, Ingredient ing, int targetSlot, int n) {
        if (n <= 0) {
            return;
        }
        int remaining = n;
        for (int i = 12; i < 48 && remaining > 0; i++) {
            Slot slot = container.getSlot(i);
            ItemStack stack = slot.getStack();
            if (stack.isEmpty() || !ing.test(stack)) {
                continue;
            }
            int available = stack.getCount();
            int take = Math.min(remaining, available);

            im.clickSlot(syncId, i, 0, SlotActionType.PICKUP, player);

            if (take == available) {
                im.clickSlot(syncId, targetSlot, 0, SlotActionType.PICKUP, player);
            } else {
                for (int j = 0; j < take; j++) {
                    im.clickSlot(syncId, targetSlot, 1, SlotActionType.PICKUP, player);
                }
                im.clickSlot(syncId, i, 0, SlotActionType.PICKUP, player);
            }

            remaining -= take;
        }
    }
}