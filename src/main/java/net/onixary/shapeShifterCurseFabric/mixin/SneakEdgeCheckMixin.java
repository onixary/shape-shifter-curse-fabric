package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.ModifyStepHeightPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(value = PlayerEntity.class, priority = 1200)
public class SneakEdgeCheckMixin {

    @Redirect(method = "adjustMovementForSneaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStepHeight()F"))
    private float redirectGetStepHeightForSneaking(PlayerEntity playerEntity) {
        return getOriginalOrModifiedStepHeight(playerEntity);
    }

    @Redirect(method = "method_30263", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStepHeight()F"))
    private float redirectGetStepHeightForEdgeCheck(PlayerEntity playerEntity) {
        return getOriginalOrModifiedStepHeight(playerEntity);
    }

    @Unique
    private float getOriginalOrModifiedStepHeight(PlayerEntity playerEntity) {
        // 检查是否有ModifyStepHeightPower设置了不影响力边缘检测
        boolean shouldUseOriginalHeight = PowerHolderComponent.getPowers(playerEntity, ModifyStepHeightPower.class)
                .stream()
                .anyMatch(power -> !power.shouldAffectSneak());

        // 如果需要使用原始高度，则返回原版的0.5f，否则返回修改后的step height
        if (shouldUseOriginalHeight) {
            return 0.5f; // 原版潜行边缘检测高度
        }

        return playerEntity.getStepHeight();
    }
}
