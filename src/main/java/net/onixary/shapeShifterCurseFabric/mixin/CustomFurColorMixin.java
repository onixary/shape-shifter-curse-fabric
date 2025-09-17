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
// 将 value 修改为你的 GeoRenderer 实现类，例如 FurRenderer.class
// 同时，由于 GeoRenderer 是一个接口，你的 Mixin 目标也应该是一个具体的类
@Mixin(value = GeoRenderer.class, remap = false)
public abstract class CustomFurColorMixin <T extends GeoAnimatable> implements GeoRenderer<T> {

    @ModifyVariable(
            method = "renderRecursively",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private RenderLayer modifyRenderLayer(RenderLayer originalLayer, MatrixStack poseStack, T animatable) {
        // 检查当前渲染的实体是否是我们想要修改的目标
        if (animatable instanceof OriginFurAnimatable fur) {

            var texture = this.getTextureLocation(animatable);
            return FurGradientRenderLayer.furGradientRemap.getRenderLayer(originalLayer);
        }

        // 如果不是我们的目标实体，则返回原始的 RenderLayer，不做任何修改
        return originalLayer;
    }
}
