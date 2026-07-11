package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.common.collect.Maps;
import dev.tr7zw.firstperson.FirstPersonModelCore;
import mod.azure.azurelib.cache.object.GeoBone;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.RotationAxis;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.util.FeralRenderUtils;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.joml.Matrix4f;

import java.util.Map;

public class LongNeckRenderUtils {
    private static final boolean IS_FIRST_PERSON_MOD_LOADED = FabricLoader.getInstance().isModLoaded("firstperson");
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
    private static BipedEntityModel<AbstractClientPlayerEntity> outerArmorModelWide;
    private static BipedEntityModel<AbstractClientPlayerEntity> outerArmorModelSlim;
    private static BipedEntityModel<AbstractClientPlayerEntity> innerArmorModelWide;
    private static BipedEntityModel<AbstractClientPlayerEntity> innerArmorModelSlim;

    public static boolean hasLongNeck(AbstractClientPlayerEntity player) {
        return findLongNeckModel(player) != null;
    }

    public static FormModel findLongNeckModel(AbstractClientPlayerEntity player) {
        for (FormRenderer formRenderer : FormRenderUtils.getPlayerAllFormRenderer(player)) {
            if (formRenderer == null) {
                continue;
            }
            FormModel formModel = (FormModel) formRenderer.getGeoModel();
            if (formModel != null && formModel.hasNeckIk()) {
                return formModel;
            }
        }
        return null;
    }

    public static boolean isFirstPersonModelActiveForSelf(AbstractClientPlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        return IS_FIRST_PERSON_MOD_LOADED
                && client.options.getPerspective().isFirstPerson()
                && player == client.player
                && FirstPersonModelCore.instance.isEnabled();
    }

    public static void applyHeadBoneTransform(MatrixStack matrices, FormModel formModel) {
        GeoBone head = formModel.getNeckIkHeadBone();
        if (head == null) {
            return;
        }
        matrices.translate(0.5F, 0.51F, 0.5F);
        matrices.multiplyPositionMatrix(new Matrix4f(head.getModelSpaceMatrix()));
    }

    public static void renderLongNeckAttachments(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AbstractClientPlayerEntity player,
            FormModel formModel,
            float tickDelta
    ) {
        if (!formModel.hasNeckIk() || isFirstPersonModelActiveForSelf(player)) {
            return;
        }
        renderMouthItem(matrices, vertexConsumers, light, player, formModel, tickDelta);
        renderHeadEquipment(matrices, vertexConsumers, light, player, formModel);
    }

    private static void renderMouthItem(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AbstractClientPlayerEntity player,
            FormModel formModel,
            float tickDelta
    ) {
        IForm curForm = FormTextureUtils.getPlayerForm_Render(player);
        if (curForm.getBodyType() != PlayerFormBodyType.FERAL) {
            return;
        }

        ItemStack mainHandStack = player.getMainHandStack();
        if (mainHandStack.isEmpty() || FeralRenderUtils.isFeralMouthItemBlackListed(mainHandStack)) {
            return;
        }
        boolean isBlocking = player.isUsingItem() && player.getActiveItem().getUseAction() == UseAction.BLOCK;
        boolean isBlockingWithMainHandShield = isBlocking
                && player.getActiveHand() == Hand.MAIN_HAND
                && mainHandStack.getItem() instanceof ShieldItem;
        if (isBlockingWithMainHandShield) {
            return;
        }

        HeldItemRenderer heldItemRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getHeldItemRenderer();
        matrices.push();
        applyHeadBoneTransform(matrices, formModel);
        matrices.translate(0.06F, 0.085F, -0.35D);
        matrices.scale(1.25F, 1.25F, 1.25F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        matrices.translate(1.0 / 16.0F, -2.0 / 16.0F, 1.0 / 16.0F);
        heldItemRenderer.renderItem(player, mainHandStack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light);
        matrices.pop();
    }

    private static void renderHeadEquipment(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AbstractClientPlayerEntity player,
            FormModel formModel
    ) {
        ItemStack headStack = player.getEquippedStack(EquipmentSlot.HEAD);
        if (headStack.isEmpty()) {
            return;
        }
        if (headStack.getItem() instanceof ArmorItem armorItem && armorItem.getSlotType() == EquipmentSlot.HEAD) {
            renderArmorHead(matrices, vertexConsumers, light, player, formModel, headStack, armorItem);
            return;
        }

        HeldItemRenderer heldItemRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getHeldItemRenderer();
        matrices.push();
        applyHeadBoneTransform(matrices, formModel);
        HeadFeatureRenderer.translate(matrices, false);
        heldItemRenderer.renderItem(player, headStack, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }

    private static void renderArmorHead(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AbstractClientPlayerEntity player,
            FormModel formModel,
            ItemStack stack,
            ArmorItem armorItem
    ) {
        BipedEntityModel<AbstractClientPlayerEntity> model = getArmorModel(player, false);
        model.setVisible(false);
        model.head.visible = true;
        model.hat.visible = true;
        model.head.resetTransform();
        model.hat.resetTransform();

        matrices.push();
        applyHeadBoneTransform(matrices, formModel);
        boolean secondTextureLayer = false;
        if (armorItem instanceof DyeableArmorItem dyeableArmorItem) {
            int color = dyeableArmorItem.getColor(stack);
            renderArmorParts(matrices, vertexConsumers, light, armorItem, model, secondTextureLayer,
                    (float)(color >> 16 & 0xFF) / 255.0F,
                    (float)(color >> 8 & 0xFF) / 255.0F,
                    (float)(color & 0xFF) / 255.0F,
                    null);
            renderArmorParts(matrices, vertexConsumers, light, armorItem, model, secondTextureLayer,
                    1.0F, 1.0F, 1.0F, "overlay");
        } else {
            renderArmorParts(matrices, vertexConsumers, light, armorItem, model, secondTextureLayer,
                    1.0F, 1.0F, 1.0F, null);
        }
        if (stack.hasGlint()) {
            model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getArmorEntityGlint()), light,
                    OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        matrices.pop();
    }

    private static BipedEntityModel<AbstractClientPlayerEntity> getArmorModel(AbstractClientPlayerEntity player, boolean inner) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean slim = player.getModel().equals("slim");
        if (inner) {
            if (slim) {
                if (innerArmorModelSlim == null) {
                    innerArmorModelSlim = new BipedEntityModel<>(client.getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_SLIM_INNER_ARMOR));
                }
                return innerArmorModelSlim;
            }
            if (innerArmorModelWide == null) {
                innerArmorModelWide = new BipedEntityModel<>(client.getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_INNER_ARMOR));
            }
            return innerArmorModelWide;
        }
        if (slim) {
            if (outerArmorModelSlim == null) {
                outerArmorModelSlim = new BipedEntityModel<>(client.getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR));
            }
            return outerArmorModelSlim;
        }
        if (outerArmorModelWide == null) {
            outerArmorModelWide = new BipedEntityModel<>(client.getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_OUTER_ARMOR));
        }
        return outerArmorModelWide;
    }

    private static void renderArmorParts(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            ArmorItem item,
            BipedEntityModel<AbstractClientPlayerEntity> model,
            boolean secondTextureLayer,
            float red,
            float green,
            float blue,
            String overlay
    ) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(getArmorTexture(item, secondTextureLayer, overlay)));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
    }

    private static Identifier getArmorTexture(ArmorItem item, boolean secondLayer, String overlay) {
        String texture = "textures/models/armor/" + item.getMaterial().getName()
                + "_layer_" + (secondLayer ? 2 : 1)
                + (overlay == null ? "" : "_" + overlay) + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(texture, Identifier::new);
    }
}
