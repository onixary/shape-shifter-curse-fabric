package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.render.form_render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// NECK FEATURES FILE

@Mixin(HeadFeatureRenderer.class)
public class LongNeckHeadFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> {
    @Unique private static FormRenderUtils.BoneBipedState headBoneState;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void shape_shifter_curse$modifyHeadStateForMAS(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T livingEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (!(livingEntity instanceof AbstractClientPlayerEntity player)) {
            return;
        }
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
        FormRenderer renderer = FormRenderUtils.searchFirstRenderer(player, formRenderer -> {
            FormModel formModel = formRenderer.realModel;
            if (formModel == null) {
                return false;
            }
            return formModel.AnimationSystem instanceof IModifyHead_MAS;
        });
        if (renderer != null) {
            headBoneState = new FormRenderUtils.BoneBipedState(playerEntityRenderer.getModel().getHead());
            ((IModifyHead_MAS)renderer.realModel.AnimationSystem).modifyHeadPart(player, playerEntityRenderer.getModel(), renderer.realModel);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("RETURN"))
    private void shape_shifter_curse$restoreVanillaHeadFeatureForLongNeck(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T livingEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (headBoneState != null) {
            headBoneState.restore();
            headBoneState = null;
        }
    }
}
