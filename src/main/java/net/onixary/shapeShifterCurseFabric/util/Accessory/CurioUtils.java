package net.onixary.shapeShifterCurseFabric.util.Accessory;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.util.TrinketUtils;

import java.util.List;
import java.util.Map;

public class CurioUtils {
    public static boolean isLoaded() { return false; }

    public static boolean isEquipped(LivingEntity entity, Item item) {
        return false;
    }

    public static Map<String, List<ItemStack>> getEntitySlots(LivingEntity entity) {
        return Map.of();
    }

    public static List<ItemStack> getEntitySlot(LivingEntity entity, String SlotName) {
        return List.of();
    }

    public static void setEntitySlot(LivingEntity entity, String SlotName, int Index, ItemStack stack) {
        return;
    }

    public static Map<String, List<ItemStack>> getEntityCosmeticSlots(LivingEntity entity) { return Map.of(); }

    public static List<ItemStack> getEntityCosmeticSlot(LivingEntity entity, String SlotName) { return List.of(); }

    public static void setEntityCosmeticSlot(LivingEntity entity, String SlotName, int Index, ItemStack stack) { return; }

    public static Map<String, List<Boolean>> getEntitySlotRenders(LivingEntity entity) { return Map.of(); }

    public static List<Boolean> getEntitySlotRender(LivingEntity entity, String SlotName) { return List.of(); }

    public static void setEntitySlotRender(LivingEntity entity, String SlotName, int Index, boolean render) { return; }
}
