package net.onixary.shapeShifterCurseFabric.features;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ExtraItemFeatureRenderer <T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    //private final HeldItemRenderer heldItemRenderer;
    private final CustomFeralItemRenderer customFeralItemRenderer;

    public ExtraItemFeatureRenderer(FeatureRendererContext<T, M> context, EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        super(context);
        this.customFeralItemRenderer = new CustomFeralItemRenderer(MinecraftClient.getInstance(), entityRenderDispatcher, itemRenderer);
    }

    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            T livingEntity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {

        if (livingEntity instanceof ClientPlayerEntity player) {
            if (true) {
                matrices.push();

                //var eR = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
                //var head = eR.getModel().head;

                // FirstPerson Mod会直接将head.pivot移动到某个非常远的位置来“隐藏”头部，所以需要直接定义好一个固定位置
                Vec3d playerHeadPivot = new Vec3d(0.0F, -3F, 0.0F);
                matrices.translate(playerHeadPivot.x / 16.0F, playerHeadPivot.y / 16.0F, playerHeadPivot.z / 16.0F);

                matrices.multiply(RotationAxis.POSITIVE_Z.rotation(0.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(headPitch));

                float pitch = MathHelper.lerp(tickDelta, player.prevPitch, player.getPitch());
                float equipProgress = 1.0F - MathHelper.lerp(tickDelta, customFeralItemRenderer.prevEquipProgressMainHand, customFeralItemRenderer.equipProgressMainHand);

                // 调用第一人称物品渲染逻辑
                customFeralItemRenderer.renderFirstPersonItem(
                        player,
                        tickDelta,
                        pitch,
                        Hand.MAIN_HAND,
                        player.getHandSwingProgress(tickDelta),
                        player.getMainHandStack(),
                        equipProgress,
                        matrices,
                        vertexConsumers,
                        light
                );

                matrices.pop();
            }
        }
    }
}
