package net.onixary.shapeShifterCurseFabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CapeFeatureRenderer.class)
public class CapeFeatureRendererMixin {

    private AbstractClientPlayerEntity currentPlayer;

    @Inject(method = "render*", at = @At("HEAD"))
    private void capturePlayer(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                               int i, AbstractClientPlayerEntity player, float f, float g, float h,
                               float j, float k, float l, CallbackInfo ci) {
        this.currentPlayer = player;
    }

    @ModifyArg(method = "render*",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"),
            index = 1)
    private float modifyTranslateY(float y) {
        if (currentPlayer != null) {
            Vec3d idlePos = getCapeIdleLoc(currentPlayer);
            return (float) idlePos.y;
        }
        return y;
    }

    @ModifyArg(method = "render*",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"),
            index = 2)
    private float modifyTranslateZ(float z) {
        if (currentPlayer != null) {
            Vec3d idlePos = getCapeIdleLoc(currentPlayer);
            return (float) idlePos.z;
        }
        return z;
    }

    @Inject(method = "render*",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V",
                    ordinal = 0))
    private void addCapeUpwardRotation(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                                       int i, AbstractClientPlayerEntity player, float f, float g, float h,
                                       float j, float k, float l, CallbackInfo ci) {
        float baseRotation = getCapeBaseRotateAngle(player);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(baseRotation));
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;",
                    ordinal = 0),
            index = 0)
    private float modifyXRotationAngle(float angle) {
        if (currentPlayer != null) {
            PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(currentPlayer).getCurrentForm();
            boolean isFeral = RegFormConfig.getConfig(curForm).getBodyType() == PlayerFormBodyType.FERAL;

            if (isFeral || curForm == PlayerForms.BAT_3) {
                // 从角度中提取 q 的部分并钳制
                // angle = 6.0F + r / 2.0F + q
                // 我们需要重新计算角度以限制 q 的部分
                float baseAngle = 6.0F; // 基础角度
                float qPortion = angle - baseAngle; // 提取包含 q 的部分

                // 钳制 q 相关的部分（这里需要根据实际情况调整）
                float maxQ = 35.0f;
                float minQ = -10.0f;

                if (qPortion > maxQ) {
                    return baseAngle + maxQ;
                } else if (qPortion < minQ) {
                    return baseAngle + minQ;
                }
            }
        }
        return angle;
    }

    // helper func
    @Unique
    private Vec3d getCapeIdleLoc(AbstractClientPlayerEntity player) {
        PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
        boolean isFeral = RegFormConfig.getConfig(curForm).getBodyType() == PlayerFormBodyType.FERAL;

        if (isFeral) {
            return new Vec3d(0.0f, -0.2f, 0.3f);
        } else {
            // 特殊形态处理
            if(curForm == PlayerForms.BAT_3 && player.isOnGround()){
                return new Vec3d(0.0f, 0.7f, 0.2f);
            }
            else{
                return new Vec3d(0.0, 0.0, 0.125);
            }
        }
    }

    // 获取披风的基础旋转角度
    @Unique
    private float getCapeBaseRotateAngle(AbstractClientPlayerEntity player) {
        PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
        boolean isFeral = RegFormConfig.getConfig(curForm).getBodyType() == PlayerFormBodyType.FERAL;
        if (isFeral) {
            return 90.0f;
        } else {
            // 特殊形态处理
            if(curForm == PlayerForms.BAT_3){
                return 100.0f;
            }
            else{
                return 0.0f;
            }
        }
    }
}
