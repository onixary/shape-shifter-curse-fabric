package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.render.entity.AxolotlEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TAxolotlEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TransformativeAxolotlEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityModel;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.*;
//import static net.onixary.shapeShifterCurseFabric.data.StaticParams.T_AXOLOTL_REPLACE_PROBABILITY;
//import static net.onixary.shapeShifterCurseFabric.data.StaticParams.T_BAT_REPLACE_PROBABILITY;

public class TEntitySpawnHandler {
    public static final EntityModelLayer T_BAT_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_bat"), "main");
    public static final EntityModelLayer T_AXOLOTL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "t_axolotl"), "main");

    public static void register() {
        // Reg custom entities model and renderer
        // bat
        FabricDefaultAttributeRegistry.register(T_BAT, TransformativeBatEntity.createTBatAttributes());
        EntityRendererRegistry.register(T_BAT, (context) -> {
            return new BatEntityRenderer(context);
        });
        EntityModelLayerRegistry.registerModelLayer(T_BAT_LAYER, BatEntityModel::getTexturedModelData);
        // axolotl
        FabricDefaultAttributeRegistry.register(T_AXOLOTL, TransformativeAxolotlEntity.createTAxolotlAttributes());
        EntityRendererRegistry.register(T_AXOLOTL, (context) -> {
            return new TAxolotlEntityRenderer(context);
        });
        EntityModelLayerRegistry.registerModelLayer(T_AXOLOTL_LAYER, BatEntityModel::getTexturedModelData);

        // handle entity spawn
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof BatEntity) {
                TBatSpawnHandler((BatEntity) entity, world);
            }
            else if (entity instanceof AxolotlEntity) {
                if(!((AxolotlEntity) entity).isFromBucket()){
                    TAxolotlSpawnHandler((AxolotlEntity) entity, world);
                }
            }
        });
    }

    private static void TBatSpawnHandler(BatEntity entity, World serverWorld) {
        if (serverWorld.getRandom().nextFloat() < CONFIG.transformativeBatSpawnChance()) {
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

    private static void TAxolotlSpawnHandler(AxolotlEntity entity, World serverWorld) {
        if (serverWorld.getRandom().nextFloat() < CONFIG.transformativeAxolotlSpawnChance()) {
            TransformativeAxolotlEntity customAxolotl = new TransformativeAxolotlEntity(
                    T_AXOLOTL, serverWorld
            );
            customAxolotl.refreshPositionAndAngles(
                    entity.getX(), entity.getY(), entity.getZ(),
                    entity.getYaw(), entity.getPitch()
            );
            serverWorld.spawnEntity(customAxolotl);
            entity.discard();
        }
    }
}
