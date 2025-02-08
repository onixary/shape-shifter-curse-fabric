package net.onixary.shapeShifterCurseFabric.integration.origins.registry;

import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.integration.origins.content.OrbOfOriginItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class ModItems {

    public static final Item ORB_OF_ORIGIN = new OrbOfOriginItem();

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(Origins.MODID, "orb_of_origin"), ORB_OF_ORIGIN);
    }
}
