package net.onixary.shapeShifterCurseFabric.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = HeldItemFeatureRenderer.class, priority = 99999999)
public abstract class AdjustItemHoldFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> {
    /*private LivingEntity cachedEntity; // 缓存 livingEntity

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD")
    )
    private void cacheLivingEntity(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            LivingEntity entity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch,
            CallbackInfo ci
    ) {
        this.cachedEntity = entity; // 缓存传入的 livingEntity
    }


    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/feature/HeldItemFeatureRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
            )
    )
    private MatrixStack modifyMatrixStack(MatrixStack originalMatrices) {
        // 创建一个新的 MatrixStack 或修改原始的 MatrixStack
        MatrixStack modifiedMatrices = new MatrixStack();
        modifiedMatrices.push();
        modifiedMatrices.scale(0.01F, 0.01F, 0.01F); // 添加缩放操作
        modifiedMatrices.pop();
        if (shouldHideItem(this.cachedEntity)) {
            return modifiedMatrices;
        }
        else{
            return originalMatrices;
        }

    }*/

    @Inject(at =
    @At(value = "HEAD"),
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            cancellable = true
    )
    private void hideHeldItem(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            LivingEntity entity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch,
            CallbackInfo ci
    ) {
        // 条件判断：例如隐藏特定玩家或满足条件时

        if (shouldHideItem(entity)) {
            ci.cancel(); // 取消原版渲染逻辑
        }
    }

    private boolean shouldHideItem(LivingEntity entity) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            PlayerFormBase curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
            boolean isFeral = curForm.getBodyType() == PlayerFormBodyType.FERAL;
            //ShapeShifterCurseFabric.LOGGER.info("Is Feral Form : " + isFeral);
            return isFeral;
        }
        return false;
    }
}
