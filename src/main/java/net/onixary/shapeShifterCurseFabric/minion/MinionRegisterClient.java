package net.onixary.shapeShifterCurseFabric.minion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.onixary.shapeShifterCurseFabric.minion.mobs.AnubisWolfMinionEntity;
import net.onixary.shapeShifterCurseFabric.minion.mobs.AnubisWolfMinionEntityModel;
import net.onixary.shapeShifterCurseFabric.minion.mobs.AnubisWolfMinionEntityRenderer;

@Environment(EnvType.CLIENT)
public class MinionRegisterClient {
    public static final EntityModelLayer WOLF_MINION_LAYER = new EntityModelLayer(AnubisWolfMinionEntity.MinionID, "main");

    public static void registerClient() {
        EntityRendererRegistry.register(MinionRegister.ANUBIS_WOLF_MINION, AnubisWolfMinionEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(WOLF_MINION_LAYER, AnubisWolfMinionEntityModel::getTexturedModelData);
    }
}
