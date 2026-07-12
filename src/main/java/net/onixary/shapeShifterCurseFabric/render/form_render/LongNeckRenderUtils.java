package net.onixary.shapeShifterCurseFabric.render.form_render;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import mod.azure.azurelib.cache.object.GeoBone;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.RotationAxis;
import net.onixary.shapeShifterCurseFabric.mixin.accessor.ArmorFeatureRendererAccessor;
import net.onixary.shapeShifterCurseFabric.mixin.accessor.LivingEntityRendererAccessor;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.util.FeralRenderUtils;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LongNeckRenderUtils {
    private static final boolean IS_FIRST_PERSON_MOD_LOADED = FabricLoader.getInstance().isModLoaded("firstperson");
    private static final ThreadLocal<Boolean> DELEGATING_LONG_NECK_HEAD_ARMOR = ThreadLocal.withInitial(() -> false);

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

    public static boolean isRenderingDelegatedLongNeckHeadArmor() {
        return DELEGATING_LONG_NECK_HEAD_ARMOR.get();
    }

    public static void applyHeadBoneTransform(MatrixStack matrices, FormModel formModel) {
        GeoBone head = formModel.getNeckIkHeadBone();
        if (head == null) {
            return;
        }

        matrices.translate(0.5F, 0.51F, 0.5F);
        List<GeoBone> boneChain = new ArrayList<>();
        for (GeoBone bone = head; bone != null; bone = bone.getParent()) {
            boneChain.add(bone);
        }
        Collections.reverse(boneChain);

        for (int i = 0; i < boneChain.size(); i++) {
            GeoBone bone = boneChain.get(i);
            applyBoneLocalTransform(matrices, bone);
            if (i < boneChain.size() - 1) {
                translateAwayFromPivot(matrices, bone);
            }
        }
    }

    private static void applyBoneLocalTransform(MatrixStack matrices, GeoBone bone) {
        matrices.translate(-bone.getPosX() / 16.0F, bone.getPosY() / 16.0F, bone.getPosZ() / 16.0F);
        matrices.translate(bone.getPivotX() / 16.0F, bone.getPivotY() / 16.0F, bone.getPivotZ() / 16.0F);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(bone.getRotZ()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(bone.getRotY()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(bone.getRotX()));
        matrices.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    private static void translateAwayFromPivot(MatrixStack matrices, GeoBone bone) {
        matrices.translate(-bone.getPivotX() / 16.0F, -bone.getPivotY() / 16.0F, -bone.getPivotZ() / 16.0F);
    }

    private static void applyVanillaHeadAttachmentAxes(MatrixStack matrices) {
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
    }

    private static void applyVanillaHeadModelAxes(MatrixStack matrices) {
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
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
        applyVanillaHeadAttachmentAxes(matrices);
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
            renderArmorHead(matrices, vertexConsumers, light, player, formModel);
            return;
        }

        HeldItemRenderer heldItemRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getHeldItemRenderer();
        matrices.push();
        applyHeadBoneTransform(matrices, formModel);
        applyVanillaHeadAttachmentAxes(matrices);
        applyVanillaHeadModelAxes(matrices);
        HeadFeatureRenderer.translate(matrices, false);
        heldItemRenderer.renderItem(player, headStack, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }

    private static void renderArmorHead(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AbstractClientPlayerEntity player,
            FormModel formModel
    ) {
        ArmorFeatureRenderer<LivingEntity, BipedEntityModel<LivingEntity>, BipedEntityModel<LivingEntity>> armorFeatureRenderer = getArmorFeatureRenderer(player);
        if (armorFeatureRenderer == null) {
            return;
        }

        matrices.push();
        applyHeadBoneTransform(matrices, formModel);
        applyVanillaHeadAttachmentAxes(matrices);
        applyVanillaHeadModelAxes(matrices);
        DELEGATING_LONG_NECK_HEAD_ARMOR.set(true);
        try {
            ArmorFeatureRendererAccessor<LivingEntity, BipedEntityModel<LivingEntity>> accessor =
                    (ArmorFeatureRendererAccessor<LivingEntity, BipedEntityModel<LivingEntity>>) armorFeatureRenderer;
            BipedEntityModel<LivingEntity> model = accessor.shape_shifter_curse$invokeGetModel(EquipmentSlot.HEAD);
            accessor.shape_shifter_curse$invokeRenderArmor(matrices, vertexConsumers, player, EquipmentSlot.HEAD, light, model);
        } finally {
            DELEGATING_LONG_NECK_HEAD_ARMOR.set(false);
            matrices.pop();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ArmorFeatureRenderer<LivingEntity, BipedEntityModel<LivingEntity>, BipedEntityModel<LivingEntity>> getArmorFeatureRenderer(AbstractClientPlayerEntity player) {
        EntityRenderer<? super AbstractClientPlayerEntity> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
        if (!(renderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer)) {
            return null;
        }
        List<FeatureRenderer<LivingEntity, EntityModel<LivingEntity>>> features =
                ((LivingEntityRendererAccessor<LivingEntity, EntityModel<LivingEntity>>) livingEntityRenderer).shape_shifter_curse$getFeatures();
        for (FeatureRenderer<LivingEntity, EntityModel<LivingEntity>> feature : features) {
            if (feature instanceof ArmorFeatureRenderer<?, ?, ?> armorFeatureRenderer) {
                return (ArmorFeatureRenderer) armorFeatureRenderer;
            }
        }
        return null;
    }
}
