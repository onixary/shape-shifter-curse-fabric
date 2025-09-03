package net.onixary.shapeShifterCurseFabric.render.render_layer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public abstract class FurColorGradientRenderLayer extends RenderLayer {
    // 缓存 RenderLayer 实例以提高性能
    private static final Map<String, RenderLayer> LAYER_CACHE = new HashMap<>();

    private FurColorGradientRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }


    public static RenderLayer getFurLayer(Identifier texture, Vector4f startColor, Vector4f endColor) {
        // 使用所有可变参数生成唯一的缓存键
        String cacheKey = "fur_layer_" + texture + startColor + endColor;

        return LAYER_CACHE.computeIfAbsent(cacheKey, key -> {
            // 创建一个自定义的 Shader 渲染阶段
            // 这个阶段在被激活时，会设置我们的颜色 Uniforms
            RenderPhase.ShaderProgram shaderPhase = new RenderPhase.ShaderProgram(() -> {
                net.minecraft.client.gl.ShaderProgram shader = ShapeShifterCurseFabricClient.getFurGradientShader();
                if (shader != null) {
                    // 在绑定着色器后，立即设置 Uniform 变量
                    //shader.getUniform("StartColor").set(startColor);
                    //shader.getUniform("EndColor").set(endColor);
                }
                return shader;
            });

            // 使用 MultiPhaseParameters 构建 RenderLayer 的所有状态
            RenderLayer.MultiPhaseParameters params = RenderLayer.MultiPhaseParameters.builder()
                    .program(shaderPhase) // 使用我们自定义的着色器阶段
                    .texture(new RenderPhase.Texture(texture, false, false))
                    .transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY)
                    .cull(RenderLayer.DISABLE_CULLING)
                    .lightmap(RenderLayer.ENABLE_LIGHTMAP)
                    .overlay(RenderLayer.ENABLE_OVERLAY_COLOR)
                    .build(true);

            // 使用 RenderLayer.of 创建一个复合渲染层
            // 这是创建自定义 RenderLayer 的标准、兼容方式
            return RenderLayer.of("fur_gradient_layer",
                    VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                    VertexFormat.DrawMode.QUADS,
                    256,
                    true, // hasCrumbling
                    true, // translucent
                    params);
        });
    }
}
