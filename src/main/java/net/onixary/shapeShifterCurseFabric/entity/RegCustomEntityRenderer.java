package net.onixary.shapeShifterCurseFabric.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class RegCustomEntityRenderer {
    static {
        EntityRendererRegistry.register(RegCustomEntity.WEB_BULLET, FlyingItemEntityRenderer::new);
    }

    public static void init() {
    }
}
