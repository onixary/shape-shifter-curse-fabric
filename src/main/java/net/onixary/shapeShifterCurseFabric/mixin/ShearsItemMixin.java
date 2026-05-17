package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.util.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {
    @Inject(method = "postMine", at = @At("RETURN"), cancellable = true)
    private void postMineMixin(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && state.isIn(ModTags.LIKE_COBWEB_TAG)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getMiningSpeedMultiplier", at = @At("HEAD"), cancellable = true)
    private void getMiningSpeedMultiplierMixin(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        if (state.isIn(ModTags.LIKE_COBWEB_TAG)) {
            cir.setReturnValue(15.0f);
        }
    }
}
