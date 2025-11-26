package net.onixary.shapeShifterCurseFabric.mixin;


import io.github.apace100.apoli.component.PowerHolderComponent;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.cache.object.GeoBone;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.NoRenderArmPower;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form_render.*;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

// issue: OverrideSkinFirstPersonMixin会与某些其他mod不兼容，需要寻找原因所在
@Mixin(PlayerEntityRenderer.class)
public abstract class OverrideSkinFirstPersonMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    protected OverrideSkinFirstPersonMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowSize) {
        super(ctx, model, shadowSize);
    }

    // 自定义皮肤路径
    @Unique
    private static final Identifier CUSTOM_SKIN = new Identifier(ShapeShifterCurseFabric.MOD_ID, "textures/entity/base_player/ssc_base_skin.png");

    @Inject(method = "renderArm", at = @At("HEAD"), cancellable = true)
    private void shape_shifter_curse$RenderArm_HEAD(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)) {return;}  // 仅当玩家激活Mod后才进行修改
        if (!ShapeShifterCurseFabric.clientConfig.ignoreNoRenderArmPower && PowerHolderComponent.hasPower(player, NoRenderArmPower.class)) {  // 不渲染手臂情况
            ci.cancel();
        }
    }

    @Inject(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;setModelPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)V", shift = At.Shift.AFTER))
    private void shape_shifter_curse$RenderArm_setModelPose_AFTER(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        // 渲染变身模型-根据模型设置修改手臂组件渲染
        if (RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)) {return;}  // 仅当玩家激活Mod后才进行修改
        if (!ShapeShifterCurseFabric.clientConfig.enableFormModelOnVanillaFirstPersonRender) {return;}  // 仅当启用自定义第一人称渲染时才进行修改
        for (OriginalFurClient.OriginFur fur : ((IPlayerEntityMixins) player).originalFur$getCurrentFurs()) {
            OriginFurModel OFModel = (OriginFurModel) fur.getGeoModel();
            boolean IsRenderRight = arm.equals(this.getModel().rightArm);
            // 设置手臂组件是否显示
            if (IsRenderRight) {
                arm.visible = !OFModel.hiddenParts.contains(OriginFurModel.VMP.rightArm);
                sleeve.visible = !OFModel.hiddenParts.contains(OriginFurModel.VMP.rightSleeve);
            }
            else {
                arm.visible = !OFModel.hiddenParts.contains(OriginFurModel.VMP.leftArm);
                sleeve.visible = !OFModel.hiddenParts.contains(OriginFurModel.VMP.leftSleeve);
            }
        }
    }

    @Inject(method = "renderArm", at = @At("RETURN"))
    private void shape_shifter_curse$RenderArm_RETURN(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        // 渲染变身模型
        if (RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)) {return;}  // 仅当玩家激活Mod后才进行修改
        if (!ShapeShifterCurseFabric.clientConfig.enableFormModelOnVanillaFirstPersonRender) {return;}  // 仅当启用自定义第一人称渲染时才进行修改
        boolean IsRenderRight = arm.equals(this.getModel().rightArm);
        String GeoBoneName = IsRenderRight ? "bipedRightArm" : "bipedLeftArm";
        for (OriginalFurClient.OriginFur fur : ((IPlayerEntityMixins) player).originalFur$getCurrentFurs()) {
            if (fur == null) {return;}
            Origin origin = fur.currentAssociatedOrigin;
            if (origin == null) {return;}
            PlayerEntityRenderer EntityRender = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
            OriginFurModel OFModel = (OriginFurModel) fur.getGeoModel();
            OriginFurAnimatable OFAnimatable = fur.getAnimatable();
            Optional<GeoBone> OptionalGeoBone = OFModel.getBone(GeoBoneName);
            if (OptionalGeoBone.isEmpty()) {
                // 有时AzureLib 未能及时注册 GeoBone 因此需要手动注册
                if (OFModel.getAnimationProcessor().getRegisteredBones().isEmpty()) {
                    ShapeShifterCurseFabric.LOGGER.info("GeoBone 未注册, 尝试重新注册模型");
                    BakedGeoModel bakedGeoModel = OFModel.getBakedModel(OFModel.getModelResource(OFAnimatable));
                    OFModel.getAnimationProcessor().setActiveModel(bakedGeoModel);
                }
                return;
            }
            var eRA = (IPlayerEntityMixins) EntityRender;
            var acc = (ModelRootAccessor) EntityRender.getModel();
            OFModel.preprocess(origin, EntityRender, eRA, acc, player);
            GeoBone geoBone = OptionalGeoBone.get();
            fur.setPlayer(player);
            OFModel.setPlayer(player);
            matrices.push();
            matrices.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
            matrices.translate(0, -1.51f, 0);
            OFModel.resetBone(GeoBoneName);
            OFModel.translatePositionForBone(GeoBoneName, ((IMojModelPart) (Object) arm).originfurs$getPosition());
            OFModel.translatePositionForBone(GeoBoneName, new Vec3d(5 * (IsRenderRight ? -1.0 : 1.0), 2, 0));
            OFModel.setRotationForBone(GeoBoneName, ((IMojModelPart) (Object) arm).originfurs$getRotation());
            OFModel.invertRotForPart(GeoBoneName, false, true, true);
            RenderLayer renderLayerNormal = RenderLayer.getEntityTranslucent(OFModel.getTextureResource(OFAnimatable));
            this.RenderOFModelBone(fur, geoBone, matrices, OFAnimatable, vertexConsumers, renderLayerNormal, vertexConsumers.getBuffer(renderLayerNormal), light);
            // fur.renderBone(GeoBoneName, matrices, vertexConsumers, renderLayerNormal, null, light);
            RenderLayer renderLayerFullBright = RenderLayer.getEntityTranslucent(OFModel.getFullbrightTextureResource(OFAnimatable));
            this.RenderOFModelBone(fur, geoBone, matrices, OFAnimatable, vertexConsumers, renderLayerFullBright, vertexConsumers.getBuffer(renderLayerFullBright), Integer.MAX_VALUE - 1);
            // fur.renderBone(GeoBoneName, matrices, vertexConsumers, renderLayerFullBright, null, Integer.MAX_VALUE - 1);
            matrices.pop();
            // Render Overlay 藏得够深的 要不是发现悦灵手臂无法显示我都不会发现
            // 从 PlayerEntityRendererMixin.renderOverlayTexture 提取的代码并进行修改
            Identifier OverlayTextureID = OFModel.getOverlayTexture(acc.originalFur$isSlim());
            if (OverlayTextureID != null) {
                // 玩家看自己绝对是非隐身
                // boolean bl = this.isVisible(player);
                // boolean bl2 = !bl && !player.isInvisibleTo(MinecraftClient.getInstance().player);
                RenderLayer OverlayLayer = null;
                if (OriginalFurClient.isRenderingInWorld && FabricLoader.getInstance().isModLoaded("iris")) {
                    OverlayLayer = RenderLayer.getEntityCutoutNoCullZOffset(OverlayTextureID);
                } else {
                    OverlayLayer = RenderLayer.getEntityCutout(OverlayTextureID);
                }
                int OverlayInt = OverlayTexture.packUv(OverlayTexture.getU(this.getAnimationCounter(player, MinecraftClient.getInstance().getTickDelta())), OverlayTexture.getV(player.hurtTime > 0 || player.deathTime > 0));
                arm.render(matrices, vertexConsumers.getBuffer(OverlayLayer), light, OverlayInt, 1.0f, 1.0f, 1.0f, 1.0F);
            }
        }
    }
    // fur.renderBone 因为没有缓存机制 在大型模型会严重卡顿(每秒渲染FPS次) 因此手动实现渲染逻辑

    // 模拟fur.render 但只渲染特定GeoBone 使用AzureLib默认渲染渲染逻辑
    @Unique
    private void RenderOFModelBone(OriginalFurClient.OriginFur OFRender, GeoBone geoBone, MatrixStack poseStack, OriginFurAnimatable animatable, VertexConsumerProvider bufferSource, RenderLayer renderType, VertexConsumer buffer, int packedLight) {
        this.RenderOFModelBone(OFRender, geoBone, poseStack, animatable, bufferSource, renderType, buffer, packedLight, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Unique
    private void RenderOFModelBone(OriginalFurClient.OriginFur OFRender, GeoBone geoBone, MatrixStack poseStack, OriginFurAnimatable animatable, VertexConsumerProvider bufferSource, RenderLayer renderType, VertexConsumer buffer, int packedLight, float R, float G, float B, float A) {
        OriginFurModel OFModel = (OriginFurModel) OFRender.getGeoModel();
        BakedGeoModel bakedGeoModel = OFModel.getBakedModel(OFModel.getModelResource(animatable));
        float TickDelta = MinecraftClient.getInstance().getTickDelta();
        int packedOverlay = OFRender.getPackedOverlay(animatable, 0.0F, MinecraftClient.getInstance().getTickDelta());
        poseStack.translate(-0.5, -0.51, -0.5); // 在 GeoObjectRenderer.preRender 中会 poseStack.translate(0.5, 0.51, 0.5) 因此需要手动调整
        OFRender.preRender(poseStack, animatable, bakedGeoModel, bufferSource, bufferSource.getBuffer(renderType), false, TickDelta, packedLight, packedOverlay, R, G, B, A);
        if (OFRender.firePreRenderEvent(poseStack, bakedGeoModel, bufferSource, TickDelta, packedLight)) {
            OFRender.preApplyRenderLayers(poseStack, animatable, bakedGeoModel, renderType, bufferSource, buffer, (float)packedLight, packedLight, packedOverlay);
            poseStack.push();
            OFRender.updateAnimatedTextureFrame(animatable);
            OFRender.renderRecursively(poseStack, animatable, geoBone, renderType, bufferSource, buffer, false, TickDelta, packedLight, packedOverlay, R, G, B, A);
            poseStack.pop();
            OFRender.applyRenderLayers(poseStack, animatable, bakedGeoModel, renderType, bufferSource, buffer, TickDelta, packedLight, packedOverlay);
            OFRender.postRender(poseStack, animatable, bakedGeoModel, bufferSource, buffer, false, TickDelta, packedLight, packedOverlay, R, G, B, A);
            OFRender.firePostRenderEvent(poseStack, bakedGeoModel, bufferSource, TickDelta, packedLight);
        }
    }


    @Redirect(method="renderArm", at= @At(value="INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSkinTexture()Lnet/minecraft/util/Identifier;"))
    private Identifier shape_shifter_curse$getSkinTexture(AbstractClientPlayerEntity player) {
        if (!RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE))  // 仅当玩家激活Mod后才进行修改
        {
            if (!RegPlayerSkinComponent.SKIN_SETTINGS.get(player).shouldKeepOriginalSkin()) {
                return CUSTOM_SKIN;
            }
        }
        return player.getSkinTexture();
    }
}
