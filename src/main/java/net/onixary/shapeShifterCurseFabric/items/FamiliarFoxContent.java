package net.onixary.shapeShifterCurseFabric.items;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.minion.mobs.ManaReservoirEntity;

public final class FamiliarFoxContent {
    public static final Item HEALING_CHARM = Registry.register(Registries.ITEM, ShapeShifterCurseFabric.identifier("healing_charm_paper"), new PotionCharmItem(Potions.STRONG_HEALING));
    public static final Item HARMING_CHARM = Registry.register(Registries.ITEM, ShapeShifterCurseFabric.identifier("harming_charm_paper"), new PotionCharmItem(Potions.STRONG_HARMING));
    public static final Item POISON_CHARM = Registry.register(Registries.ITEM, ShapeShifterCurseFabric.identifier("poison_charm_paper"), new PotionCharmItem(Potions.STRONG_POISON));
    public static final EntityType<ManaReservoirEntity> MANA_RESERVOIR = Registry.register(Registries.ENTITY_TYPE,
            ShapeShifterCurseFabric.identifier("mana_reservoir"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, ManaReservoirEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.6f)).build());
    public static final EntityType<PotionCharmArrowEntity> POTION_CHARM_ARROW = Registry.register(Registries.ENTITY_TYPE,
            ShapeShifterCurseFabric.identifier("potion_charm_arrow"), FabricEntityTypeBuilder.create(SpawnGroup.MISC, PotionCharmArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeBlocks(64).trackedUpdateRate(1).build());
    public static void register() {
        FabricDefaultAttributeRegistry.register(MANA_RESERVOIR, ManaReservoirEntity.attributes());
    }
}
