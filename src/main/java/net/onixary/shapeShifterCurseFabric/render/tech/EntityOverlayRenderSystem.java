package net.onixary.shapeShifterCurseFabric.render.tech;

import mod.azure.azurelib.renderer.GeoObjectRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects;

import java.util.ArrayList;

public class EntityOverlayRenderSystem {
    public static EmptyAnimatable EmptyAnimatable = new EmptyAnimatable();

    public static abstract class OverlayData {
        public abstract boolean canRender(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);
        public abstract void render(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);
    }

    public static ArrayList<OverlayData> overlayDataList = new ArrayList<OverlayData>();

    static {
        overlayDataList.add(new OverlayData() {
            private static CocoonModel cocoonModel = new CocoonModel();
            private static GeoObjectRenderer<EmptyAnimatable> cocoonRenderer = new GeoObjectRenderer<EmptyAnimatable>(cocoonModel);

            @Override
            public boolean canRender(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
                return entity instanceof LivingEntity livingEntity && livingEntity.getStatusEffect(RegOtherStatusEffects.ENTANGLED_FULL_EFFECT) != null;
            }

            @Override
            public void render(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
                matrices.push();
                matrices.translate(-0.5, -0.5, -0.5);
                cocoonRenderer.render(matrices, EmptyAnimatable, vertexConsumers, RenderLayer.getEntityTranslucent(cocoonModel.getTextureResource(EmptyAnimatable)), null, light);
                matrices.pop();
            }
        });
    }

    public static void render(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        // 保底一下 防止炸的太厉害
        matrices.push();
        for (OverlayData overlayData : overlayDataList) {
            if (overlayData.canRender(entity, yaw, tickDelta, matrices, vertexConsumers, light)) {
                overlayData.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
            }
        }
        matrices.pop();
    }
}
