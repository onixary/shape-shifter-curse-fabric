package net.onixary.shapeShifterCurseFabric.mixin;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.renderer.GeoObjectRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.render.tech.CocoonModel;
import net.onixary.shapeShifterCurseFabric.render.tech.EmptyAnimatable;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    private static CocoonModel cocoonModel = new CocoonModel();
    private static EmptyAnimatable cocoonAnimatable = new EmptyAnimatable();
    private static GeoObjectRenderer<EmptyAnimatable> cocoonRenderer = new GeoObjectRenderer<EmptyAnimatable>(cocoonModel);

    @Inject(method = "render", at = @At("HEAD"))
    public void renderOverlay(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        matrices.push();
        // matrices.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
        // matrices.translate(0, -1.51f, 0);
        matrices.translate(-0.5, -0.5, -0.5);
        cocoonRenderer.render(matrices, cocoonAnimatable, vertexConsumers, RenderLayer.getEntityTranslucent(cocoonModel.getTextureResource(cocoonAnimatable)), null, light);
        matrices.pop();
    }
}
