package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.onixary.shapeShifterCurseFabric.util.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AnvilScreenHandler.class)
public class EnchantmentUtilAnvilMixin {
    @ModifyExpressionValue(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean isAcceptableItem(boolean original, @Local(ordinal = 0) ItemStack itemStack, @Local Enchantment enchantment) {
        if (!original) {
            return EnchantmentUtils.isItemCanEnchantment(enchantment, itemStack);
        }
        return original;
    }
}
