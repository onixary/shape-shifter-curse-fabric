package net.onixary.shapeShifterCurseFabric.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.feralItemEulerX;
import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.feralItemPosOffset;

public class MouthItemFeature<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
    private final HeldItemRenderer heldItemRenderer;

    public MouthItemFeature(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        if (!(livingEntity instanceof ClientPlayerEntity player) || MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
            return;
        }

        PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
        if (RegFormConfig.getConfig(curForm).getBodyType() != PlayerFormBodyType.FERAL) {
            return;
        }

        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();

        boolean isBlocking = player.isUsingItem() && player.getActiveItem().getUseAction() == UseAction.BLOCK;
        Hand activeHand = player.getActiveHand();

        // 检查是否正在用主手持盾格挡
        boolean isBlockingWithMainHandShield = isBlocking && activeHand == Hand.MAIN_HAND && mainHandStack.getItem() instanceof ShieldItem;

        // --- 主手物品渲染 ---
        if (!mainHandStack.isEmpty()) {
            if (isBlockingWithMainHandShield) {
                // 如果用主手盾牌格挡，则渲染在背后
                renderShieldOnBack(matrixStack, vertexConsumerProvider, i, livingEntity, mainHandStack, true);
            } else {
                // 否则，渲染在嘴里
                renderItemInMouth(matrixStack, vertexConsumerProvider, i, livingEntity, mainHandStack, k, l);
            }
        }

        // --- 副手物品渲染 ---
        if (!offHandStack.isEmpty()) {
            // 检查是否正在用副手持盾格挡
            boolean isBlockingWithOffHandShield = isBlocking && activeHand == Hand.OFF_HAND && offHandStack.getItem() instanceof ShieldItem;

            if (isBlockingWithOffHandShield) {
                // 如果用副手盾牌格挡，则渲染在背后
                renderShieldOnBack(matrixStack, vertexConsumerProvider, i, livingEntity, offHandStack, false);
            } else {
                // 否则，渲染在背后的默认位置
                renderDefaultItemOnBack(matrixStack, vertexConsumerProvider, i, livingEntity, offHandStack);
            }
        }
    }

    private void renderItemInMouth(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, ItemStack itemStack, float k, float l) {
        matrixStack.push();
        var eR = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(livingEntity);
        var head = eR.getModel().head;
        matrixStack.translate(head.pivotX / 16.0F, head.pivotY / 16.0F, head.pivotZ / 16.0F);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(l));
        matrixStack.translate(0.06F, 0.085F, -0.35D);
        matrixStack.scale(1.25F, 1.25F, 1.25F);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        matrixStack.translate(1.0 / 16.0F, -2.0 / 16.0F, 1.0 / 16.0F);
        heldItemRenderer.renderItem(livingEntity, itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    private void renderShieldOnBack(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, ItemStack itemStack, boolean isLeftHand) {
        matrixStack.push();
        var eR = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(livingEntity);
        var body = eR.getModel().body;
        body.rotate(matrixStack);
        // --- 格挡时盾牌的调整 ---
        matrixStack.translate(-0.7f, 1.2f, 0.8f);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f - 20.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-75.0f));
        matrixStack.scale(1.2f, 1.2f, 1.2f);
        // --- 调整结束 ---
        heldItemRenderer.renderItem(livingEntity, itemStack, ModelTransformationMode.FIXED, isLeftHand, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    private void renderDefaultItemOnBack(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, ItemStack itemStack) {
        matrixStack.push();
        var eR = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(livingEntity);
        var body = eR.getModel().body;
        body.rotate(matrixStack);
        matrixStack.translate(0.0F, 0.5F, 0.25F);
        matrixStack.scale(1.5F, 1.5F, 1.5F);
        heldItemRenderer.renderItem(livingEntity, itemStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}
