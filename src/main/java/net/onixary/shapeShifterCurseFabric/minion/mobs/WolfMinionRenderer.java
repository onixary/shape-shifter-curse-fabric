package net.onixary.shapeShifterCurseFabric.minion.mobs;

/* 弃用 不会做AzureLib模型 原先的模型不兼容 连测试都没法测试

import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class WolfMinionRenderer extends AzEntityRenderer<WolfMinion> {
    private static final Identifier GEO = ShapeShifterCurseFabric.identifier(
            "geo/entity/wolf_minion.geo.json"
    );

    private static final Identifier TEX = ShapeShifterCurseFabric.identifier(
            "textures/entity/mob/wolf_minion.png"
    );

    public WolfMinionRenderer(EntityRendererFactory.Context context) {
        super(
                AzEntityRendererConfig.<WolfMinion>builder(GEO, TEX).setAnimatorProvider(WolfMinionAnimator::new).build(),
                context
        );
    }
}

 */

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WolfEntityRenderer;

public class WolfMinionRenderer extends WolfEntityRenderer {
    public WolfMinionRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
}