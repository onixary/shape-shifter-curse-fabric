package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.render.form_render.LongNeckRenderUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeadFeatureRenderer.class)
public class LongNeckHeadFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void shape_shifter_curse$skipVanillaHeadFeatureForLongNeck(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T livingEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (!(livingEntity instanceof AbstractClientPlayerEntity player) || !LongNeckRenderUtils.hasLongNeck(player)) {
            return;
        }
        ItemStack headStack = livingEntity.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD);
        if (!headStack.isEmpty() && !(headStack.getItem() instanceof ArmorItem)) {
            ci.cancel();
        }
    }
}
