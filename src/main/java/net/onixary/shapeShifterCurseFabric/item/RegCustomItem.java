package net.onixary.shapeShifterCurseFabric.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import static net.minecraft.item.Items.register;

public class RegCustomItem {
    private RegCustomItem(){}

    //public static final Item CURSED_BOOK_OF_SHAPE_SHIFTER = register("cursed_book_of_shape_shifter", new StartBook(new StartBook.Settings()));
    public static final Item BOOK_OF_SHAPE_SHIFTER = register("book_of_shape_shifter", new BookOfShapeShifter(new BookOfShapeShifter.Settings()));

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, new Identifier(ShapeShifterCurseFabric.MOD_ID, path), item);
    }

    public static void initialize() {
    }
}
