package net.onixary.shapeShifterCurseFabric.additional_power;

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
import net.onixary.shapeShifterCurseFabric.util.Accessory.AccessoryUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TrinketsConditionAction {
    public static boolean isEquipped(Entity entity, String AccessoryMod, Identifier trinketID) {
        if (trinketID == null) {
            return false;
        }
        Optional<Item> trinketItem = Registries.ITEM.getOrEmpty(trinketID);
        if (trinketItem.isEmpty()) {
            return false;
        }
        if (entity instanceof LivingEntity livingEntity) {
            if (Objects.equals(AccessoryMod, "all")) {
                for (AccessoryUtils.AccessoryIO accessoryIO : AccessoryUtils.activeAccessoryModInterfaces.values()) {
                    @Nullable Map<Pair<@Nullable String, String>, List<ItemStack>> slots = accessoryIO.getEntitySlots(livingEntity);
                    if (slots != null) {
                        for (List<ItemStack> slot : slots.values()) {
                            for (ItemStack stack : slot) {
                                if (stack.getItem() == trinketItem.get()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            } else {
                @Nullable Map<Pair<@Nullable String, String>, List<ItemStack>> slots = AccessoryUtils.getEntitySlots(livingEntity, AccessoryMod);
                if (slots != null) {
                    for (List<ItemStack> slot : slots.values()) {
                        for (ItemStack stack : slot) {
                            if (stack.getItem() == trinketItem.get()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean CheckEquipped(Entity entity, String AccessoryMod, String GroupString, String SlotString, int Slot, Predicate<ItemStack> conditon, boolean rDefault) {
        if (entity instanceof LivingEntity livingEntity) {
            List<ItemStack> accessoryList = AccessoryUtils.getEntitySlot(livingEntity, AccessoryMod, GroupString, SlotString);
            if (accessoryList != null && Slot >= 0 && Slot < accessoryList.size()) {
                ItemStack stack = accessoryList.get(Slot);
                if (!stack.isEmpty()) {
                    return conditon.test(stack);
                }
            }
        }
        return rDefault;
    }

    public static void InvokeEquipped(Entity entity, String AccessoryMod, String GroupString, String SlotString, int Slot, Consumer<Pair<World, ItemStack>> action) {
        if (entity instanceof LivingEntity livingEntity) {
            List<ItemStack> accessoryList = AccessoryUtils.getEntitySlot(livingEntity, AccessoryMod, GroupString, SlotString);
            if (accessoryList != null && Slot >= 0 && Slot < accessoryList.size()) {
                ItemStack stack = accessoryList.get(Slot);
                if (!stack.isEmpty()) {
                    action.accept(new Pair<>(entity.getWorld(), stack));
                }
            }
        }
        return;
    }

    // 原先有个单独处理drop的函数 但由于trinkets安装状态不确定 java会读取函数描述符 所以trinket的class不能出现在返回值和参数值 所以移除掉了

    public static void DropEquipped(Entity entity, String AccessoryMod, String GroupString, String SlotString, int Slot, boolean remove) {
        if (entity instanceof LivingEntity livingEntity) {
            List<ItemStack> accessoryList = AccessoryUtils.getEntitySlot(livingEntity, AccessoryMod, GroupString, SlotString);
            if (accessoryList != null && Slot >= 0 && Slot < accessoryList.size()) {
                ItemStack stack = accessoryList.get(Slot);
                if (!stack.isEmpty()) {
                    if (!remove) {
                        entity.getWorld().spawnEntity(new ItemEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ(), stack));
                    }
                    AccessoryUtils.setEntitySlot(livingEntity, AccessoryMod, GroupString, SlotString, Slot, ItemStack.EMPTY);
                }
            }
        }
    }

    public static void registerCondition(Consumer<ConditionFactory<Entity>> registerFunc) {
        registerFunc.accept(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("equip_accessory"),  // 为了之后写双端不用改 还是使用equip_accessories吧
                new SerializableData()
                        .add("accessory_mod", SerializableDataTypes.STRING, "auto")
                        .add("accessory", SerializableDataTypes.IDENTIFIER, null),
                (data, e) -> isEquipped(e, data.get("accessory_mod"), data.get("accessory"))
        ));

        registerFunc.accept(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("check_accessory"),
                new SerializableData()
                        .add("accessory_mod", SerializableDataTypes.STRING, "auto")
                        .add("group", SerializableDataTypes.STRING, "")
                        .add("slot", SerializableDataTypes.STRING, "")
                        .add("slot_index", SerializableDataTypes.INT, 0)
                        .add("condition", ApoliDataTypes.ITEM_CONDITION, null),
                (data, e) -> CheckEquipped(e, data.get("accessory_mod"), data.get("group"), data.get("slot"), data.get("slot_index"), data.get("condition"), false)
        ));
    }

    public static void registerAction(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("drop_accessory"),
                new SerializableData()
                        .add("accessory_mod", SerializableDataTypes.STRING, "auto")
                        .add("group", SerializableDataTypes.STRING, "")
                        .add("slot", SerializableDataTypes.STRING, "")
                        .add("slot_index", SerializableDataTypes.INT, -1)
                        .add("remove", SerializableDataTypes.BOOLEAN, false),
                (data, e) -> DropEquipped(e, data.get("accessory_mod"), data.get("group"), data.get("slot"), data.get("slot_index"), data.get("remove"))
        ));

        ActionRegister.accept(new ActionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("invoke_accessory"),
                new SerializableData()
                        .add("accessory_mod", SerializableDataTypes.STRING, "auto")
                        .add("group", SerializableDataTypes.STRING, "")
                        .add("slot", SerializableDataTypes.STRING, "")
                        .add("slot_index", SerializableDataTypes.INT, 0)
                        .add("action", ApoliDataTypes.ITEM_ACTION, null),
                (data, e) -> InvokeEquipped(e, data.get("accessory_mod"), data.get("group"), data.get("slot"), data.get("slot_index"), data.get("action"))
        ));
    }
}
