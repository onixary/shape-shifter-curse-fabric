package net.onixary.shapeShifterCurseFabric.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import dev.kosmx.playerAnim.core.util.Vec3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form_render.IPlayerEntityMixins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemFeatureRenderer.class)
public abstract class AdjustItemHoldMixin <T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> {
    @Shadow protected abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.AFTER))
    void renderMixin(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ShapeShifterCurseFabric.LOGGER.info("Item render mixin is working");
        if (livingEntity instanceof ClientPlayerEntity player) {
            PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
            boolean isFeral = RegFormConfig.getConfig(curForm).getBodyType() == PlayerFormBodyType.FERAL;
            ShapeShifterCurseFabric.LOGGER.info("Is Feral Form : " + isFeral);
            if(!isFeral){
                return;
            }
            else{
                ShapeShifterCurseFabric.LOGGER.info("Feral form render item");
                matrixStack.push();
                var eR = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
                var head = eR.getModel().head;

                matrixStack.translate(head.pivotX / 16.0F, (head.pivotY) / 16.0F, head.pivotZ / 16.0F);
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(0.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k));
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(l));
                matrixStack.translate(0.06F, 0.27F, -0.5D);

                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));

                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
                matrixStack.translate(1.0 / 16.0F, -2.0 / 16.0F, 1.0 / 16.0F);

                ItemStack itemstack = player.getMainHandStack();
                renderItem(livingEntity, itemstack, ModelTransformationMode.GROUND, Arm.LEFT, matrixStack, vertexConsumerProvider, i);
                matrixStack.pop();
            }
        }
    }
}
