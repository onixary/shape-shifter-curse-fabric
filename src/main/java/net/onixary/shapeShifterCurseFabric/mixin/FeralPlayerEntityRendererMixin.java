package net.onixary.shapeShifterCurseFabric.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 取消feral的潜行下压
@Environment(EnvType.CLIENT) // 仅客户端渲染逻辑
@Mixin(PlayerEntityRenderer.class)
public abstract class FeralPlayerEntityRendererMixin {

    /**
     * 取消潜行时的模型下压动画
     */
    @Inject(method = "getPositionOffset*", at = @At("RETURN"), cancellable = true)
    private void cancelSneakOffset(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, CallbackInfoReturnable<Vec3d> ci)
    {
        if(abstractClientPlayerEntity instanceof AbstractClientPlayerEntity){
            PlayerFormBase curForm = RegPlayerFormComponent.PLAYER_FORM.get(abstractClientPlayerEntity).getCurrentForm();
            boolean isFeral = curForm.getBodyType() == PlayerFormBodyType.FERAL;
            if(isFeral){
                ci.setReturnValue(Vec3d.ZERO);
            }
        }
    }
}
