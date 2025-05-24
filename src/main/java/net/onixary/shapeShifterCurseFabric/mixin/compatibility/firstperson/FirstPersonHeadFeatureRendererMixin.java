package net.onixary.shapeShifterCurseFabric.mixin.compatibility.firstperson;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        value = {HeadFeatureRenderer.class},
        priority = 90
)
public class FirstPersonHeadFeatureRendererMixin {
    public FirstPersonHeadFeatureRendererMixin() {}

    @Inject(
            method = {"render"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo info)
    {
        if (FirstPersonModelCore.instance.isRenderingPlayer()) {
            return;
        }
    }
}
