package net.onixary.shapeShifterCurseFabric.render.form_render;

import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.cache.object.GeoBone;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;

public class FormRenderFeature <T extends PlayerEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public FormRenderFeature(FeatureRendererContext<T, M> context) {
        super(context);
    }

    private static final boolean IS_FIRST_PERSON_MOD_LOADED = FabricLoader.getInstance().isModLoaded("firstperson");
    private static final boolean BetterCombatInstalled = FabricLoader.getInstance().isModLoaded("bettercombat");
    private static final boolean IRISInstalled = FabricLoader.getInstance().isModLoaded("iris");

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

    // 处理 BonePart 隐藏
    public static void rM_PartA(PlayerEntityRenderer playerEntityRenderer, AbstractClientPlayerEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (player.isSpectator()) {
            PlayerEntityModel<?> model = (PlayerEntityModel<?>) playerEntityRenderer.getModel();
            model.hat.hidden = false;
            model.head.hidden = false;
            model.body.hidden = false;
            model.jacket.hidden = false;
            model.leftArm.hidden = false;
            model.leftSleeve.hidden = false;
            model.rightArm.hidden = false;
            model.rightSleeve.hidden = false;
            model.leftLeg.hidden = false;
            model.leftPants.hidden = false;
            model.rightLeg.hidden = false;
            model.rightPants.hidden = false;
            return;
        }
        List<FormRenderer> formRendererList = FormRenderUtils.getPlayerAllFormRenderer(player);
        PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = playerEntityRenderer.getModel();
        boolean hatHidden = !player.isPartVisible(PlayerModelPart.HAT);
        boolean headHidden = false;
        boolean bodyHidden = false;
        boolean jacketHidden = !player.isPartVisible(PlayerModelPart.JACKET);
        boolean leftArmHidden = false;
        boolean leftSleeveHidden = !player.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
        boolean rightArmHidden = false;
        boolean rightSleeveHidden = !player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
        boolean leftLegHidden = false;
        boolean leftPantsHidden = !player.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
        boolean rightLegHidden = false;
        boolean rightPantsHidden = !player.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
        for (FormRenderer formRenderer : formRendererList) {
            FormModel formModel = (FormModel) formRenderer.getGeoModel();
            hatHidden |= formModel.Hidden_Hat;
            headHidden |= formModel.Hidden_Head;
            bodyHidden |= formModel.Hidden_Body;
            jacketHidden |= formModel.Hidden_Jacket;
            leftArmHidden |= formModel.Hidden_LeftArm;
            leftSleeveHidden |= formModel.Hidden_LeftSleeve;
            rightArmHidden |= formModel.Hidden_RightArm;
            rightSleeveHidden |= formModel.Hidden_RightSleeve;
            leftLegHidden |= formModel.Hidden_LeftLeg;
            leftPantsHidden |= formModel.Hidden_LeftPants;
            rightLegHidden |= formModel.Hidden_RightLeg;
            rightPantsHidden |= formModel.Hidden_RightPants;
        }
        playerEntityModel.hat.visible = !hatHidden;
        playerEntityModel.head.visible = !headHidden;
        playerEntityModel.body.visible = !bodyHidden;
        playerEntityModel.jacket.visible = !jacketHidden;
        playerEntityModel.leftArm.visible = !leftArmHidden;
        playerEntityModel.leftSleeve.visible = !leftSleeveHidden;
        playerEntityModel.rightArm.visible = !rightArmHidden;
        playerEntityModel.rightSleeve.visible = !rightSleeveHidden;
        playerEntityModel.leftLeg.visible = !leftLegHidden;
        playerEntityModel.leftPants.visible = !leftPantsHidden;
        playerEntityModel.rightLeg.visible = !rightLegHidden;
        playerEntityModel.rightPants.visible = !rightPantsHidden;

        boolean IsClientNowPlayedPlayer = player instanceof ClientPlayerEntity;
        boolean IsFirstPersonView = MinecraftClient.getInstance().options.getPerspective().isFirstPerson();

        if (BetterCombatInstalled && IsFirstPersonView && IsClientNowPlayedPlayer && ClientUtils.ShouldEnableBetterCombatFix()) {
            playerEntityModel.hat.visible = false;
            playerEntityModel.head.visible = false;
        }
    }

    public static void rM_PartB(PlayerEntityRenderer playerEntityRenderer, AbstractClientPlayerEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        int p = OverlayTexture.packUv(OverlayTexture.getU(playerEntityRenderer.getAnimationCounter(player, g)), OverlayTexture.getV(player.hurtTime > 0 || player.deathTime > 0));
        if (player.isSpectator()) {
            return;
        }
        List<FormRenderer> formRendererList = FormRenderUtils.getPlayerAllFormRenderer(player);
        for (FormRenderer formRenderer : formRendererList) {
            PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = playerEntityRenderer.getModel();
            FormModel formModel = (FormModel) formRenderer.getGeoModel();
            Identifier overlayTexture = formModel.getOverlayTextureResource(playerEntityModel.thinArms);
            Identifier emissiveTexture = formModel.getEmissiveTextureResource(playerEntityModel.thinArms);
            boolean bl = playerEntityRenderer.isVisible(player);
            boolean bl2 = !bl && !player.isInvisibleTo(MinecraftClient.getInstance().player);
            if (overlayTexture != null) {
                RenderLayer l = null;
                if (FormRenderUtils.isRenderingInWorld && IRISInstalled) {
                    l = RenderLayer.getEntityCutoutNoCullZOffset(overlayTexture);
                } else {
                    l = RenderLayer.getEntityCutout(overlayTexture);
                }
                playerEntityModel.render(matrixStack, vertexConsumerProvider.getBuffer(l), i, p, 1, 1, 1, bl2 ? 0.15F : 1.0F);
            }
            if (emissiveTexture != null) {
                RenderLayer l = RenderLayer.getEntityTranslucentEmissive(emissiveTexture);
                playerEntityModel.render(matrixStack, vertexConsumerProvider.getBuffer(l), i, p, 1, 1, 1, bl2 ? 0.15F : 1.0F);
            }
            playerEntityModel.hat.hidden = false;
            playerEntityModel.head.hidden = false;
            playerEntityModel.body.hidden = false;
            playerEntityModel.jacket.hidden = false;
            playerEntityModel.leftArm.hidden = false;
            playerEntityModel.leftSleeve.hidden = false;
            playerEntityModel.rightArm.hidden = false;
            playerEntityModel.rightSleeve.hidden = false;
            playerEntityModel.leftLeg.hidden = false;
            playerEntityModel.leftPants.hidden = false;
            playerEntityModel.rightLeg.hidden = false;
            playerEntityModel.rightPants.hidden = false;
        }
    }

    private static void renderGeoBone(FormRenderer formRenderer, GeoBone geoBone, MatrixStack matrixStack, FormAnimatable formAnimatable, VertexConsumerProvider vertexConsumerProvider, RenderLayer renderLayer, VertexConsumer vertexConsumer, int packedLight) {
        renderGeoBone(formRenderer, geoBone, matrixStack, formAnimatable, vertexConsumerProvider, renderLayer, vertexConsumer, packedLight, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void renderGeoBone(FormRenderer formRenderer, GeoBone geoBone, MatrixStack matrixStack, FormAnimatable formAnimatable, VertexConsumerProvider vertexConsumerProvider, RenderLayer renderLayer, VertexConsumer vertexConsumer, int packedLight, float R, float G, float B, float A) {
        FormModel formModel = (FormModel) formRenderer.getGeoModel();
        BakedGeoModel bakedGeoModel = formModel.getBakedModel(formModel.getModelResource(formAnimatable));
        float TickDelta = MinecraftClient.getInstance().getTickDelta();
        int packedOverlay = formRenderer.getPackedOverlay(formAnimatable, 0.0F, MinecraftClient.getInstance().getTickDelta());
        matrixStack.translate(-0.5, -0.51, -0.5); // 在 GeoObjectRenderer.preRender 中会 poseStack.translate(0.5, 0.51, 0.5) 因此需要手动调整
        formRenderer.preRender(matrixStack, formAnimatable, bakedGeoModel, vertexConsumerProvider, vertexConsumerProvider.getBuffer(renderLayer), false, TickDelta, packedLight, packedOverlay, R, G, B, A);
        if (formRenderer.firePreRenderEvent(matrixStack, bakedGeoModel, vertexConsumerProvider, TickDelta, packedLight)) {
            formRenderer.preApplyRenderLayers(matrixStack, formAnimatable, bakedGeoModel, renderLayer, vertexConsumerProvider, vertexConsumer, (float)packedLight, packedLight, packedOverlay);
            matrixStack.push();
            formRenderer.updateAnimatedTextureFrame(formAnimatable);
            formRenderer.renderRecursively(matrixStack, formAnimatable, geoBone, renderLayer, vertexConsumerProvider, vertexConsumer, false, TickDelta, packedLight, packedOverlay, R, G, B, A);
            matrixStack.pop();
            formRenderer.applyRenderLayers(matrixStack, formAnimatable, bakedGeoModel, renderLayer, vertexConsumerProvider, vertexConsumer, TickDelta, packedLight, packedOverlay);
            formRenderer.postRender(matrixStack, formAnimatable, bakedGeoModel, vertexConsumerProvider, vertexConsumer, false, TickDelta, packedLight, packedOverlay, R, G, B, A);
            formRenderer.firePostRenderEvent(matrixStack, bakedGeoModel, vertexConsumerProvider, TickDelta, packedLight);
        }
    }

    public static void rFPM_PartA(PlayerEntityRenderer playerEntityRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        List<FormRenderer> formRendererList = FormRenderUtils.getPlayerAllFormRenderer(player);
        boolean IsRenderRight = arm.equals(playerEntityRenderer.getModel().rightArm);
        boolean ArmHidden = false;
        boolean SleeveHidden = IsRenderRight ? !player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE) : !player.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
        for (FormRenderer formRenderer : formRendererList) {
            FormModel formModel = (FormModel) formRenderer.getGeoModel();
            // 设置手臂组件是否显示
            if (IsRenderRight) {
                ArmHidden |= formModel.Hidden_RightArm;
                SleeveHidden |= formModel.Hidden_RightSleeve;
            } else {
                ArmHidden |= formModel.Hidden_LeftArm;
                SleeveHidden |= formModel.Hidden_LeftSleeve;
            }
        }
        arm.visible = !ArmHidden;
        sleeve.visible = !SleeveHidden;
    }

    public static void rFPM_PartB(PlayerEntityRenderer playerEntityRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        boolean IsRenderRight = arm.equals(playerEntityRenderer.getModel().rightArm);
        List<FormRenderer> formRendererList = FormRenderUtils.getPlayerAllFormRenderer(player);
        for (FormRenderer formRenderer : formRendererList) {
            @Nullable GeoBone geoBone = null;
            if (formRenderer == null) {return;}
            PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = playerEntityRenderer.getModel();
            FormModel formModel = (FormModel) formRenderer.getGeoModel();
            FormAnimatable formAnimatable = formRenderer.getAnimatable();
            formRenderer.setPlayer(player, playerEntityModel.thinArms);
            matrices.push();
            matrices.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
            matrices.translate(0, -1.51f, 0);
            geoBone = formModel.AnimationSystem.beforeRenderFirstPerson(geoBone, formRenderer, formModel, playerEntityRenderer, player, arm, sleeve);
            geoBone = formModel.AnimationSystem.processAnimationFirstPerson(geoBone, formRenderer, formModel, playerEntityRenderer, player, arm, sleeve);
            if (geoBone == null) {
                formModel.AnimationSystem.afterRenderFirstPerson(geoBone, formRenderer, formModel, playerEntityRenderer, player, arm, sleeve);
                matrices.pop();
                continue;
            }
            RenderLayer renderLayerNormal = RenderLayer.getEntityTranslucent(formModel.getTextureResource(formAnimatable));
            renderGeoBone(formRenderer, geoBone, matrices, formAnimatable, vertexConsumers, renderLayerNormal, vertexConsumers.getBuffer(renderLayerNormal), light);
            RenderLayer renderLayerFullBright = RenderLayer.getEntityTranslucent(formModel.getFullbrightTextureResource(formAnimatable));
            renderGeoBone(formRenderer, geoBone, matrices, formAnimatable, vertexConsumers, renderLayerFullBright, vertexConsumers.getBuffer(renderLayerFullBright), Integer.MAX_VALUE - 1);
            matrices.pop();

            // Render Overlay 藏得够深的 要不是发现悦灵手臂无法显示我都不会发现
            // 从 PlayerEntityRendererMixin.renderOverlayTexture 提取的代码并进行修改
            Identifier OverlayTextureID = formModel.getOverlayTextureResource(playerEntityModel.thinArms);
            if (OverlayTextureID != null) {
                // 玩家看自己绝对是非隐身
                // boolean bl = this.isVisible(player);
                // boolean bl2 = !bl && !player.isInvisibleTo(MinecraftClient.getInstance().player);
                RenderLayer OverlayLayer = null;
                if (FormRenderUtils.isRenderingInWorld && IRISInstalled) {
                    OverlayLayer = RenderLayer.getEntityCutoutNoCullZOffset(OverlayTextureID);
                } else {
                    OverlayLayer = RenderLayer.getEntityCutout(OverlayTextureID);
                }
                int OverlayInt = OverlayTexture.packUv(OverlayTexture.getU(playerEntityRenderer.getAnimationCounter(player, MinecraftClient.getInstance().getTickDelta())), OverlayTexture.getV(player.hurtTime > 0 || player.deathTime > 0));
                arm.render(matrices, vertexConsumers.getBuffer(OverlayLayer), light, OverlayInt, 1.0f, 1.0f, 1.0f, 1.0F);
            }
            formModel.AnimationSystem.afterRenderFirstPerson(geoBone, formRenderer, formModel, playerEntityRenderer, player, arm, sleeve);
        }
    }
}
