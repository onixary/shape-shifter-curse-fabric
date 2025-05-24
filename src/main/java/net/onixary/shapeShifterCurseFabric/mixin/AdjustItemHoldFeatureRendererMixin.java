package net.onixary.shapeShifterCurseFabric.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemFeatureRenderer.class)
public abstract class AdjustItemHoldFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> {
    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"),
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
        if (entity instanceof ClientPlayerEntity player) {
            PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
            boolean isFeral = RegFormConfig.getConfig(curForm).getBodyType() == PlayerFormBodyType.FERAL;
            //ShapeShifterCurseFabric.LOGGER.info("Is Feral Form : " + isFeral);
            return isFeral;
        }
        return false;
    }
}
