package net.onixary.shapeShifterCurseFabric.render.form_render;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.player_form_render.OriginFurModel;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public class FormRenderFeature <T extends PlayerEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public FormRenderFeature(FeatureRendererContext<T, M> context) {
        super(context);
    }

    private static final boolean IS_FIRST_PERSON_MOD_LOADED = FabricLoader.getInstance().isModLoaded("firstperson");

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
            boolean hasOutline = MinecraftClient.getInstance().hasOutline(abstractClientPlayerEntity);
            if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && IS_FIRST_PERSON_MOD_LOADED) {
                if (abstractClientPlayerEntity == MinecraftClient.getInstance().player) {
                    hasOutline = false;
                }
            }
            if (abstractClientPlayerEntity.isInvisible() || abstractClientPlayerEntity.isSpectator()) { return; }
            List<FormRenderer> formRendererList = FormRenderUtils.getPlayerAllFormRenderer(abstractClientPlayerEntity);
            for (FormRenderer formRenderer : formRendererList) {
                if (formRenderer == null) {
                    continue;
                }
                PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(abstractClientPlayerEntity);
                PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = playerEntityRenderer.getModel();
                FormModel formModel = (FormModel) formRenderer.getGeoModel();
                FormAnimatable formAnimatable = formRenderer.getAnimatable();
                formRenderer.setPlayer(abstractClientPlayerEntity, playerEntityModel.thinArms);
                matrices.push();
                matrices.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
                matrices.translate(0, -1.51f, 0);
                matrices.translate(-0.5, -0.5, -0.5);
                formModel.AnimationSystem.beforeRender(formRenderer, formModel, playerEntityRenderer, abstractClientPlayerEntity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
                formModel.AnimationSystem.processAnimation(formRenderer, formModel, playerEntityRenderer, abstractClientPlayerEntity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
                // 渲染部分
                formRenderer.render(matrices, formAnimatable, vertexConsumers, RenderLayer.getEntityTranslucent(formModel.getTextureResource(formAnimatable)), null, light);
                formRenderer.render(matrices, formAnimatable, vertexConsumers, RenderLayer.getEntityTranslucentEmissive(formModel.getFullbrightTextureResource(formAnimatable)), null, Integer.MAX_VALUE - 1);
                if (hasOutline) {
                    formRenderer.render(matrices, formAnimatable, vertexConsumers, RenderLayer.getOutline(formModel.getTextureResource(formAnimatable)), vertexConsumers.getBuffer(RenderLayer.getOutline(formModel.getTextureResource(formAnimatable))), light);
                }
                matrices.pop();
                formModel.AnimationSystem.afterRender(formRenderer, formModel, playerEntityRenderer, abstractClientPlayerEntity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            }
        }
    }

    // TODO 复制PlayerEntityRendererMixin里的逻辑
    public static void renderModel(PlayerEntityRenderer playerEntityRenderer, PlayerEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {

    }

    // TODO 复制OverrideSkinFirstPersonMixin里的逻辑
    public static void renderFirstPersonModelA(PlayerEntityRenderer playerEntityRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {

    }

    public static void renderFirstPersonModelB(PlayerEntityRenderer playerEntityRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {

    }
}
