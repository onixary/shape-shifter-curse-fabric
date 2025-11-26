package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ModTags {
    public static final TagKey<EntityType<?>> Illager_Tag = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(ShapeShifterCurseFabric.MOD_ID, "illager"));
    public static final TagKey<EntityType<?>> Witch_Tag = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(ShapeShifterCurseFabric.MOD_ID, "witch"));
    public static final TagKey<Item> MorphScaleItem_Tag = TagKey.of(RegistryKeys.ITEM, new Identifier(ShapeShifterCurseFabric.MOD_ID, "morph_scale_item"));
}
