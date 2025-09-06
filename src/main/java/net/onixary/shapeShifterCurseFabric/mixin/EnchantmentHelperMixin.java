package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.LootingPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getLooting", at = @At("RETURN"), cancellable = true)
    private static void getLootingMixin(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        int powerLooting = PowerHolderComponent.getPowers(entity, LootingPower.class)
                .stream()
                .mapToInt(LootingPower::getLevel)
                .sum(); // 叠加所有能力等级
        cir.setReturnValue(cir.getReturnValue() + powerLooting);
    }
}
