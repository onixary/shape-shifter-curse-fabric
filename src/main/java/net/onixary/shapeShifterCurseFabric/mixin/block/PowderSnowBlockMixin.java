package net.onixary.shapeShifterCurseFabric.mixin.block;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.PowderSnowWalkerPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(method = "canWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void allowPowderSnowWalking(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity livingEntity
                && PowerHolderComponent.hasPower(livingEntity, PowderSnowWalkerPower.class)) {
            cir.setReturnValue(true);
        }
    }
}
