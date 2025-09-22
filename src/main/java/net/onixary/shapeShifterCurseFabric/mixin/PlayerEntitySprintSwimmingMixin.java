package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.additional_power.AlwaysSprintSwimmingPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntitySprintSwimmingMixin {

    @ModifyArg(method = "addExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"), index = 0)
    private float modifySwimmingHungerConsumption(float exhaustion) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.isSwimming()) {
            return PowerHolderComponent.getPowers(player, AlwaysSprintSwimmingPower.class).stream()
                    .map(power -> exhaustion * power.getHungerMultiplier())
                    .findFirst()
                    .orElse(exhaustion);
        }
        return exhaustion;
    }

    @Inject(method = "updateSwimming", at = @At("TAIL"))
    private void forceSwimmingUnderwater(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (PowerHolderComponent.hasPower(player, AlwaysSprintSwimmingPower.class)
                && player.isSubmergedInWater()
                && !player.hasVehicle()
                && player.getWorld().getFluidState(player.getBlockPos()).isIn(FluidTags.WATER)) {
            player.setSwimming(true);
        }
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"), index = 0)
    private Vec3d modifyVerticalVelocity(Vec3d originalVelocity) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (PowerHolderComponent.hasPower(player, AlwaysSprintSwimmingPower.class)){
            // 只有在冲刺时才修改Y轴速度
            if (player.isSprinting()) {
                return originalVelocity;
            } else {
                // 保持原有的X和Z速度，但不修改Y速度
                Vec3d currentVelocity = player.getVelocity();
                return new Vec3d(originalVelocity.x, currentVelocity.y, originalVelocity.z);
            }
        }
        return originalVelocity;
    }
}
