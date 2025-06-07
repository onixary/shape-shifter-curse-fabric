package net.onixary.shapeShifterCurseFabric.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import static net.minecraft.item.Items.register;

public class RegCustomItem {
    private RegCustomItem(){}

    //public static final Item CURSED_BOOK_OF_SHAPE_SHIFTER = register("cursed_book_of_shape_shifter", new StartBook(new StartBook.Settings()));
    public static final Item BOOK_OF_SHAPE_SHIFTER = register("book_of_shape_shifter", new BookOfShapeShifter(new BookOfShapeShifter.Settings()));
    public static final Item UNTREATED_MOONDUST = register("untreated_moondust", new UntreatedMoonDust(new Item.Settings()));
    public static final Item INHIBITOR = register("inhibitor", new Inhibitor(new Item.Settings()));
    public static final Item POWERFUL_INHIBITOR = register("powerful_inhibitor", new PowerfulInhibitor(new Item.Settings()));
    public static final Item CATALYST = register("catalyst", new Catalyst(new Item.Settings()));
    public static final Item MOONDUST_MATRIX = register("moondust_matrix", new MoonDustMatrix(new Item.Settings()));

    // 用于成就图标的占位物品
    public static final Item ICON_CURSED_MOON = register("icon_cursed_moon", new Item(new Item.Settings()));

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), item);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(BOOK_OF_SHAPE_SHIFTER);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(UNTREATED_MOONDUST);
            entries.add(MOONDUST_MATRIX);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(INHIBITOR);
            entries.add(POWERFUL_INHIBITOR);
            entries.add(CATALYST);
        });
    }
}
