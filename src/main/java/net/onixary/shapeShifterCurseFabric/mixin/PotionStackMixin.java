package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.screen.slot.Slot;
import net.onixary.shapeShifterCurseFabric.additional_power.ModifyPotionStackPower;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Slot.class)
public abstract class PotionStackMixin {

    @Shadow @Final
    public Inventory inventory;

    /*
    @Redirect(
            method = "getMaxItemCount(Lnet/minecraft/item/ItemStack;)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I")
    )
    private int modifyPotionStackSize(ItemStack itemStack) {
        if (this.inventory instanceof PlayerInventory) {
            PlayerEntity player = ((PlayerInventory) this.inventory).player;

            if (itemStack.getItem() instanceof PotionItem) {
                int StackCount = PowerHolderComponent.getPowers(player, ModifyPotionStackPower.class)
                        .stream()
                        .mapToInt(ModifyPotionStackPower::getCount)
                        .max().orElseGet(() -> 1);
                return Math.max(StackCount, itemStack.getMaxCount());
            }
        }

        return itemStack.getMaxCount();
    }
     */
    @Inject(method = "getMaxItemCount(Lnet/minecraft/item/ItemStack;)I", at=@At(value = "RETURN"), cancellable = true)
    private void modifyPotionStackSize(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        if (this.inventory instanceof PlayerInventory) {
            PlayerEntity player = ((PlayerInventory) this.inventory).player;
            if (itemStack.getItem() instanceof PotionItem) {
                int StackCount = PowerHolderComponent.getPowers(player, ModifyPotionStackPower.class)
                        .stream()
                        .mapToInt(ModifyPotionStackPower::getCount)
                        .max().orElseGet(() -> 1);
                cir.setReturnValue(Math.max(StackCount, cir.getReturnValue()));
            }
        }
    }
}
