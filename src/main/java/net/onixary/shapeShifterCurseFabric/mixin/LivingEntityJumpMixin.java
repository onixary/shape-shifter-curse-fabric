package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.TripleJumpPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityJumpMixin {

    // 注入到 jump() 方法的开头，用于更新 Power 的状态
    @Inject(method = "jump", at = @At("HEAD"))
    private void onJump(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        // 在计算跳跃速度之前，先让 Power 更新它的内部状态（如跳跃次数）
        PowerHolderComponent.getPowers(entity, TripleJumpPower.class).forEach(TripleJumpPower::onJump);
    }

    @ModifyReturnValue(method = "getJumpVelocity", at = @At("RETURN"))
    private float modifyJumpVelocity(float originalVelocity) {
        LivingEntity entity = (LivingEntity) (Object) this;

        return PowerHolderComponent.getPowers(entity, TripleJumpPower.class).stream()
                .findFirst()
                .map(power -> {
                    float powerMultiplier = power.getActiveJumpMultiplier();

                    if (powerMultiplier != 1.0f) {
                        // 假设基础跳跃速度为 0.42F
                        float baseJumpVelocity = 0.42F;
                        // 计算额外效果（如跳跃提升）
                        float additionalVelocity = originalVelocity - baseJumpVelocity;
                        // 只对基础速度应用倍率，保持额外效果不变
                        return (baseJumpVelocity * powerMultiplier) + additionalVelocity;
                    }
                    return originalVelocity;
                })
                .orElse(originalVelocity);
    }
}
