package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CustomEdibleUtils {

    public static HashMap<UUID, HashMap<Identifier, FoodComponent>> customEdibleMap = new HashMap<>();

    public static FoodComponent getPowerFoodComponent(PlayerEntity user, ItemStack itemStack) {
        HashMap<Identifier, FoodComponent> customEdible = customEdibleMap.computeIfAbsent(user.getUuid(), k -> new HashMap<>());
        return customEdible.getOrDefault(Registries.ITEM.getId(itemStack.getItem()), null);
    }

    public static void addCustomEdible(PlayerEntity user, Identifier itemId, FoodComponent foodComponent) {
        HashMap<Identifier, FoodComponent> customEdible = customEdibleMap.computeIfAbsent(user.getUuid(), k -> new HashMap<>());
        customEdible.put(itemId, foodComponent);
    }

    public static void addCustomEdibleWithList(PlayerEntity user, List<Identifier> itemIdList, FoodComponent foodComponent) {
        HashMap<Identifier, FoodComponent> customEdible = customEdibleMap.computeIfAbsent(user.getUuid(), k -> new HashMap<>());
        for (Identifier itemId : itemIdList) {
            customEdible.put(itemId, foodComponent);
        }
    }

    public static void clearCustomEdible(PlayerEntity user, Identifier itemId) {
        if (customEdibleMap.containsKey(user.getUuid())) {
            customEdibleMap.get(user.getUuid()).remove(itemId);
        }
    }

    public static void clearCustomEdibleWithList(PlayerEntity user, List<Identifier> itemIdList) {
        if (customEdibleMap.containsKey(user.getUuid())) {
            HashMap<Identifier, FoodComponent> customEdible = customEdibleMap.get(user.getUuid());
            for (Identifier itemId : itemIdList) {
                customEdible.remove(itemId);
            }
        }
    }
}
