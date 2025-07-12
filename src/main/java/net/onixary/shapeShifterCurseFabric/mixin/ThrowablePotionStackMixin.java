package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThrowablePotionItem.class)
public abstract  class ThrowablePotionStackMixin {
    @Inject(method = "use", at = @At("RETURN"))
    private void addCooldown(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient) {
            player.getItemCooldownManager().set((ThrowablePotionItem) (Object) this, 20); // 20 ticks = 1 second
        }
    }
}
