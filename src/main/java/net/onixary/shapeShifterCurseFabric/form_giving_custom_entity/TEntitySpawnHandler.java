package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TransformativeAxolotlEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot.TransformativeOcelotEntity;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.*;

public class TEntitySpawnHandler {

    public static void register() {
        // Reg custom entities model and renderer
        // bat
        FabricDefaultAttributeRegistry.register(T_BAT, TransformativeBatEntity.createTBatAttributes());
        // axolotl
        FabricDefaultAttributeRegistry.register(T_AXOLOTL, TransformativeAxolotlEntity.createTAxolotlAttributes());
        // ocelot
        FabricDefaultAttributeRegistry.register(T_OCELOT, TransformativeBatEntity.createTBatAttributes());


        // handle entity spawn
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if(!entity.hasCustomName()){
                if (entity instanceof BatEntity) {
                    TBatSpawnHandler((BatEntity) entity, world);
                }
                else if (entity instanceof AxolotlEntity) {
                    if(!((AxolotlEntity) entity).isFromBucket()){
                        TAxolotlSpawnHandler((AxolotlEntity) entity, world);
                    }
                }
                else if (entity instanceof OcelotEntity) {
                    TOcelotSpawnHandler((OcelotEntity) entity, world);
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

    private static void TOcelotSpawnHandler(OcelotEntity entity, World serverWorld) {
        if (serverWorld.getRandom().nextFloat() < CONFIG.transformativeOcelotSpawnChance()) {
            TransformativeOcelotEntity customOcelot = new TransformativeOcelotEntity(
                    T_OCELOT, serverWorld
            );
            customOcelot.refreshPositionAndAngles(
                    entity.getX(), entity.getY(), entity.getZ(),
                    entity.getYaw(), entity.getPitch()
            );
            serverWorld.spawnEntity(customOcelot);
            entity.discard();
        }
    }
}
