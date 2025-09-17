package net.onixary.shapeShifterCurseFabric.mixin.mob;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.PillagerFriendlyPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VindicatorEntity.class)
public abstract class VindicatorEntityMixin {

    @Inject(method = "isTeammate", at = @At("HEAD"), cancellable = true)
    private void onIsTeammateCheck(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (other instanceof PlayerEntity player) {
            if (PowerHolderComponent.hasPower(player, PillagerFriendlyPower.class)) {
                cir.setReturnValue(true);
            }
        }
    }
}
