package net.onixary.shapeShifterCurseFabric.minion.mobs;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;

public class WolfMinionRenderer extends WolfEntityRenderer {
    public WolfMinionRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(WolfEntity wolfEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(0.8f,0.8f,0.8f);
        super.render(wolfEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}

