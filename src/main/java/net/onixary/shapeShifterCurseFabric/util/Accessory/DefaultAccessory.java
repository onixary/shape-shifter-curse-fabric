package net.onixary.shapeShifterCurseFabric.util.Accessory;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DefaultAccessory {
    static {
        AccessoryUtils.registerAccessoryMod("trinkets", new AccessoryUtils.AccessoryIO() {
            @Override
            public int priority() {
                return 1000000;
            }

            @Override
            public boolean canLoaded() {
                return FabricLoader.getInstance().isModLoaded("trinkets");
            }

            @Override
            public Map<Pair<@Nullable String, String>, List<ItemStack>> getEntitySlots(LivingEntity entity) {
                Map<Pair<@Nullable String, String>, List<ItemStack>> map = new HashMap<>();
                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
                if (component.isEmpty()) {
                    return map;
                }
                Map<String, Map<String, TrinketInventory>> invMap = component.get().getInventory();
                for (String slotGroup : invMap.keySet()) {
                    for (String slotName : invMap.get(slotGroup).keySet()) {
                        List<ItemStack> stacks = new ArrayList<>();
                        TrinketInventory inventory = invMap.get(slotGroup).get(slotName);
                        for (int i = 0; i < inventory.size(); i++) {
                            ItemStack stack = inventory.getStack(i);
                            stacks.add(stack);
                        }
                        map.put(new Pair<>(slotGroup, slotName), stacks);
                    }
                }
                return map;
            }

            @Override
            public List<ItemStack> getEntitySlot(LivingEntity entity, @Nullable String SlotGroup, String SlotName) {
                List<ItemStack> ItemList = new ArrayList<>();
                if (SlotGroup == null) {
                    return ItemList;
                }
                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
                if (component.isEmpty()) {
                    return ItemList;
                }
                Map<String, Map<String, TrinketInventory>> invMap = component.get().getInventory();
                if (invMap.containsKey(SlotGroup) && invMap.get(SlotGroup).containsKey(SlotName)) {
                    TrinketInventory inventory = invMap.get(SlotGroup).get(SlotName);
                    for (int i = 0; i < inventory.size(); i++) {
                        ItemStack stack = inventory.getStack(i);
                        ItemList.add(stack);
                    }
                }
                return ItemList;
            }

            @Override
            public @Nullable ItemStack getEntitySlot(LivingEntity entity, @Nullable String SlotGroup, String SlotName, int Index) {
                if (SlotGroup == null) {
                    return null;
                }
                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
                if (component.isEmpty()) {
                    return null;
                }
                Map<String, Map<String, TrinketInventory>> invMap = component.get().getInventory();
                if (invMap.containsKey(SlotGroup) && invMap.get(SlotGroup).containsKey(SlotName)) {
                    TrinketInventory inventory = invMap.get(SlotGroup).get(SlotName);
                    if (Index >= 0 && Index < inventory.size()) {
                        return inventory.getStack(Index);
                    }
                }
                return null;
            }

            @Override
            public void setEntitySlot(LivingEntity entity, @Nullable String SlotGroup, String SlotName, int Index, ItemStack stack) {
                if (SlotGroup == null) {
                    return;
                }
                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
                if (component.isEmpty()) {
                    return;
                }
                Map<String, Map<String, TrinketInventory>> invMap = component.get().getInventory();
                if (invMap.containsKey(SlotGroup) && invMap.get(SlotGroup).containsKey(SlotName)) {
                    TrinketInventory inventory = invMap.get(SlotGroup).get(SlotName);
                    inventory.setStack(Index, stack);
                }
            }
        });

        AccessoryUtils.registerAccessoryMod("curios", new AccessoryUtils.AccessoryIO() {
            @Override
            public int priority() {
                return 1000;
            }

            @Override
            public boolean canLoaded() {
                return FabricLoader.getInstance().isModLoaded("curios");
            }

            @Override
            public Map<Pair<@Nullable String, String>, List<ItemStack>> getEntitySlots(LivingEntity entity) {
                Map<Pair<@Nullable String, String>, List<ItemStack>> map = new HashMap<>();
                Map<String, List<ItemStack>> curiosData = CurioUtils.getEntitySlots(entity);
                for (String slotName : curiosData.keySet()) {
                    map.put(new Pair<>(null, slotName), curiosData.get(slotName));
                }
                return map;
            }

            @Override
            public List<ItemStack> getEntitySlot(LivingEntity entity, @Nullable String SlotGroup, String SlotName) {
                return CurioUtils.getEntitySlot(entity, SlotName);
            }

            @Override
            public @Nullable ItemStack getEntitySlot(LivingEntity entity, @Nullable String SlotGroup, String SlotName, int Index) {
                List<ItemStack> ItemList = CurioUtils.getEntitySlot(entity, SlotName);
                if (Index >= 0 && Index < ItemList.size()) {
                    return ItemList.get(Index);
                }
                return null;
            }

            @Override
            public void setEntitySlot(LivingEntity entity, @Nullable String SlotGroup, String SlotName, int Index, ItemStack stack) {
                CurioUtils.setEntitySlot(entity, SlotName, Index, stack);
            }
        });

        AccessoryUtils.reCalcAccessoryMod();
    }

    public static void init() {
        // DO NOTHING
    }
}
