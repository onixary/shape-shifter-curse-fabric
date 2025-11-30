package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import net.minecraft.world.gen.structure.StructureType;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TransformativeAxolotlEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot.TransformativeOcelotEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.wolf.TransformativeWolfEntity;

public class TransformativeEntitySpawning {
    public static void addEntitySpawns() {
        // original weights located at data/minecraft/worldgen/biome/...

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
                1,  // 1/4 兔子的权重
                2,
                3
        );
        // 用数据包的方式来让T_WOLF生成在沙漠神殿 可能会与修改结构的Mod冲突
    }
}
