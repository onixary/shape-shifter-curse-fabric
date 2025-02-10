package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.player_form_render.IPlayerEntityMixins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public class ItemRendererMixin <T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> {
    @Inject(method="renderItem", at=@At(value = "INVOKE",target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.BEFORE))
    void renderItemMixin(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof ClientPlayerEntity cPE && entity instanceof IPlayerEntityMixins iPE) {
            // TODO: possibly make this use a loop to find the max offset and cache it?
            var mA = iPE.originalFur$getCurrentModels();
            if (mA.size() == 0){return;}
            var m = mA.get(0);
            if (m == null) {
                return;
            }
            Vec3d o = Vec3d.ZERO;
            switch (arm) {
                case LEFT -> o = m.getLeftOffset();
                case RIGHT -> o = m.getRightOffset();
            }
            matrices.translate(o.x, o.y, o.z);
        }

    }
}