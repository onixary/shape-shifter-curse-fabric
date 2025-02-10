package net.onixary.shapeShifterCurseFabric.mixin;


import net.onixary.shapeShifterCurseFabric.integration.origins.component.PlayerOriginComponent;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayers;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModComponents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form_render.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Pseudo
@Mixin(value = PlayerEntityRenderer.class, priority = 99999)
public class PlayerEntityRendererMixin {

    @Pseudo
    @Mixin(value= ElytraFeatureRenderer.class)
    public static abstract class ElytraRendererMixin <T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M>{

        public ElytraRendererMixin(FeatureRendererContext<T, M> context) { super(context); }

        @Inject(method="render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.AFTER))
        void renderMixin(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci){
            if (livingEntity instanceof IPlayerEntityMixins ipem) {
                ipem.originalFur$getCurrentModels().forEach(originFurModel -> {
                    var v = originFurModel.getSubRenderingOffset("elytra").negate();
                    matrixStack.translate(v.x, v.y, v.z);
                });

            }
        }

    }

    @Pseudo
    @Mixin(value=PlayerEntityModel.class, priority = 99999)
    public static abstract class PlayerEntityModel$RootModel$Mixin implements ModelRootAccessor, IPlayerEntityMixins{
        @Shadow @Final private boolean thinArms;


        @Unique
        ModelPart root;
        @Unique
        float elytraPitch = 0;
        @Unique
        boolean justStartedFlying = false;

        @Override
        public boolean originalFur$justUsedElytra() {
            return justStartedFlying;
        }

        @Override
        public float originalFur$elytraPitch() {
            return elytraPitch;
        }

        @Override
        public void originalFur$setElytraPitch(float f) {
            elytraPitch = f;
        }

        @Override
        public void originalFur$setJustUsedElytra(boolean b) {
            justStartedFlying = b;
        }

        @Inject(method="<init>", at=@At("TAIL"))
        void initMixin(ModelPart root, boolean thinArms, CallbackInfo ci){
            this.root = root;

        }

        @Override
        public ModelPart originalFur$getRoot() {
            return root;
        }
        @Unique
        boolean proc_slim = false;
        @Override
        public boolean originalFur$hasProcessedSlim() {
            return proc_slim;
        }

        @Override
        public void originalFur$setProcessedSlim(boolean state) {
            proc_slim = state;
        }

        @Override
        public boolean originalFur$isSlim() {
            return thinArms;
        }
    }

    @Pseudo
    @Mixin(value = LivingEntityRenderer.class, priority = 99999)
    public static abstract class LivingEntityRendererMixin$HidePlayerModelIfNeeded <T extends LivingEntity, M extends EntityModel<T>> implements IPlayerEntityMixins {
        @Shadow @Final protected List<FeatureRenderer<T, M>> features;

    @Inject(method="<init>", at=@At(value = "TAIL"))
    void initMixin(EntityRendererFactory.Context ctx, EntityModel model, float shadowRadius, CallbackInfo ci) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (model instanceof PlayerEntityModel<?>) {
            //noinspection unchecked,rawtypes
            addFeature(new FurRenderFeature<>((LivingEntityRenderer)(Object)this));
        }
    }
        @Shadow public abstract M getModel();

        @Shadow protected M model;

        @Shadow protected abstract boolean isVisible(T entity);

        @Unique
        private int getOverlayMixin(LivingEntity entity, float whiteOverlayProgress) {
            return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
        }

        @Shadow protected abstract float getAnimationCounter(T entity, float tickDelta);

    @Shadow protected abstract boolean addFeature(FeatureRenderer<T, M> feature);

    @Unique
        boolean isInvisible = false;
        @Override
        public boolean originalFur$isPlayerInvisible() {
            return isInvisible;
        }

