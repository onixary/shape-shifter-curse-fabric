package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.additional_power.LootingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.SoulSpeedPower;
import net.onixary.shapeShifterCurseFabric.util.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Unique
    private static int getLootingLevel(LivingEntity entity, int PreValue) {
        AtomicInteger powerLooting = new AtomicInteger(PreValue);
        PowerHolderComponent.getPowers(entity, LootingPower.class).forEach(power -> powerLooting.set(power.getLevel(powerLooting.get())));
        return powerLooting.get();
    }

    @Unique
    private static int getSoulSpeedLevel(LivingEntity entity, int PreValue) {
        AtomicInteger powerSoulSpeed = new AtomicInteger(PreValue);
        PowerHolderComponent.getPowers(entity, SoulSpeedPower.class).forEach(power -> powerSoulSpeed.set(power.getLevel(powerSoulSpeed.get())));
        return powerSoulSpeed.get();
    }

    @Inject(method = "getEquipmentLevel", at = @At("RETURN"), cancellable = true)
    private static void getEquipmentLevelMixin(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (enchantment == Enchantments.LOOTING) {
            cir.setReturnValue(getLootingLevel(entity, cir.getReturnValue()));
        } else if (enchantment == Enchantments.SOUL_SPEED) {
            cir.setReturnValue(getSoulSpeedLevel(entity, cir.getReturnValue()));
        }
    }

    @Inject(method = "hasSoulSpeed", at = @At("RETURN"), cancellable = true)
    private static void hasSoulSpeedMixin(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }
        cir.setReturnValue(!PowerHolderComponent.getPowers(entity, SoulSpeedPower.class).isEmpty());
    }

    @ModifyExpressionValue(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean isAcceptableItem(boolean original, @Local(ordinal = 0) ItemStack itemStack, @Local Enchantment enchantment) {
        if (!original) {
            return EnchantmentUtils.isItemCanEnchantment(enchantment, itemStack);
        }
        return original;
    }
}
