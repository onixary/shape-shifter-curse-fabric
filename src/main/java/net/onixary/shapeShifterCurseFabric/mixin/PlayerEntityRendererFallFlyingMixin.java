package net.onixary.shapeShifterCurseFabric.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 这一mixin与ViveCraft mod冲突，当其存在时禁用此mixin
// This mixin conflicts with the ViveCraft mod, disable this mixin when it exists
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererFallFlyingMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererFallFlyingMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }


    @Inject(method = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;setupTransforms(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"),
            slice = {
                @Slice(
                        from = @At(value = "INVOKE", target = "Ljava/lang/Math;acos(D)D"),
                        to = @At(value = "RETURN")
                ),
            },
            cancellable = true
    )
    public void setupTransformsInject(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h, CallbackInfo ci) {
        // 如果 vivecraft 对此Inject仍不兼容 把下面代码解除注释
        /*
        if (FabricLoader.getInstance().isModLoaded("vivecraft")) {
            ShapeShifterCurseFabric.LOGGER.info("ViveCraft mod detected, skipping PlayerEntityRendererFallFlyingMixin.");
            return;
        }
        */
        boolean isFeral = RegPlayerFormComponent.PLAYER_FORM.get(abstractClientPlayerEntity).getCurrentForm().getBodyType() == PlayerFormBodyType.FERAL;
        if(!isFeral){
            return;
        }
        else{
            // 补充变量
            Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
            Vec3d vec3d2 = abstractClientPlayerEntity.lerpVelocity(h);
            double d = vec3d2.horizontalLengthSquared();
            double e = vec3d.horizontalLengthSquared();
            double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
            double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;

            // Feral形态的特殊旋转
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(Math.signum(m) * Math.acos(l)) * 180.0F / (float)Math.PI));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.0F)); // 不向下倾斜
            // 你可以在这里添加任何额外的旋转
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F)); // 使翅膀向上
            ci.cancel();
        }
    }

    // 老逻辑
    /*
     * @author OnyxAmber
     * @reason Feral form elytra cancel rotation
     */
    /* @Overwrite
    public void setupTransforms(AbstractClientPlayerEntity player, MatrixStack matrices, float f, float g, float h) {
        // 这一mixin与ViveCraft mod冲突，当其存在时禁用此mixin
        // This mixin conflicts with the ViveCraft mod, disable this mixin when it exists
        boolean IS_VIVECRAFT_LOADED = FabricLoader.getInstance().isModLoaded("vivecraft");
        if (IS_VIVECRAFT_LOADED) {
            super.setupTransforms(player, matrices, f, g, h);
            ShapeShifterCurseFabric.LOGGER.info("ViveCraft mod detected, skipping PlayerEntityRendererFallFlyingMixin.");
            return;
        }
        PlayerFormBase curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
        boolean isFeral = curForm.getBodyType() == PlayerFormBodyType.FERAL;

        float i = player.getLeaningPitch(h);
        if (player.isFallFlying()) {
            super.setupTransforms(player, matrices, f, g, h);
            float j = (float)player.getRoll() + h;
            float k = MathHelper.clamp(j * j / 100.0f, 0.0f, 1.0f);

            if (!player.isUsingRiptide()) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(k * (-90.0f - player.getPitch())));
            }
            Vec3d vec3d = player.getRotationVec(h);
            Vec3d vec3d2 = player.lerpVelocity(h);
            double d = vec3d2.horizontalLengthSquared();
            double e = vec3d.horizontalLengthSquared();
            if (d > 0.0 && e > 0.0) {
                double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                if(!isFeral){
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(Math.signum(m) * Math.acos(l))));
                }
                else{
                    // Feral形态的特殊旋转
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(Math.signum(m) * Math.acos(l)) * 180.0F / (float)Math.PI));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.0F)); // 不向下倾斜
                    // 你可以在这里添加任何额外的旋转
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F)); // 使翅膀向上
                }
            }
        }
        else if (i > 0.0f) {
            super.setupTransforms(player, matrices, f, g, h);
            float j = player.isTouchingWater() ? -90.0f - player.getPitch() : -90.0f;
            float k = MathHelper.lerp(i, 0.0f, j);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(k));
            if (player.isInSwimmingPose()) {
                matrices.translate(0.0f, -1.0f, 0.3f);
            }
        } else {
            super.setupTransforms(player, matrices, f, g, h);
        }
    }
    */
}

