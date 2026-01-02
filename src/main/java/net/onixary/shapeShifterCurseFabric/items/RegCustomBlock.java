package net.onixary.shapeShifterCurseFabric.items;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public final class RegCustomBlock {
    public static final Block MOONDUST_CRYSTAL_GRIT = register("moondust_crystal_grit", new Block(AbstractBlock.Settings.copy(Blocks.GRAVEL).mapColor(MapColor.PURPLE).strength(0.6f, 0.6f).sounds(BlockSoundGroup.GRAVEL)));

    private static <T extends Block> T register(String path, T block) {
        Registry.register(Registries.BLOCK, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), block);
        Registry.register(Registries.ITEM, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize() {
    }
}
