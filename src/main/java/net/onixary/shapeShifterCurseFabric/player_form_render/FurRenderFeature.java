package net.onixary.shapeShifterCurseFabric.player_form_render;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.util.Vec3f;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.render.render_layer.FurColorGradientRenderLayer;
import net.onixary.shapeShifterCurseFabric.render.render_layer.FurGradientRenderLayer;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Unique;

public class FurRenderFeature <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public FurRenderFeature(FeatureRendererContext<T, M> context) {
        super(context);
    }
    // add dynamic tail effect:
    static float tailDragAmount = 0.0F;
    static float tailDragAmountO;
    static float currentTailDragAmount = 0.0F;
    // X轴向的drag
    static float tailDragAmountVertical = 0.0F;
    static float tailDragAmountVerticalO;
    static float currentTailDragAmountVertical = 0.0F;

    //static float wingFlapAmount = 0.0f;

    @Unique
    private int getOverlayMixin(LivingEntity entity, float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
    }

    private void updateTailDragAmount(float targetValue, float lerpSpeed) {
        currentTailDragAmount = MathHelper.lerp(lerpSpeed, currentTailDragAmount, targetValue);
    }

    private void updateVerticalTailDrag(float targetValue, float lerpSpeed) {
        currentTailDragAmountVertical = MathHelper.lerp(lerpSpeed, currentTailDragAmountVertical, targetValue);
    }
    /*
    private void updateWingFlapParams(T entity) {
        // 获取速度绝对值（忽略方向）
        float horizontalSpeed = (float) entity.getVelocity().horizontalLength();
        //ShapeShifterCurseFabric.LOGGER.info("horizontal speed: " + horizontalSpeed);
        float horizontalSpeedClamped = MathHelper.clamp(horizontalSpeed * 8.5f, 0.0f, 1.0f);
        // 根据速度调节翅膀振动
        wingFlapAmount = MathHelper.clamp(horizontalSpeedClamped, 0.2f, 1.0f);

        // 应用平滑
        wingFlapAmount = MathHelper.lerp(0.2f, wingFlapAmount, wingFlapAmount);
    }*/

    public static class ModelTransformation {
        public Vec3d position, rotation;
        public ModelTransformation(Vec3f pos, Vec3f rot) {
            this.position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            this.rotation = new Vec3d(rot.getX(), rot.getY(), rot.getZ());
        }
        public ModelTransformation(IAnimation anim, String bone_name) {
            Vec3f pos = anim.get3DTransform(bone_name, TransformType.POSITION, MinecraftClient.getInstance().getTickDelta(), new Vec3f(0,0,0));
            Vec3f rot = anim.get3DTransform(bone_name, TransformType.ROTATION, MinecraftClient.getInstance().getTickDelta(), new Vec3f(0,0,0));
            this.position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            this.rotation = new Vec3d(rot.getX(), rot.getY(), rot.getZ());
        }
        public ModelTransformation invert(boolean x, boolean y, boolean z) {
            this.rotation = this.rotation.multiply(x ? -1 : 1, y ? -1 : 1, z ? -1 : 1);
            return this;
        }
        public ModelTransformation invert(boolean i) {
            this.rotation = this.rotation.multiply(i ? -1 : 1);
            return this;
        }
    }
    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
            if (abstractClientPlayerEntity.isInvisible() || abstractClientPlayerEntity.isSpectator()) {return;}
            var iPEM = (IPlayerEntityMixins) abstractClientPlayerEntity;
            for (var fur : iPEM.originalFur$getCurrentFurs()) {
                if (fur == null) {
                    return;
                }
                Origin o = fur.currentAssociatedOrigin;
                if (o == null) {
                    return;
                }
                var eR = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(abstractClientPlayerEntity);
                var eRA = (IPlayerEntityMixins) eR;
                var acc = (ModelRootAccessor) eR.getModel();
                var a = fur.getAnimatable();
                OriginFurModel m = (OriginFurModel) fur.getGeoModel();
                Origin finalO = o;
                m.getAnimationProcessor().getRegisteredBones().forEach(coreGeoBone -> {
                    if (((IGeoBone) coreGeoBone).originfurs$isHiddenByDefault()) {
                        return;
                    }
                    m.preprocess(finalO, eR, eRA, acc, abstractClientPlayerEntity);
                });
                fur.setPlayer(abstractClientPlayerEntity);
                matrixStack.push();
                matrixStack.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
                matrixStack.translate(0, -1.51f, 0);
                //
                m.resetBone("bipedHead");
                m.resetBone("bipedBody");
                m.resetBone("bipedLeftArm");
                m.resetBone("bipedRightArm");
                m.resetBone("bipedLeftLeg");
                m.resetBone("bipedRightLeg");

                m.setRotationForBone("bipedHead", ((IMojModelPart) (Object) eR.getModel().head).originfurs$getRotation());
                m.translatePositionForBone("bipedHead", ((IMojModelPart) (Object) eR.getModel().head).originfurs$getPosition());
                m.translatePositionForBone("bipedBody", ((IMojModelPart) (Object) eR.getModel().body).originfurs$getPosition());
                m.translatePositionForBone("bipedLeftArm", ((IMojModelPart) (Object) eR.getModel().leftArm).originfurs$getPosition());
                m.translatePositionForBone("bipedRightArm", ((IMojModelPart) (Object) eR.getModel().rightArm).originfurs$getPosition());
                m.translatePositionForBone("bipedLeftLeg", ((IMojModelPart) (Object) eR.getModel().rightLeg).originfurs$getPosition());
                m.translatePositionForBone("bipedRightLeg", ((IMojModelPart) (Object) eR.getModel().leftLeg).originfurs$getPosition());
                m.translatePositionForBone("bipedLeftArm", new Vec3d(5, 2, 0));
                m.translatePositionForBone("bipedRightArm", new Vec3d(-5, 2, 0));
                m.translatePositionForBone("bipedLeftLeg", new Vec3d(-2, 12, 0));
                m.translatePositionForBone("bipedRightLeg", new Vec3d(2, 12, 0));
                matrixStack.translate(-0.5, -0.5, -0.5);
                m.setRotationForBone("bipedBody", ((IMojModelPart) (Object) eR.getModel().body).originfurs$getRotation());

                float targetDrag = MathHelper.lerp(tickDelta, tailDragAmountO, tailDragAmount);
                // adjust tail drag back speed
                updateTailDragAmount(targetDrag, 0.04F);
                m.setRotationForTailBones(limbAngle, limbDistance, entity.age, currentTailDragAmount, tailDragAmountVertical);
                m.setRotationForHeadTailBones(headYaw, entity.age, currentTailDragAmount, tailDragAmountVertical);
                m.setRotationForWingBones(limbAngle, limbDistance, entity.age, tailDragAmountVertical);
                tailDragAmountO = tailDragAmount;


                tailDragAmount *= 0.75F;
                // adjust tail drag curvature scale
                tailDragAmount -= (float) (Math.toRadians((entity.bodyYaw - entity.prevBodyYaw)) * 0.55F);
                // clamp tail drag curvature
                tailDragAmount = MathHelper.clamp(tailDragAmount, -1.6F, 1.6F);

                // 获取实体垂直速度（转换为角度变化量）
                float verticalSpeed = (float) entity.getVelocity().y;
                float targetVerticalDrag = MathHelper.clamp(verticalSpeed * 1.5f, -1.6f, 1.6f);

                // 更新垂直拖拽量
                float targetDragVertical = MathHelper.lerp(tickDelta, tailDragAmountVerticalO, tailDragAmountVertical);
                updateVerticalTailDrag(targetDragVertical, 0.04F);

                // 衰减和限制垂直拖拽量
                tailDragAmountVertical *= 0.8F;
                tailDragAmountVertical += targetVerticalDrag * 0.15F;
                tailDragAmountVertical = MathHelper.clamp(tailDragAmountVertical, -1.6f, 1.6f);
                tailDragAmountVerticalO = tailDragAmountVertical;

                //updateWingFlapParams(entity);

                m.invertRotForPart("bipedBody", false, true, false);
                m.setRotationForBone("bipedLeftArm", ((IMojModelPart) (Object) eR.getModel().leftArm).originfurs$getRotation());
                m.setRotationForBone("bipedRightArm", ((IMojModelPart) (Object) eR.getModel().rightArm).originfurs$getRotation());
                m.setRotationForBone("bipedLeftLeg", ((IMojModelPart) (Object) eR.getModel().rightLeg).originfurs$getRotation());
                m.setRotationForBone("bipedRightLeg", ((IMojModelPart) (Object) eR.getModel().leftLeg).originfurs$getRotation());
                m.invertRotForPart("bipedHead", false, true, true);
                m.invertRotForPart("bipedRightArm", false, true, true);
                m.invertRotForPart("bipedLeftArm", false, true, true);
                m.invertRotForPart("bipedRightLeg", false, true, true);
                m.invertRotForPart("bipedLeftLeg", false, true, true);

                // 用Core Shader渲染的尝试，但这玩意儿不兼容Iris，:(
                // Core shader fur render implementation, but not capable with iris, :(
                //RenderLayer myLayer = FurGradientRenderLayer.furGradientRemap.getRenderLayer(RenderLayer.getEntityTranslucent(m.getTextureResource(a)));
                //RenderLayer testFurLayer = FurColorGradientRenderLayer.getFurLayer(m.getTextureResource(a), new Vector4f(1.0f, 1.0f ,1.0f, 1.0f), new Vector4f(0.8f, 0.8f, 0.8f, 1.0f));
                fur.render(matrixStack, a, vertexConsumerProvider, RenderLayer.getEntityTranslucent(m.getTextureResource(a)), null, light);

                // 用Core Shader渲染的尝试，但这玩意儿不兼容Iris，:(
                // Core shader fur render implementation, but not capable with iris, :(
                //RenderLayer myLayer2 = FurGradientRenderLayer.furGradientRemap.getRenderLayer(RenderLayer.getEntityTranslucentEmissive(m.getFullbrightTextureResource(a)));
                //RenderLayer testFurLayer2 = FurColorGradientRenderLayer.getFurLayer(m.getFullbrightTextureResource(a), new Vector4f(1.0f, 1.0f ,1.0f, 1.0f), new Vector4f(0.8f, 0.8f, 0.8f, 1.0f));
                fur.render(matrixStack, a, vertexConsumerProvider, RenderLayer.getEntityTranslucentEmissive(m.getTextureResource(a)), null, light);
                matrixStack.pop();
            }


        }
    }

}
