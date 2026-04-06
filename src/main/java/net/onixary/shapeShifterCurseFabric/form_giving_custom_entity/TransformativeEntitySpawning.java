package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.structure.Structure;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TransformativeAxolotlEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot.TransformativeOcelotEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.wolf.TransformativeWolfEntity;

import java.util.Map;
import java.util.stream.Collectors;

public class TransformativeEntitySpawning {
    public static void addEntitySpawns() {
        // T_OCELOT
        SpawnRestriction.register(
                ShapeShifterCurseFabric.T_OCELOT,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING,
                TransformativeOcelotEntity::canCustomSpawn
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(BiomeKeys.JUNGLE)
                        .or(BiomeSelectors.includeByKey(BiomeKeys.BAMBOO_JUNGLE)),
                SpawnGroup.MONSTER,
                ShapeShifterCurseFabric.T_OCELOT,
                10,
                1,
                3
        );
        // T_AXOLOTL
        SpawnRestriction.register(
                ShapeShifterCurseFabric.T_AXOLOTL,
                SpawnRestriction.Location.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                TransformativeAxolotlEntity::canCustomSpawn
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(BiomeKeys.LUSH_CAVES),
                SpawnGroup.AXOLOTLS,
                ShapeShifterCurseFabric.T_AXOLOTL,
                8,
                4,
                6
        );
        // T_BAT
        SpawnRestriction.register(
                ShapeShifterCurseFabric.T_BAT,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                TransformativeBatEntity::canCustomSpawn
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.tag(BiomeTags.IS_OVERWORLD),
                SpawnGroup.AMBIENT,
                ShapeShifterCurseFabric.T_BAT,
                8,
                1,
                3
        );
        // T_WOLF
        SpawnRestriction.register(
                ShapeShifterCurseFabric.T_WOLF,
                SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                TransformativeWolfEntity::canCustomSpawn
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(BiomeKeys.DESERT),
                SpawnGroup.CREATURE,
                ShapeShifterCurseFabric.T_WOLF,
                2,  // 1/2 兔子的权重
                1,
                2
        );

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Structure structure = server.getOverworld().getRegistryManager().get(RegistryKeys.STRUCTURE).get(new Identifier("minecraft", "desert_pyramid"));
            if (structure != null) {
                Map<SpawnGroup, StructureSpawns> oldSpawns = structure.getStructureSpawns();
                Map<SpawnGroup, StructureSpawns> spawns = oldSpawns.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                spawns.put(SpawnGroup.CREATURE, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of(new SpawnSettings.SpawnEntry(ShapeShifterCurseFabric.T_WOLF, 20, 3, 5))));
                structure.config.spawnOverrides = spawns;
            }
            structure = server.getOverworld().getRegistryManager().get(RegistryKeys.STRUCTURE).get(new Identifier("minecraft", "mineshaft"));
            if (structure != null) {
                Map<SpawnGroup, StructureSpawns> oldSpawns = structure.getStructureSpawns();
                Map<SpawnGroup, StructureSpawns> spawns = oldSpawns.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                spawns.put(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of(new SpawnSettings.SpawnEntry(ShapeShifterCurseFabric.T_SPIDER, 5, 1, 2))));
                structure.config.spawnOverrides = spawns;
            }
            structure = server.getOverworld().getRegistryManager().get(RegistryKeys.STRUCTURE).get(new Identifier("minecraft", "mineshaft_mesa"));
            if (structure != null) {
                Map<SpawnGroup, StructureSpawns> oldSpawns = structure.getStructureSpawns();
                Map<SpawnGroup, StructureSpawns> spawns = oldSpawns.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                spawns.put(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of(new SpawnSettings.SpawnEntry(ShapeShifterCurseFabric.T_SPIDER, 5, 1, 2))));
                structure.config.spawnOverrides = spawns;
            }
        });
    }
}
