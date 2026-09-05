package net.onixary.shapeShifterCurseFabric.blocks;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.blocks.block_entity.AlterBlockEntity;

public final class RegCustomBlock {
    public static final Block MOONDUST_CRYSTAL_GRIT = register("moondust_crystal_grit", new Block(AbstractBlock.Settings.copy(Blocks.GRAVEL).mapColor(MapColor.PURPLE).strength(0.6f, 0.6f).sounds(BlockSoundGroup.GRAVEL)));
    // TODO TEMP_WEB_BRIDGE 仅在测试时有物品 发布时记得用 registerWithOutItem
    public static final Block TEMP_WEB_BRIDGE = register("temp_web_bridge", new TempWebBridgeBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE_GRAY).strength(4.0f).ticksRandomly().noCollision().dynamicBounds().dropsNothing().solidBlock(Blocks::never).burnable().sounds(BlockSoundGroup.WOOL)));

    public static final Block WEB_COMPOSTER = register("web_composter", new WebComposterBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE_GRAY).instrument(Instrument.BASS).strength(0.6F).sounds(BlockSoundGroup.WOOL).burnable().nonOpaque()));
    public static final Block DEW_COVERED_COBWEB = register("dew_covered_cobweb", new DewCoveredCobwebBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE_GRAY).instrument(Instrument.BELL).strength(1.0F).sounds(BlockSoundGroup.WOOL).noCollision().nonOpaque()));

    public static final Block ALTER_BLOCK = register("alter", new AlterBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE_GRAY).instrument(Instrument.BELL).strength(4.0F, 10.0F).sounds(BlockSoundGroup.STONE).nonOpaque()));
    public static final BlockEntityType<AlterBlockEntity> ALTER_BLOCK_ENTITY = registerBlockEntity("alter_block_entity", BlockEntityType.Builder.create(AlterBlockEntity::new, ALTER_BLOCK).build(null));


    public static void ClientInit() {
        // transparent透明模式不写Z，会出现自排序问题遮挡自己，只需要镂空的模型应该使用getCutout
        BlockRenderLayerMap.INSTANCE.putBlock(TEMP_WEB_BRIDGE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(WEB_COMPOSTER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(DEW_COVERED_COBWEB, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ALTER_BLOCK, RenderLayer.getCutout());
    }

    private static <T extends Block> T registerWithOutItem(String path, T block) {
        Registry.register(Registries.BLOCK, ShapeShifterCurseFabric.identifier(path), block);
        return block;
    }

    private static <T extends Block> T register(String path, T block) {
        Registry.register(Registries.BLOCK, ShapeShifterCurseFabric.identifier(path), block);
        Registry.register(Registries.ITEM, ShapeShifterCurseFabric.identifier(path), new BlockItem(block, new Item.Settings()));
        return block;
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String path, BlockEntityType<T> blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, ShapeShifterCurseFabric.identifier(path), blockEntityType);
    }

    public static void initialize() {
        // 蔓延速度=20, 燃烧速度=5，与木板相同
        FlammableBlockRegistry.getDefaultInstance().add(TEMP_WEB_BRIDGE, 60, 20);
    }
}
