package net.onixary.shapeShifterCurseFabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerEntitySpeedFOVMixin {

    /**
     * 注入到 getFovMultiplier 方法的末尾，以限制其返回值。
     * 这可以精确地控制因速度变化引起的FOV缩放效果，而不会影响其他FOV修改（如望远镜）。
     *
     * @param cir 回调信息，用于修改返回值。
     */
    @Inject(method = "getFovMultiplier", at = @At("RETURN"), cancellable = true)
    private void shape_shifter_curse$limitFovMultiplier(CallbackInfoReturnable<Float> cir) {
        float originalMultiplier = cir.getReturnValue();

        float minMultiplier = 0.95f;
        float maxMultiplier = 1.25f;

        float clampedMultiplier = originalMultiplier;
        if (clampedMultiplier > maxMultiplier) {
            clampedMultiplier = maxMultiplier;
        }
        if (clampedMultiplier < minMultiplier) {
            clampedMultiplier = minMultiplier;
        }

        cir.setReturnValue(clampedMultiplier);
    }
}
