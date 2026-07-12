package net.onixary.shapeShifterCurseFabric.util;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AdvancementUtils {
    public static final HashMap<Identifier, List<Consumer<Advancement>>> advancementAddedCallbacks = new HashMap<>();

    public static void registerAdvancementAddedCallback(Identifier id, Consumer<Advancement> callback) {
        if (advancementAddedCallbacks.containsKey(id)) {
            advancementAddedCallbacks.get(id).add(callback);
        } else {
            advancementAddedCallbacks.put(id, List.of(callback));
        }
    }

    public static void onAdvancementAdded(Advancement advancement) {
        Identifier id = advancement.getId();
        if (advancementAddedCallbacks.containsKey(id)) {
            advancementAddedCallbacks.get(id).forEach(callback -> callback.accept(advancement));
        }
    }

    // XuHaoNan:
    // 使用加载时patch 兼容性最强 比覆盖原版数据包兼容性强了不少
    // 我认为覆盖原版数据包这种应该仅数据包Mod使用 都写javaMod了 别和数据包抢修改方式
    static {
        registerAdvancementAddedCallback(new Identifier("minecraft", "nether/netherite_armor"), advancement -> {
            advancement.getCriteria().forEach((id, criteria) -> {
                if (id.equals("netherite_armor") && criteria.getConditions() instanceof InventoryChangedCriterion.Conditions conditions) {
                    for (int index = 0, size = conditions.items.length; index < size; index++) {
                        ItemPredicate predicate = conditions.items[index];
                        if (predicate.items == null) {
                            continue;
                        }
                        boolean isModify = false;
                        Set<Item> items = predicate.items;
                        List<Item> newItem = new ArrayList<>();
                        for (Item item : items) {
                            newItem.add(item);
                            if (item == Items.NETHERITE_HELMET) {
                                newItem.add(RegCustomItem.NETHERITE_MORPHSCALE_HEADRING);
                                isModify = true;
                                break;
                            }
                            if (item == Items.NETHERITE_CHESTPLATE) {
                                newItem.add(RegCustomItem.NETHERITE_MORPHSCALE_VEST);
                                isModify = true;
                                break;
                            }
                            if (item == Items.NETHERITE_LEGGINGS) {
                                newItem.add(RegCustomItem.NETHERITE_MORPHSCALE_CUISH);
                                isModify = true;
                                break;
                            }
                            if (item == Items.NETHERITE_BOOTS) {
                                newItem.add(RegCustomItem.NETHERITE_MORPHSCALE_ANKLET);
                                isModify = true;
                                break;
                            }
                        }
                        if (isModify) {
                            Set<Item> newItems = Set.copyOf(newItem);
                            conditions.items[index] = new ItemPredicate(predicate.tag, newItems, predicate.count, predicate.durability, predicate.enchantments, predicate.storedEnchantments, predicate.potion, predicate.nbt);
                        }
                    }
                }
            });
        });
        registerAdvancementAddedCallback(new Identifier("minecraft", "story/shiny_gear"), advancement -> {
            advancement.getCriteria().forEach((id, criteria) -> {
                if (id.equals("diamond_helmet") && criteria.getConditions() instanceof InventoryChangedCriterion.Conditions conditions) {
                    for (int index = 0, size = conditions.items.length; index < size; index++) {
                        ItemPredicate predicate = conditions.items[index];
                        if (predicate.items == null) {
                            continue;
                        }
                        boolean isModify = false;
                        Set<Item> items = predicate.items;
                        List<Item> newItem = new ArrayList<>();
                        for (Item item : items) {
                            newItem.add(item);
                            if (item == Items.DIAMOND_HELMET) {
                                newItem.add(RegCustomItem.MORPHSCALE_HEADRING);
                                isModify = true;
                            }
                        }
                        if (isModify) {
                            Set<Item> newItems = Set.copyOf(newItem);
                            conditions.items[index] = new ItemPredicate(predicate.tag, newItems, predicate.count, predicate.durability, predicate.enchantments, predicate.storedEnchantments, predicate.potion, predicate.nbt);
                        }
                    }
                }
                if (id.equals("diamond_chestplate") && criteria.getConditions() instanceof InventoryChangedCriterion.Conditions conditions) {
                    for (int index = 0, size = conditions.items.length; index < size; index++) {
                        ItemPredicate predicate = conditions.items[index];
                        if (predicate.items == null) {
                            continue;
                        }
                        boolean isModify = false;
                        Set<Item> items = predicate.items;
                        List<Item> newItem = new ArrayList<>();
                        for (Item item : items) {
                            newItem.add(item);
                            if (item == Items.DIAMOND_CHESTPLATE) {
                                newItem.add(RegCustomItem.MORPHSCALE_VEST);
                                isModify = true;
                            }
                        }
                        if (isModify) {
                            Set<Item> newItems = Set.copyOf(newItem);
                            conditions.items[index] = new ItemPredicate(predicate.tag, newItems, predicate.count, predicate.durability, predicate.enchantments, predicate.storedEnchantments, predicate.potion, predicate.nbt);
                        }
                    }
                }
                if (id.equals("diamond_leggings") && criteria.getConditions() instanceof InventoryChangedCriterion.Conditions conditions) {
                    for (int index = 0, size = conditions.items.length; index < size; index++) {
                        ItemPredicate predicate = conditions.items[index];
                        if (predicate.items == null) {
                            continue;
                        }
                        boolean isModify = false;
                        Set<Item> items = predicate.items;
                        List<Item> newItem = new ArrayList<>();
                        for (Item item : items) {
                            newItem.add(item);
                            if (item == Items.DIAMOND_LEGGINGS) {
                                newItem.add(RegCustomItem.MORPHSCALE_CUISH);
                                isModify = true;
                            }
                        }
                        if (isModify) {
                            Set<Item> newItems = Set.copyOf(newItem);
                            conditions.items[index] = new ItemPredicate(predicate.tag, newItems, predicate.count, predicate.durability, predicate.enchantments, predicate.storedEnchantments, predicate.potion, predicate.nbt);
                        }
                    }
                }
                if (id.equals("diamond_boots") && criteria.getConditions() instanceof InventoryChangedCriterion.Conditions conditions) {
                    for (int index = 0, size = conditions.items.length; index < size; index++) {
                        ItemPredicate predicate = conditions.items[index];
                        if (predicate.items == null) {
                            continue;
                        }
                        boolean isModify = false;
                        Set<Item> items = predicate.items;
                        List<Item> newItem = new ArrayList<>();
                        for (Item item : items) {
                            newItem.add(item);
                            if (item == Items.DIAMOND_BOOTS) {
                                newItem.add(RegCustomItem.MORPHSCALE_ANKLET);
                                isModify = true;
                            }
                        }
                        if (isModify) {
                            Set<Item> newItems = Set.copyOf(newItem);
                            conditions.items[index] = new ItemPredicate(predicate.tag, newItems, predicate.count, predicate.durability, predicate.enchantments, predicate.storedEnchantments, predicate.potion, predicate.nbt);
                        }
                    }
                }
            });
        });

    }
}
