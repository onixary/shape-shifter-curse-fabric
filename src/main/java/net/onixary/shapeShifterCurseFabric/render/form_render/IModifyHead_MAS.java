package net.onixary.shapeShifterCurseFabric.render.form_render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Quaternionf;

public interface IModifyHead_MAS {
    void modifyHeadMatrix(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntity player, PlayerEntityRenderer renderer, FormModel formModel);

    static void reverseMatrixFromVanilla(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntity player, PlayerEntityRenderer renderer, FormModel formModel) {
        PlayerEntityModel<AbstractClientPlayerEntity> model = renderer.getModel();
        ModelPart head = model.getHead();
        matrices.scale(1 / head.xScale, 1 / head.yScale, 1 / head.zScale);
        matrices.multiply(new Quaternionf().rotationZYX(-head.roll, -head.yaw, -head.pitch));
        matrices.translate(-head.pivotX / 16.0f, -head.pivotY / 16.0f, -head.pivotZ / 16.0f);
    }
}
