package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashSet;

public class FeralRenderUtils {
    public final static HashSet<Identifier> FeralMouthItemBlackList = new HashSet<>();
    static {
        FeralMouthItemBlackList.add(new Identifier("tacz", "modern_kinetic_gun"));
    }

    public static boolean isFeralMouthItemBlackListed(Identifier identifier) {
        return FeralMouthItemBlackList.contains(identifier);
    }

    public static boolean isFeralMouthItemBlackListed(ItemStack itemStack) {
        try {
            return FeralMouthItemBlackList.contains(Registries.ITEM.getId(itemStack.getItem()));
        } catch (Exception e) {
            return false;
        }
    }
}
