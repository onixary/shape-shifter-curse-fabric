package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(BowItem.class)
public abstract class CompoundBowMixin {
    @ModifyExpressionValue(method = "onStoppedUsing", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ArrowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;"))
    private PersistentProjectileEntity ssc$kineticArrow(PersistentProjectileEntity arrow, @Local(argsOnly = true) ItemStack bow) {
        if (bow.isOf(RegCustomItem.COMPOUND_KINETIC_BOW)) arrow.setDamage(arrow.getDamage() * 1.25);
        return arrow;
    }
}
