package net.onixary.shapeShifterCurseFabric.screen_effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public final class TransformOverlay {
    public static final TransformOverlay INSTANCE = new TransformOverlay();
    private final Identifier nausea_texture = new Identifier(MOD_ID, "textures/overlay/nausea_black.png");
    private final Identifier black_texture = new Identifier(MOD_ID, "textures/overlay/black.png");

    private boolean enableOverlay = false;
    private float strength_nausea = 0.0f;
    private float strength_black = 0.0f;

    public void init() {
        enableOverlay = false;
        strength_nausea = 0.0f;
        strength_black = 0.0f;
    }

    @Environment(EnvType.CLIENT)
    public void render()  {
        //double d = MathHelper.lerp(strength, 2.0D, 1.0D);
        if(!enableOverlay){
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        int i = client.getWindow().getScaledWidth();
        int j = client.getWindow().getScaledHeight();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, strength_nausea);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        // draw nausea
        RenderSystem.setShaderTexture(0, nausea_texture);
        Tessellator tessellator_nausea = Tessellator.getInstance();
        BufferBuilder bufferBuilder_nausea = tessellator_nausea.getBuffer();
        bufferBuilder_nausea.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder_nausea.vertex(0, j, -90.0D).texture(0.0F, 1.0F).next();
        bufferBuilder_nausea.vertex(i, j, -90.0D).texture(1.0F, 1.0F).next();
        bufferBuilder_nausea.vertex(i, 0, -90.0D).texture(1.0F, 0.0F).next();
        bufferBuilder_nausea.vertex(0, 0, -90.0D).texture(0.0F, 0.0F).next();
        tessellator_nausea.draw();
        // draw black
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, strength_black);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, black_texture);
        Tessellator tessellator_black = Tessellator.getInstance();
        BufferBuilder bufferBuilder_black = tessellator_black.getBuffer();
        bufferBuilder_black.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder_black.vertex(0, j, -90.0D).texture(0.0F, 1.0F).next();
        bufferBuilder_black.vertex(i, j, -90.0D).texture(1.0F, 1.0F).next();
        bufferBuilder_black.vertex(i, 0, -90.0D).texture(1.0F, 0.0F).next();
        bufferBuilder_black.vertex(0, 0, -90.0D).texture(0.0F, 0.0F).next();
        tessellator_black.draw();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public void setEnableOverlay(boolean enableOverlay) {
        this.enableOverlay = enableOverlay;
    }

    public void setNauesaStrength(float strength) {
        this.strength_nausea = MathHelper.clamp(strength, 0.0f, 1.0f);
    }

    public void setBlackStrength(float strength) {
        this.strength_black = MathHelper.clamp(strength, 0.0f, 1.0f);
    }
}