        @Inject(method="render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                shift = At.Shift.BEFORE))
        private void renderPreProcessMixin(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
            if (livingEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
                isInvisible = false;
                PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(abstractClientPlayerEntity);
                for (var layer : OriginLayers.getLayers()) {
                    var origin = c.getOrigin(layer);
                    if (origin == null) {return;}
                    Identifier id = origin.getIdentifier();
                    for (var opt : ((IPlayerEntityMixins)abstractClientPlayerEntity).originalFur$getCurrentFurs()) {
                        OriginFurModel m_Model = (OriginFurModel) opt.getGeoModel();

                        m_Model.preRender$mixinOnly(abstractClientPlayerEntity);
                        if (m_Model.isPlayerModelInvisible()) {
                            isInvisible = true;
                            matrixStack.translate(0, 9999, 0);
                        } else {
                            isInvisible = false;
                        }

                        if (!isInvisible) {
                            var p = m_Model.getHiddenParts();
                            var model = (PlayerEntityModel<?>) this.getModel();
                            model.hat.hidden = p.contains(OriginFurModel.VMP.hat);
                            model.head.hidden = p.contains(OriginFurModel.VMP.head);
                            model.body.hidden = p.contains(OriginFurModel.VMP.body);
                            model.jacket.hidden = p.contains(OriginFurModel.VMP.jacket);
                            model.leftArm.hidden = p.contains(OriginFurModel.VMP.leftArm);
                            model.leftSleeve.hidden = p.contains(OriginFurModel.VMP.leftSleeve);
                            model.rightArm.hidden = p.contains(OriginFurModel.VMP.rightArm);
                            model.rightSleeve.hidden = p.contains(OriginFurModel.VMP.rightSleeve);
                            model.leftLeg.hidden = p.contains(OriginFurModel.VMP.leftLeg);
                            model.leftPants.hidden = p.contains(OriginFurModel.VMP.leftPants);
                            model.rightLeg.hidden = p.contains(OriginFurModel.VMP.rightLeg);
                            model.rightPants.hidden = p.contains(OriginFurModel.VMP.rightPants);
                        }
                    }
                }
            }
        }
        @Inject(method="render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                        shift = At.Shift.AFTER))
        private void renderPostProcessMixin(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
            if (isInvisible) {
                matrixStack.translate(0,-9999,0);
            }
        }
        @Inject(method="render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                        shift = At.Shift.AFTER))
        private void renderOverlayTexture(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
            if (!isInvisible && livingEntity instanceof AbstractClientPlayerEntity aCPE) {
                PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(aCPE);
                int p = getOverlayMixin(livingEntity, this.getAnimationCounter(livingEntity, g));
                for (var layer : OriginLayers.getLayers()) {
                    var origin = c.getOrigin(layer);
                    if (origin == null) {
                        return;
                    }
                    Identifier id = origin.getIdentifier();
                    for (var opt : ((IPlayerEntityMixins) aCPE).originalFur$getCurrentFurs()) {
                        var model = (ModelRootAccessor) (PlayerEntityModel<?>) this.getModel();
                        OriginFurModel m_Model = (OriginFurModel) opt.getGeoModel();
                        var overlayTexture = m_Model.getOverlayTexture(model.originalFur$isSlim());
                        var emissiveTexture = m_Model.getEmissiveTexture(model.originalFur$isSlim());
                        boolean bl = this.isVisible(livingEntity);
                        boolean bl2 = !bl && !livingEntity.isInvisibleTo(MinecraftClient.getInstance().player);
                        if (overlayTexture != null) {
                            RenderLayer l = null;
                            if (OriginalFurClient.isRenderingInWorld && FabricLoader.getInstance().isModLoaded("iris")) {
                                l = RenderLayer.getEntityCutoutNoCullZOffset(overlayTexture);
                            } else {
                                l = RenderLayer.getEntityCutout(overlayTexture);
                            }
                            this.model.render(matrixStack, vertexConsumerProvider.getBuffer(l), i, p, 1, 1, 1, bl2 ? 0.15F : 1.0F);
                        }
                        if (emissiveTexture != null) {

                            RenderLayer l = RenderLayer.getEntityTranslucentEmissive(emissiveTexture);
                            this.model.render(matrixStack, vertexConsumerProvider.getBuffer(l), i, p, 1, 1, 1, bl2 ? 0.15F : 1.0F);
                        }
                        var m = (PlayerEntityModel<?>) this.getModel();
                        m.hat.hidden = false;
                        m.head.hidden = false;
                        m.body.hidden = false;
                        m.jacket.hidden = false;
                        m.leftArm.hidden = false;
                        m.leftSleeve.hidden = false;
                        m.rightArm.hidden = false;
                        m.rightSleeve.hidden = false;
                        m.leftLeg.hidden = false;
                        m.leftPants.hidden = false;
                        m.rightLeg.hidden = false;
                        m.rightPants.hidden = false;
                    }
                }
            }
        }
    }
}
