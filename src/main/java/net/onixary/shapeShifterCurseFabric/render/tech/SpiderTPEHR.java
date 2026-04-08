package net.onixary.shapeShifterCurseFabric.render.tech;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

import java.util.Map;
import java.util.Optional;

public class SpiderTPEHR extends ThirdPersonExtraHandItemRender.TPEHR_Render {
    public static final String GROUP_STRING = "hand";
    public static final String INV_STRING = "extra_hand";

    @Override
    public void render(HeldItemRenderer heldItemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        if (component.isEmpty()) {
            return;
        }
        Map<String, TrinketInventory> groupInv = component.get().getInventory().get(GROUP_STRING);
        if (groupInv == null) {
            return;
        }
        TrinketInventory inv = groupInv.get(INV_STRING);
        if (inv == null) {
            return;
        }
        ItemStack stack = inv.getStack(0);
        if (stack.isEmpty()) {
            return;
        }
        PlayerEntityRenderer eR = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
        ModelPart head = eR.getModel().head;
        matrices.translate(head.pivotX / 16.0F, head.pivotY / 16.0F, head.pivotZ / 16.0F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(headPitch));
        matrices.translate(0.06F, 0.085F, -0.35D);
        matrices.scale(1.25F, 1.25F, 1.25F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        matrices.translate(1.0 / 16.0F, -2.0 / 16.0F, 1.0 / 16.0F);
        heldItemRenderer.renderItem(player, stack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light);
    }
}
