package net.onixary.shapeShifterCurseFabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerEntitySpeedFOVMixin {

    /**
     * 注入到 getFovMultiplier 方法的末尾，以限制其返回值。
     * 这可以精确地控制因速度变化引起的FOV缩放效果，而不会影响其他FOV修改（如望远镜）。
     *
     * @param cir 回调信息，用于修改返回值。
     * @Inject(method = "getFovMultiplier", at = @At("RETURN"), cancellable = true)
     * private void shape_shifter_curse$limitFovMultiplier(CallbackInfoReturnable<Float> cir) {
     *     float originalMultiplier = cir.getReturnValue();
     *     float minMultiplier = 0.95f;
     *     float maxMultiplier = 1.25f;
     *     float clampedMultiplier = originalMultiplier;
     *     if (clampedMultiplier > maxMultiplier) {
     *         clampedMultiplier = maxMultiplier;
     *     }
     *     if (clampedMultiplier < minMultiplier) {
     *         clampedMultiplier = minMultiplier;
     *     }
     *     cir.setReturnValue(clampedMultiplier);
     * }
    */

    @Unique  // 0.95
    private final float nowSpeedMaxMul = 0.95f * 2 - 1.0f;
    @Unique  // 2.5
    private final float nowSpeedMinMul = 2.5f * 2 - 1.0f;

    // 旧的会破坏望远镜等FOV修改 尝试用新的方法 为了减少冲突不用重定向
    // f *= ((float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) / this.getAbilities().getWalkSpeed() + 1.0F) / 2.0F;
    @ModifyExpressionValue(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;getWalkSpeed()F"))
    private float shape_shifter_curse$modifyWalkSpeed(float original) {
        var self = (AbstractClientPlayerEntity) (Object) this;
        if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(self)) {
            return original;
        }
        float nowSpeed = (float) ((self).getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
        return Math.min(nowSpeedMinMul * nowSpeed, Math.max(nowSpeedMaxMul * nowSpeed, original));
    }

}
