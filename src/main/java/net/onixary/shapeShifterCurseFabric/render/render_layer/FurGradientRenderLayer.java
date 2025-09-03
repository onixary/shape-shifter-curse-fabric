package net.onixary.shapeShifterCurseFabric.render.render_layer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.api.event.EntitiesPreRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedCoreShader;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform1f;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public abstract class FurGradientRenderLayer {

    public static final ManagedCoreShader furGradientRemap = ShaderEffectManager.getInstance().manageCoreShader(new Identifier(ShapeShifterCurseFabric.MOD_ID, "fur_gradient_remap"));
    private static final Uniform1f uniformSTime = furGradientRemap.findUniform1f("STime");
    private static int ticks;

    public static void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> ticks++);
        EntitiesPreRenderCallback.EVENT.register((camera, frustum, tickDelta) -> uniformSTime.set((ticks + tickDelta) * 0.05f));
//        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
//                    MinecraftClient client = MinecraftClient.getInstance();
//                    client.getFramebuffer().beginWrite(true);
//                    RenderSystem.enableBlend();
//                    RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
//                    client.getFramebuffer().beginWrite(true);
//                    RenderSystem.disableBlend();
//                }
//        );
    }
}
