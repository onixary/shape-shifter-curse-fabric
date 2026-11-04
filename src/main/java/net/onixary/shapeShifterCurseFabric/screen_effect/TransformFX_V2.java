package net.onixary.shapeShifterCurseFabric.screen_effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;

import java.io.IOException;
import java.util.List;

public final class TransformFX_V2 implements ClientTickEvents.EndTick {

    public static final TransformFX_V2 INSTANCE = new TransformFX_V2();

    private PostEffectProcessor processor;
    private GlUniform sliderUniform;

    private static int transformEffectDuration = 0;
    private static boolean doEffectIn = false;
    private static boolean doEffectOut = false;

    private int lastWidth = -1;
    private int lastHeight = -1;

    public void registerCallbacks() {
        ClientTickEvents.END_CLIENT_TICK.register(this);
        WorldRenderEvents.LAST.register(ctx -> INSTANCE.render(ctx.tickDelta()));

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return ShapeShifterCurseFabric.identifier("transform_fx_reload");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        INSTANCE.onResourceReload();
                    }
                });
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        ensureProcessor(client);

        if (doEffectIn || doEffectOut) {
            --transformEffectDuration;
            if (transformEffectDuration <= 0) {
                finishEffect();
            }
        }
    }

    private void ensureProcessor(MinecraftClient client) {
        if (processor != null) return;
        if (client.getResourceManager() == null) return;

        try {
            Identifier id = ShapeShifterCurseFabric.identifier("shaders/post/transform_effect_v2.json");
            processor = new PostEffectProcessor(
                    client.getTextureManager(),
                    client.getResourceManager(),
                    client.getFramebuffer(),
                    id
            );
            System.out.println("[TransformFX] processor created");

            List<PostEffectPass> passes = processor.passes;
            for (int i = 0; i < passes.size(); i++) {
                GlUniform u = passes.get(i).getProgram().getUniformByName("Slider");
                System.out.println("[TransformFX] pass[" + i + "] Slider = " + (u != null ? "FOUND" : "null"));
                if (u != null) {
                    sliderUniform = u;
                    break;
                }
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("[TransformFX] processor creation FAILED");
            e.printStackTrace();
            processor = null;
            sliderUniform = null;
        }
    }

    public void render(float tickDelta) {
        if (processor == null || (!doEffectIn && !doEffectOut)) return;

        MinecraftClient client = MinecraftClient.getInstance();

        int w = client.getWindow().getFramebufferWidth();
        int h = client.getWindow().getFramebufferHeight();
        if (w != lastWidth || h != lastHeight) {
            processor.setupDimensions(w, h);
            lastWidth = w;
            lastHeight = h;
        }

        float progress;
        if (doEffectIn) {
            float d = StaticParams.TRANSFORM_FX_DURATION_IN;
            progress = 1.0f - (float) transformEffectDuration / d;
        } else {
            float d = StaticParams.TRANSFORM_FX_DURATION_OUT;
            progress = (float) transformEffectDuration / d;
        }
        progress = Math.max(0f, Math.min(1f, progress));

        if (sliderUniform != null) {
            sliderUniform.set(progress);
        }

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        processor.render(tickDelta);

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private void finishEffect() {
        if (doEffectIn) doEffectIn = false;
        else if (doEffectOut) doEffectOut = false;
        transformEffectDuration = 0;
    }

    public void onResourceReload() {
        if (processor != null) {
            processor.close();
            processor = null;
        }
        sliderUniform = null;
        lastWidth = -1;
        lastHeight = -1;
    }

    public static void beginTransformEffect() {
        doEffectIn = true;
        doEffectOut = false;
        transformEffectDuration = StaticParams.TRANSFORM_FX_DURATION_IN;
    }

    public static void endTransformEffect() {
        doEffectIn = false;
        doEffectOut = true;
        transformEffectDuration = StaticParams.TRANSFORM_FX_DURATION_OUT;
    }
}