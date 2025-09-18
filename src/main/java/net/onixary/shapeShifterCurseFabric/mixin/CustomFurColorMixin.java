package net.onixary.shapeShifterCurseFabric.mixin;

import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.renderer.GeoRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.onixary.shapeShifterCurseFabric.player_form_render.OriginFurAnimatable;
import net.onixary.shapeShifterCurseFabric.render.render_layer.FurGradientRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
// 基于RenderLayer与自定义Core Shader的方法在光影环境下无法使用
// 这是不可接受的。弃用这一方法
@Mixin(value = GeoRenderer.class, remap = false)
public abstract class CustomFurColorMixin <T extends GeoAnimatable> implements GeoRenderer<T> {

    @ModifyVariable(
            method = "renderRecursively",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private RenderLayer modifyRenderLayer(RenderLayer originalLayer, MatrixStack poseStack, T animatable) {
        if (animatable instanceof OriginFurAnimatable fur) {

            var texture = this.getTextureLocation(animatable);
            return FurGradientRenderLayer.furGradientRemap.getRenderLayer(originalLayer);
        }
        return originalLayer;
    }
}
