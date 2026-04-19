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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryUtils;

import java.util.Map;
import java.util.Optional;

public class SpiderTPEHR extends ThirdPersonExtraHandItemRender.TPEHR_Render {
    public static final String GROUP_STRING = "hand";
    public static final String INV_STRING = "extra_hand";

    @Override
    public void render(HeldItemRenderer heldItemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        // XuHaoNan: 此功能暂时 trinket 独占 影响不大 不想写切换逻辑了
        if (!AccessoryUtils.LOADED_Trinkets) {
            return;
        }
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
        ModelPart body = eR.getModel().body;
        body.rotate(matrices);
        if(stack.getItem() == Items.SHIELD){
            // 适用于spider_3额外手臂盾牌的transform，需要将其转向正面
            //matrices.translate(-0.1F, 0.6F, -0.5F);
            matrices.translate(0.1F, 0.2F, -0.2F);
            matrices.scale(0.65F, 0.65F, 0.65F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(30.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
            matrices.translate(1.0 / 16.0F, -2.0 / 16.0F, 1.0 / 16.0F);
        }
        else{
            // 适用于spider_3额外手臂通常道具的transform
            matrices.translate(-0.1F, 0.6F, -0.5F);
            matrices.scale(0.8F, 0.8F, 0.8F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(15.0F));
            matrices.translate(1.0 / 16.0F, -2.0 / 16.0F, 1.0 / 16.0F);
        }
        heldItemRenderer.renderItem(player, stack, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, false, matrices, vertexConsumers, light);

    }
}
