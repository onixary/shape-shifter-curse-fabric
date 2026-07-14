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
    @Unique private static FormRenderUtils.BoneBipedState headBoneState;

    @Inject(
            method = "renderArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;copyBipedStateTo(Lnet/minecraft/client/render/entity/model/BipedEntityModel;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void shape_shifter_curse$modifyHeadStateForMAS(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        if (!(entity instanceof AbstractClientPlayerEntity player)) {
            return;
        }
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
        FormRenderer renderer = FormRenderUtils.searchFirstRenderer(player, formRenderer -> {
            FormModel formModel = formRenderer.realModel;
            if (formModel == null) {
                return false;
            }
            return formModel.AnimationSystem instanceof IModifyHead_MAS;
        });
        if (renderer != null) {
            headBoneState = new FormRenderUtils.BoneBipedState(model.getHead());
            ((IModifyHead_MAS)renderer.realModel.AnimationSystem).modifyHeadPart(player, model, renderer.realModel);
        }
    }

    @Inject(method = "renderArmor", at = @At(value = "RETURN"))
    private void shape_shifter_curse$restoreHeadStateForMAS(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        if (headBoneState != null) {
            headBoneState.restore();
            headBoneState = null;
        }
    }


}
