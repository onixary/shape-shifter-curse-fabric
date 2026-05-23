package net.onixary.shapeShifterCurseFabric.recipes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.*;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class UpgradeRecipe implements SmithingRecipe, ISmithingRecipeEX {
    private final Identifier id;
    private final Predicate<ItemStack> template;
    private final Predicate<ItemStack> base;
    private final Predicate<ItemStack> addition;
    private final Function<ItemStack, ItemStack> upgradeResult;

    public boolean isUpgradeAll() {
        return false;
    }

    public UpgradeRecipe(Identifier id, Predicate<ItemStack> template, Predicate<ItemStack> base, Predicate<ItemStack> addition, Function<ItemStack, ItemStack> upgradeResult) {
        this.id = id;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.upgradeResult = upgradeResult;
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.template.test(inventory.getStack(0)) && this.base.test(inventory.getStack(1)) && this.addition.test(inventory.getStack(2));
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        ItemStack itemStack = inventory.getStack(1);
        if (this.base.test(itemStack)) {
            ItemStack outputStack = itemStack.copy();
            if (!isUpgradeAll()) {
                outputStack.setCount(1);
            }
            return this.upgradeResult.apply(outputStack);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
        return this.upgradeResult.apply(itemStack.copy());
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean overrideVanillaOnTakeOutput() {
        return this.isUpgradeAll();
    }

    @Override
    public void onTakeOutput(SmithingScreenHandler screenHandler, PlayerEntity player, ItemStack stack) {
        if (this.isUpgradeAll()) {
            screenHandler.decrementStack(0);
            screenHandler.input.setStack(1, ItemStack.EMPTY);
            screenHandler.decrementStack(2);
        }
    }
}
