package net.onixary.shapeShifterCurseFabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.data.ConfigSSC;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.RegEntitySpawnEgg;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityModel;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.BatEntityRenderer;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.item.CustomItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShapeShifterCurseFabric implements ModInitializer {

    public static final String MOD_ID = "shape-shifter-curse";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ConfigSSC CONFIG = ConfigSSC.createAndLoad();

    // Regs
    // Bat
    public static final EntityType<TransformativeBatEntity> T_BAT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(ShapeShifterCurseFabric.MOD_ID, "custom_bat"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TransformativeBatEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );
    public static final EntityModelLayer T_BAT_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "custom_bat"), "main");


    @Override
    public void onInitialize() {
        PlayerDataStorage.initialize();
        CustomItems.initialize();
        RegEntitySpawnEgg.initialize();
        //CONFIG.keepOriginalSkin(false);

        FabricDefaultAttributeRegistry.register(T_BAT, TransformativeBatEntity.createMobAttributes());
        EntityRendererRegistry.register(T_BAT, (context) -> {
            return new BatEntityRenderer(context);
        });

        EntityModelLayerRegistry.registerModelLayer(T_BAT_LAYER, BatEntityModel::getTexturedModelData);
        // entity spawn replacer
        /*ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof BatEntity) {
                // 50% 概率替换为自定义蝙蝠
                if (world.getRandom().nextFloat() < 0.5f) {
                    TransformativeBatEntity customBat = new TransformativeBatEntity(
                            RegTransformativeEntity.T_BAT, world
                    );
                    customBat.refreshPositionAndAngles(
                            entity.getX(), entity.getY(), entity.getZ(),
                            entity.getYaw(), entity.getPitch()
                    );
                    world.spawnEntity(customBat);
                    entity.discard(); // 移除原版蝙蝠
                }
            }
        });*/


        LOGGER.info(CONFIG.keepOriginalSkin() ? "Original skin will be kept." : "Override skin");
    }
}
