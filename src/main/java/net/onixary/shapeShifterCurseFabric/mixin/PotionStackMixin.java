package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.screen.slot.Slot;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Slot.class)
public abstract class PotionStackMixin {

    @Shadow @Final
    public Inventory inventory;

    @Redirect(
            method = "getMaxItemCount(Lnet/minecraft/item/ItemStack;)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I")
    )
    private int modifyPotionStackSize(ItemStack itemStack) {
        if (this.inventory instanceof PlayerInventory) {
            PlayerEntity player = ((PlayerInventory) this.inventory).player;

            if (itemStack.getItem() instanceof PotionItem) {
                PlayerForms currentForm = FormAbilityManager.getForm(player);
                if (currentForm == PlayerForms.FAMILIAR_FOX_2 || currentForm == PlayerForms.FAMILIAR_FOX_3) {
                    return 3;
                }
            }
        }

        return itemStack.getMaxCount();
    }
}
