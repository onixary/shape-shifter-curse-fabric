package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityModel;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;
import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.T_BAT;

public class TEntitySpawnHandler {
    public static final EntityModelLayer T_BAT_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_bat"), "main");

    public static void register() {
        // Reg custom entities model and renderer
        // bat
        FabricDefaultAttributeRegistry.register(T_BAT, TransformativeBatEntity.createTBatAttributes());
        EntityRendererRegistry.register(T_BAT, (context) -> {
            return new BatEntityRenderer(context);
        });
        EntityModelLayerRegistry.registerModelLayer(T_BAT_LAYER, BatEntityModel::getTexturedModelData);

        // handle entity spawn
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof BatEntity) {
                TBatSpawnHandler((BatEntity) entity, world);
            }
        });
    }

    private static void TBatSpawnHandler(BatEntity entity, World serverWorld) {
        // 50% 概率替换为自定义蝙蝠
        if (serverWorld.getRandom().nextFloat() < 0.5f) {
            TransformativeBatEntity customBat = new TransformativeBatEntity(
                    T_BAT, serverWorld
            );
            customBat.refreshPositionAndAngles(
                    entity.getX(), entity.getY(), entity.getZ(),
                    entity.getYaw(), entity.getPitch()
            );
            serverWorld.spawnEntity(customBat);
            entity.discard(); // 移除原版蝙蝠
        }
    }
}
