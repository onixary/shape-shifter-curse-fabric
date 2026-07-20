package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.ModifyStepHeightPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerEntity.class, priority = 1200)
public class SneakEdgeCheckMixin {

    @WrapOperation(method = "adjustMovementForSneaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStepHeight()F"))
    private float modifyStepHeightForSneaking(PlayerEntity instance, Operation<Float> original) {
        return getOriginalOrModifiedStepHeight(instance, original);
    }

    @WrapOperation(method = "method_30263", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStepHeight()F"))
    private float modifyStepHeightForEdgeCheck(PlayerEntity instance, Operation<Float> original) {
        return getOriginalOrModifiedStepHeight(instance, original);
    }

    @Unique
    private float getOriginalOrModifiedStepHeight(PlayerEntity playerEntity, Operation<Float> operation) {
        // 检查是否有ModifyStepHeightPower设置了不影响力边缘检测
        boolean shouldUseOriginalHeight = PowerHolderComponent.getPowers(playerEntity, ModifyStepHeightPower.class)
                .stream()
                .anyMatch(power -> !power.shouldAffectSneak());

        // 如果需要使用原始高度，则返回原版的0.5f，否则返回修改后的step height
        if (shouldUseOriginalHeight) {
            return 0.5f; // 原版潜行边缘检测高度
        }

        return operation.call(playerEntity);
    }
}
