package net.onixary.shapeShifterCurseFabric.mixin.accessor;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorFeatureRenderer.class)
public interface ArmorFeatureRendererAccessor<T extends LivingEntity, A extends BipedEntityModel<T>> {
    @Invoker("getModel")
    A shape_shifter_curse$invokeGetModel(EquipmentSlot slot);

    @Invoker("renderArmor")
    void shape_shifter_curse$invokeRenderArmor(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            T entity,
            EquipmentSlot armorSlot,
            int light,
            A model
    );
}
