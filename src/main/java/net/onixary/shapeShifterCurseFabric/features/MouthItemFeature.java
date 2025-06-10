package net.onixary.shapeShifterCurseFabric.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.feralItemEulerX;
import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.feralItemPosOffset;

public class MouthItemFeature<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final HeldItemRenderer heldItemRenderer;

    public MouthItemFeature(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        //ShapeShifterCurseFabric.LOGGER.info("Item render mixin is working");
        if (livingEntity instanceof ClientPlayerEntity player && !MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
            PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
            boolean isFeral = RegFormConfig.getConfig(curForm).getBodyType() == PlayerFormBodyType.FERAL;
            //ShapeShifterCurseFabric.LOGGER.info("Is Feral Form : " + isFeral);
            if(!isFeral){
                return;
            }
            else{
                //ShapeShifterCurseFabric.LOGGER.info("Feral form render item");
                matrixStack.push();
                var eR = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
                var head = eR.getModel().head;

                //Vec3d playerHeadPivot = new Vec3d(0.0F, -3F, 0.0F);
                matrixStack.translate(head.pivotX / 16.0F, head.pivotY / 16.0F, head.pivotZ / 16.0F);
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(0.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k));
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(l));
                matrixStack.translate(0.06F, 0.085F, -0.35D);
                matrixStack.scale(1.25F,1.25F,1.25F);

                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));

                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
                matrixStack.translate(1.0 / 16.0F, -2.0 / 16.0F, 1.0 / 16.0F);

                ItemStack itemstack = player.getMainHandStack();
                heldItemRenderer.renderItem(livingEntity, itemstack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i);
                matrixStack.pop();

                // 副装备栏放在背部
                matrixStack.push();
                var eR2 = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
                var body = eR.getModel().body;
                body.rotate(matrixStack);

                matrixStack.translate(body.pivotX / 16.0F, body.pivotY / 16.0F, body.pivotZ / 16.0F);
                matrixStack.translate(0.0F, 0.5F, 0.25F);
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.0F));
                matrixStack.scale(1.5F, 1.5F, 1.5F);

                ItemStack offHandStack = player.getOffHandStack();
                heldItemRenderer.renderItem(livingEntity, offHandStack, ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i);
                matrixStack.pop();

            }
        }
    }


}
