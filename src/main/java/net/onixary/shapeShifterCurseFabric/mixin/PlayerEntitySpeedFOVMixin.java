package net.onixary.shapeShifterCurseFabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Unique
    private float shape_shifter_curse$originalWalkSpeed;

    // 旧的会破坏望远镜等FOV修改 尝试用新的方法 为了减少冲突不用重定向
    // f *= ((float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) / this.getAbilities().getWalkSpeed() + 1.0F) / 2.0F;
    @Inject(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;getWalkSpeed()F"))
    private void shape_shifter_curse$modifyWalkSpeed(CallbackInfoReturnable<Float> cir) {
        shape_shifter_curse$originalWalkSpeed = ((AbstractClientPlayerEntity) (Object) this).getAbilities().getWalkSpeed();
        // PlayerFormBase playerFormBase = RegPlayerFormComponent.PLAYER_FORM.get((AbstractClientPlayerEntity) (Object) this).getCurrentForm();
        // if (playerFormBase.FormID == RegPlayerForms.ORIGINAL_BEFORE_ENABLE.FormID) {
        //     return;
        // }
        float nowSpeed = (float)(((AbstractClientPlayerEntity) (Object) this).getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
        // 0.6(0.8 * 2 - 1.0) <= nowSpeed / targetWalkSpeed <= 4.0(2.5 * 2 - 1.0)
        float targetWalkSpeed = Math.min(4.0f * nowSpeed, Math.max(0.6f * nowSpeed, shape_shifter_curse$originalWalkSpeed));
        ((AbstractClientPlayerEntity) (Object) this).getAbilities().setWalkSpeed(targetWalkSpeed);
    }

    @Inject(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;getWalkSpeed()F", shift = At.Shift.AFTER))
    private void shape_shifter_curse$restoreWalkSpeed(CallbackInfoReturnable<Float> cir) {
        ((AbstractClientPlayerEntity) (Object) this).getAbilities().setWalkSpeed(shape_shifter_curse$originalWalkSpeed);
    }
}
