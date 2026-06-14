package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.onixary.shapeShifterCurseFabric.recipes.ISmithingRecipeEX;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SmithingScreenHandler.class)
public class SmithingScreenHandlerMixin {
    @Inject(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;unlockLastRecipe(Lnet/minecraft/entity/player/PlayerEntity;Ljava/util/List;)V", shift = At.Shift.AFTER), cancellable = true)
    public void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        SmithingScreenHandler realThis = (SmithingScreenHandler) (Object) this;
        List<SmithingRecipe> list = realThis.world.getRecipeManager().getAllMatches(RecipeType.SMITHING, realThis.input, realThis.world);
        if (list.isEmpty()) {
            return;
        }
        SmithingRecipe recipe = list.get(0);
        if (recipe instanceof ISmithingRecipeEX iSmithingRecipeEX) {
            iSmithingRecipeEX.onTakeOutput(realThis, player, stack);
            if (iSmithingRecipeEX.overrideVanillaOnTakeOutput()) {
                realThis.context.run((world, pos) -> world.syncWorldEvent(1044, (BlockPos)pos, 0));
                ci.cancel();
            }
        }
    }
}
