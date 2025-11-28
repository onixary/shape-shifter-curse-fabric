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
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;
import static net.onixary.shapeShifterCurseFabric.minion.MinionRegister.WOLF_MINION_LAYER;

public class AnubisWolfMinionEntityRenderer extends MobEntityRenderer<WolfEntity, AnubisWolfMinionEntityModel<WolfEntity>> {
    private static final Identifier ANUBIS_WOLF_MINION_TEXTURE = new Identifier(MOD_ID,"textures/entity/mob/anubis_wolf_minion.png");

    public AnubisWolfMinionEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AnubisWolfMinionEntityModel<>(context.getPart(WOLF_MINION_LAYER)), 0.5F);
    }

    protected float getAnimationProgress(WolfEntity wolfEntity, float f) {
        return wolfEntity.getTailAngle();
    }

    @Override
    public Identifier getTexture(WolfEntity entity) {
        return ANUBIS_WOLF_MINION_TEXTURE;
    }
}