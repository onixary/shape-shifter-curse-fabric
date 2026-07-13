package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.render.form_render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// NECK FEATURES FILE

@Mixin(ArmorFeatureRenderer.class)
public class LongNeckArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private void shape_shifter_curse$skipVanillaHeadArmorForLongNeck(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        if (LongNeckRenderUtils.isRenderingDelegatedLongNeckHeadArmor()) {
            return;
        }
        if (armorSlot == EquipmentSlot.HEAD
                && entity instanceof AbstractClientPlayerEntity player
                && LongNeckRenderUtils.hasLongNeck(player)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "renderArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;copyBipedStateTo(Lnet/minecraft/client/render/entity/model/BipedEntityModel;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void shape_shifter_curse$resetHeadModelForDelegatedLongNeckArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        if (armorSlot != EquipmentSlot.HEAD || !LongNeckRenderUtils.isRenderingDelegatedLongNeckHeadArmor()) {
            return;
        }
        model.child = false;
        model.head.resetTransform();
        model.hat.resetTransform();
    }
}
