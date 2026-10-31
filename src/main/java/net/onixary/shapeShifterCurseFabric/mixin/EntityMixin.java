package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.onixary.shapeShifterCurseFabric.additional_power.AlwaysSprintSwimmingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.BypassesLandingEffectsPower;
import net.onixary.shapeShifterCurseFabric.additional_power.BypassesSteppingEffectsPower;
import net.onixary.shapeShifterCurseFabric.additional_power.ModifyFootstepSoundSpeedPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "bypassesSteppingEffects", at = @At("RETURN"), cancellable = true)
    private void bypassesSteppingEffects(CallbackInfoReturnable<Boolean> cir) {
        // 只有玩家需要这个Power 所以为了减少性能开销 所以做了个判断
        if ((Object)this instanceof PlayerEntity player && !cir.getReturnValueZ()) {
            int powerCount = PowerHolderComponent.getPowers(player, BypassesSteppingEffectsPower.class).size();
            if (powerCount > 0) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "bypassesLandingEffects", at = @At("RETURN"), cancellable = true)
    private void bypassesLandingEffects(CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof PlayerEntity player && !cir.getReturnValueZ()) {
            int powerCount = PowerHolderComponent.getPowers(player, BypassesLandingEffectsPower.class).size();
            if (powerCount > 0) {
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private boolean shapeShifter$longStepInterval;

    @Inject(method = "calculateNextStepSoundDistance", at = @At("RETURN"), cancellable = true)
    private void modifyFootstepSoundDistance(CallbackInfoReturnable<Float> cir) {
        if ((Object)this instanceof PlayerEntity player) {
            var powers = PowerHolderComponent.getPowers(player, ModifyFootstepSoundSpeedPower.class);
            if (!powers.isEmpty()) {
                var power = powers.get(0);
                if (player.isTouchingWater()) {
                    // This vanilla threshold also schedules swimming sounds; retain their uniform cadence.
                    shapeShifter$longStepInterval = false;
                    cir.setReturnValue(player.distanceTraveled + 1.0f / power.getSpeedMultiplierFor(player));
                } else {
                    cir.setReturnValue(player.distanceTraveled + power.getStepSoundDistance(player, 1.0f, shapeShifter$longStepInterval));
                    shapeShifter$longStepInterval = !shapeShifter$longStepInterval;
                }
            } else {
                shapeShifter$longStepInterval = false;
            }
        }
    }

    private boolean lastIsSwimming = false;

    @Inject(method = "updateSwimming", at = @At("HEAD"))
    private void updateSwimmingH(CallbackInfo ci) {
        lastIsSwimming = ((Entity) (Object) this).isSwimming();
    }

    @Inject(method = "updateSwimming", at = @At("TAIL"))
    private void updateSwimmingT(CallbackInfo ci) {
        if (((Entity) (Object) this).isSwimming()) {
            return;
        }
        if ((Object) this instanceof PlayerEntity player && PowerHolderComponent.hasPower(player, AlwaysSprintSwimmingPower.class)) {
            if (lastIsSwimming) {
                player.setSwimming(player.isTouchingWater() && !player.hasVehicle());
            } else {
                player.setSwimming(player.isSubmergedInWater() && !player.hasVehicle() && player.getWorld().getFluidState(player.getBlockPos()).isIn(FluidTags.WATER));
            }
        }
    }
}
