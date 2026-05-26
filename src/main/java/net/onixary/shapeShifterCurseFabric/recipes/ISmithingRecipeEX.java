package net.onixary.shapeShifterCurseFabric.recipes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SmithingScreenHandler;

public interface ISmithingRecipeEX {
    public boolean overrideVanillaOnTakeOutput();

    public void onTakeOutput(SmithingScreenHandler screenHandler, PlayerEntity player, ItemStack stack);
}
