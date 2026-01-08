package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.EnchantCommand;
import net.onixary.shapeShifterCurseFabric.util.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantCommand.class)
public class EnchantmentUtilEnchantCommandMixin {
    @ModifyExpressionValue(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean execute(boolean original, @Local ItemStack itemStack, @Local Enchantment enchantment) {
        if (!original) {
            return EnchantmentUtils.isItemCanEnchantment(enchantment, itemStack);
        }
        return original;
    }
}
