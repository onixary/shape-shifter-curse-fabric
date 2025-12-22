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

import static net.minecraft.item.Items.register;
import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.T_AXOLOTL;
import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.T_BAT;

public class RegTransformativeEntitySpawnEgg {
    private RegTransformativeEntitySpawnEgg(){}
    // 注册刷怪蛋
    //public static final Item T_BAT_SPAWN_EGG = new SpawnEggItem(
    //        T_BAT, 0x1F1F1F, 0x8B8B8B, new Item.Settings()
    //);

    public static final Item T_BAT_SPAWN_EGG = register("custom_bat_spawn_egg", new SpawnEggItem(
            T_BAT,  0x8B8B8B, 0x1F1F1F,new FabricItemSettings()
    ));

    public static final Item T_AXOLOTL_SPAWN_EGG = register("custom_axolotl_spawn_egg", new SpawnEggItem(
            T_AXOLOTL,  0x8B8B8B,0xe4a7ae, new FabricItemSettings()
    ));

    public static final Item T_OCELOT_SPAWN_EGG = register("custom_ocelot_spawn_egg", new SpawnEggItem(
            ShapeShifterCurseFabric.T_OCELOT,  0x8B8B8B,0xfc801d, new FabricItemSettings()
    ));

    public static final Item T_WOLF_SPAWN_EGG = register("custom_wolf_spawn_egg", new SpawnEggItem(
            ShapeShifterCurseFabric.T_WOLF,  0x8B8B8B,0xffd355, new FabricItemSettings()
    ));

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), item);
    }

    public static void initialize() {
        //Registry.register(Registries.ITEM, new Identifier(ShapeShifterCurseFabric.MOD_ID, "custom_bat_spawn_egg"), T_BAT_SPAWN_EGG);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.add(T_BAT_SPAWN_EGG);
            content.add(T_AXOLOTL_SPAWN_EGG);
            content.add(T_OCELOT_SPAWN_EGG);
            content.add(T_WOLF_SPAWN_EGG);
        });
    }
}
