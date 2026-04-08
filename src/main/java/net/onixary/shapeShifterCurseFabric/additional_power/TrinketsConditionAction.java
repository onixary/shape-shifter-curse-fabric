package net.onixary.shapeShifterCurseFabric.additional_power;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class TrinketsConditionAction {
    public static boolean isEquipped(Entity entity, Identifier trinketID) {
        if (trinketID == null) {
            return false;
        }
        Optional<Item>  trinketItem = Registries.ITEM.getOrEmpty(trinketID);
        if (trinketItem.isEmpty()) {
            return false;
        }
        if (entity instanceof LivingEntity livingEntity) {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
            if (component.isEmpty()) {
                return false;
            }
            boolean IsEquipped = component.get().isEquipped(trinketItem.get());
            return IsEquipped;
        }
        return false;
    }

    public static boolean CheckEquipped(Entity entity, String GroupString, String SlotString, int Slot, ConditionFactory<ItemStack>.Instance conditon, boolean rDefault) {
        if (entity instanceof LivingEntity livingEntity) {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
            if (component.isEmpty()) {
                return rDefault;
            }
            Map<String, TrinketInventory> groupInv = component.get().getInventory().get(GroupString);
            if (groupInv == null) {
                return rDefault;
            }
            TrinketInventory inv = groupInv.get(SlotString);
            if (inv == null) {
                return rDefault;
            }
            ItemStack stack = inv.getStack(Slot);
            if (conditon == null) {
                return rDefault;
            }
            return conditon.test(stack);
        }
        return rDefault;
    }

    public static void InvokeEquipped(Entity entity, String GroupString, String SlotString, int Slot, ActionFactory<Pair<World, ItemStack>>.Instance action) {
        if (entity instanceof LivingEntity livingEntity) {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
            if (component.isEmpty()) {
                return;
            }
            Map<String, TrinketInventory> groupInv = component.get().getInventory().get(GroupString);
            if (groupInv == null) {
                return;
            }
            TrinketInventory inv = groupInv.get(SlotString);
            if (inv == null) {
                return;
            }
            ItemStack stack = inv.getStack(Slot);
            if (action == null) {
                return;
            }
            action.accept(new Pair<>(entity.getWorld(), stack));
        }
        return;
    }

    private static void DropEquippedSingle(Entity entity, TrinketInventory inv, int slot, boolean remove) {
        if (!remove) {
            if (!inv.getStack(slot).isEmpty()) {
                entity.getWorld().spawnEntity(
                        new ItemEntity(
                                entity.getWorld(),
                                entity.getX(),
                                entity.getY(),
                                entity.getZ(),
                                inv.getStack(slot)
                        )
                );
                inv.setStack(slot, ItemStack.EMPTY);
            }
        } else {
            inv.setStack(slot, ItemStack.EMPTY);
        }
    }

    public static void DropEquipped(Entity entity, String GroupString, String SlotString, int Slot, boolean remove) {
        if (entity instanceof LivingEntity livingEntity) {
            Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
            if (component.isEmpty()) {
                return;
            }
            Map<String, TrinketInventory> groupInv = component.get().getInventory().get(GroupString);
            if (groupInv == null) {
                return;
            }
            TrinketInventory inv = groupInv.get(SlotString);
            if (inv == null) {
                return;
            }
            if (Slot >= 0) {
                DropEquippedSingle(entity, inv, Slot, remove);
            } else {
                for (int i = 0; i < inv.size(); i++) {
                    DropEquippedSingle(entity, inv, i, remove);
                }
            }
        }
    }

    public static void registerCondition(Consumer<ConditionFactory<Entity>> registerFunc) {
        registerFunc.accept(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("equip_accessory"),  // 为了之后写双端不用改 还是使用equip_accessories吧
                new SerializableData()
                        .add("accessory", SerializableDataTypes.IDENTIFIER, null),
                (data, e) -> isEquipped(e, data.get("accessory"))
        ));

        registerFunc.accept(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("check_accessory"),
                new SerializableData()
                        .add("group", SerializableDataTypes.STRING, "")
                        .add("slot", SerializableDataTypes.STRING, "")
                        .add("slot_index", SerializableDataTypes.INT, 0)
                        .add("condition", ApoliDataTypes.ITEM_CONDITION, null),
                (data, e) -> CheckEquipped(e, data.get("group"), data.get("slot"), data.get("slot_index"), data.get("condition"), false)
        ));
    }

    public static void registerAction(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("drop_accessory"),
                new SerializableData()
                        .add("group", SerializableDataTypes.STRING, "")
                        .add("slot", SerializableDataTypes.STRING, "")
                        .add("slot_index", SerializableDataTypes.INT, -1)
                        .add("remove", SerializableDataTypes.BOOLEAN, false),
                (data, e) -> DropEquipped(e, data.get("group"), data.get("slot"), data.get("slot_index"), data.get("remove"))
        ));

        ActionRegister.accept(new ActionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("invoke_accessory"),
                new SerializableData()
                        .add("group", SerializableDataTypes.STRING, "")
                        .add("slot", SerializableDataTypes.STRING, "")
                        .add("slot_index", SerializableDataTypes.INT, 0)
                        .add("action", ApoliDataTypes.ITEM_ACTION, null),
                (data, e) -> InvokeEquipped(e, data.get("group"), data.get("slot"), data.get("slot_index"), data.get("action"))
        ));
    }
}
