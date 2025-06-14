package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererFallFlyingMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererFallFlyingMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    /**
     * @author OnyxAmber
     * @reason Feral form elytra cancel rotation
     */
    @Overwrite
    public void setupTransforms(AbstractClientPlayerEntity player, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        super.setupTransforms(player, matrices, animationProgress, bodyYaw, tickDelta);

        // 检查是否是Feral形态
        PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
        boolean isFeral = RegFormConfig.getConfig(curForm).getBodyType() == PlayerFormBodyType.FERAL;

        if (player.isInPose(EntityPose.FALL_FLYING)) {
            matrices.push();
            matrices.translate(0.0, 0.0, 0.1F);

            // 根据玩家朝向和速度计算旋转角度
            Vec3d vec3d = player.getRotationVec(tickDelta);
            Vec3d vec3d2 = player.getVelocity();
            double d = vec3d2.horizontalLengthSquared();
            double e = vec3d.horizontalLengthSquared();

            if (d > 0.0 && e > 0.0) {
                double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;

                // 正常情况的旋转
                if (!isFeral) {
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(Math.signum(m) * Math.acos(l)) * 180.0F / (float)Math.PI));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                } else {
                    // Feral形态的特殊旋转
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(Math.signum(m) * Math.acos(l)) * 180.0F / (float)Math.PI));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.0F)); // 不向下倾斜
                    // 你可以在这里添加任何额外的旋转
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F)); // 使翅膀向上
                }
            }

            matrices.pop();
        }
    }
}

