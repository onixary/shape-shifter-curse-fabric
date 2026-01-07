package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashSet;

public class FeralRenderUtils {
    public final static HashSet<Identifier> FeralMouseItemBlackList = new HashSet<>();
    static {
        FeralMouseItemBlackList.add(new Identifier("tacz", "modern_kinetic_gun"));
    }

    public static boolean isFeralMouseItemBlackListed(Identifier identifier) {
        return FeralMouseItemBlackList.contains(identifier);
    }

    public static boolean isFeralMouseItemBlackListed(ItemStack itemStack) {
        try {
            return FeralMouseItemBlackList.contains(Registries.ITEM.getId(itemStack.getItem()));
        } catch (Exception e) {
            return false;
        }
    }
}
