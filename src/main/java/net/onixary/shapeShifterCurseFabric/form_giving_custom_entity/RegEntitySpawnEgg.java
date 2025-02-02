package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.T_BAT;

public class RegEntitySpawnEgg {
    // 注册刷怪蛋
    //public static final Item T_BAT_SPAWN_EGG = new SpawnEggItem(
    //        T_BAT, 0x1F1F1F, 0x8B8B8B, new Item.Settings()
    //);

    public static final Item T_BAT_SPAWN_EGG = register("custom_bat_spawn_egg.json", new SpawnEggItem(
            T_BAT, 0x1F1F1F, 0x8B8B8B, new FabricItemSettings()
    ));

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), item);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.add(T_BAT_SPAWN_EGG);
        });
    }
}
