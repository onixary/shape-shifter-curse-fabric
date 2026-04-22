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
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryUtils;
import net.onixary.shapeShifterCurseFabric.items.accessory.CurioUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryUtils.calcAutoMod;

public class TrinketsConditionAction {
    public static boolean isEquipped(Entity entity, String AccessoryMod, Identifier trinketID) {
        if (trinketID == null) {
            return false;
        }
        Optional<Item> trinketItem = Registries.ITEM.getOrEmpty(trinketID);
        if (trinketItem.isEmpty()) {
            return false;
        }
        switch (calcAutoMod(AccessoryMod)) {
            case "trinkets":
                if (!AccessoryUtils.LOADED_Trinkets) {
                    return false;
                }
                if (entity instanceof LivingEntity livingEntity) {
                    Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
                    if (!component.isEmpty()) {
                        boolean IsEquipped = component.get().isEquipped(trinketItem.get());
                        return IsEquipped;
                    }
                }
                return false;
            case "curios":
                if (!AccessoryUtils.LOADED_Curios) {
                    return false;
                }
                if (entity instanceof LivingEntity livingEntity) {
                    return CurioUtils.isEquipped(livingEntity, trinketItem.get());
                }
                return false;
            case "all":
                boolean FoundEquipped = false;
                if (!FoundEquipped && AccessoryUtils.LOADED_Trinkets) {
                    if (entity instanceof LivingEntity livingEntity) {
                        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
                        if (!component.isEmpty()) {
                            boolean IsEquipped = component.get().isEquipped(trinketItem.get());
                            FoundEquipped = IsEquipped;
                        }
                    }
                }
                if (!FoundEquipped && AccessoryUtils.LOADED_Curios) {
                    if (entity instanceof LivingEntity livingEntity) {
                        return CurioUtils.isEquipped(livingEntity, trinketItem.get());
                    }
                }
                return FoundEquipped;
            case "none":
                return false;
            default:
                ShapeShifterCurseFabric.LOGGER.error("[check_accessory_slot] accessory_mod is not valid");
        }
        return false;
    }

    public static boolean CheckEquipped(Entity entity, String AccessoryMod, String GroupString, String SlotString, int Slot, Predicate<ItemStack> conditon, boolean rDefault) {
        if (entity instanceof LivingEntity livingEntity) {
            switch (calcAutoMod(AccessoryMod)) {
                case "trinkets":
                    if (!AccessoryUtils.LOADED_Trinkets) {
                        return rDefault;
                    }
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
                case "curios":
                    if (!AccessoryUtils.LOADED_Curios) {
                        return rDefault;
                    }
                    List<ItemStack> itemStackList = CurioUtils.getEntitySlot(livingEntity, SlotString);
                    if (itemStackList == null) {
                        return rDefault;
                    }
                    if (Slot >= itemStackList.size()) {
                        return rDefault;
                    }
                    ItemStack itemStack = itemStackList.get(Slot);
                    if (conditon == null) {
                        return rDefault;
                    }
                    return conditon.test(itemStack);
                case "none":
                    return rDefault;
                default:
                    ShapeShifterCurseFabric.LOGGER.error("[check_accessory_slot] accessory_mod is not valid");
            }
        }
        return rDefault;
    }

    public static void InvokeEquipped(Entity entity, String AccessoryMod, String GroupString, String SlotString, int Slot, Consumer<Pair<World, ItemStack>> action) {
        if (entity instanceof LivingEntity livingEntity) {
            switch (calcAutoMod(AccessoryMod)) {
                case "trinkets":
                    if (!AccessoryUtils.LOADED_Trinkets) {
                        return;
                    }
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
                case "curios":
                    if (!AccessoryUtils.LOADED_Curios) {
                        return;
                    }
                    List<ItemStack> itemStackList = CurioUtils.getEntitySlot(livingEntity, SlotString);
                    if (itemStackList == null) {
                        return;
                    }
                    if (Slot >= itemStackList.size()) {
                        return;
                    }
                    ItemStack itemStack = itemStackList.get(Slot);
                    if (action == null) {
                        return;
                    }
                    action.accept(new Pair<>(entity.getWorld(), itemStack));
                    return;
                case "none":
                    return;
                default:
                    ShapeShifterCurseFabric.LOGGER.error("[invoke_accessory] accessory_mod is not valid");
            }
        }
        return;
    }

    // 原先有个单独处理drop的函数 但由于trinkets安装状态不确定 java会读取函数描述符 所以trinket的class不能出现在返回值和参数值 所以移除掉了

    public static void DropEquipped(Entity entity, String AccessoryMod, String GroupString, String SlotString, int Slot, boolean remove) {
        if (entity instanceof LivingEntity livingEntity) {
            switch (calcAutoMod(AccessoryMod)) {
                case "trinkets":
                    if (!AccessoryUtils.LOADED_Trinkets) {
                        return;
                    }
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
                        if (!remove) {
                            if (!inv.getStack(Slot).isEmpty()) {
                                entity.getWorld().spawnEntity(new ItemEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ(), inv.getStack(Slot)));
                                inv.setStack(Slot, ItemStack.EMPTY);
                            }
                        } else {
                            inv.setStack(Slot, ItemStack.EMPTY);
                        }
                    } else {
                        for (int i = 0; i < inv.size(); i++) {
                            if (!remove) {
                                if (!inv.getStack(i).isEmpty()) {
                                    entity.getWorld().spawnEntity(new ItemEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ(), inv.getStack(i)));
                                    inv.setStack(i, ItemStack.EMPTY);
                                }
                            } else {
                                inv.setStack(i, ItemStack.EMPTY);
                            }
                        }
                    }
                case "curios":
                    if (!AccessoryUtils.LOADED_Curios) {
                        return;
                    }
                    List<ItemStack> itemStackList = CurioUtils.getEntitySlot(livingEntity, SlotString);
                    if (itemStackList == null) {
                        return;
                    }
                    if (Slot >= itemStackList.size()) {
                        return;
                    }
                    if (Slot >= 0) {
                        if (!remove) {
                            if (!itemStackList.get(Slot).isEmpty()) {
                                entity.getWorld().spawnEntity(new ItemEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ(), itemStackList.get(Slot)));
                                CurioUtils.setEntitySlot(livingEntity, SlotString, Slot, ItemStack.EMPTY);
                            }
                        } else {
                            CurioUtils.setEntitySlot(livingEntity, SlotString, Slot, ItemStack.EMPTY);
                        }
                    } else {
                        for (int i = 0; i < itemStackList.size(); i++) {
                            if (!remove) {
                                if (!itemStackList.get(i).isEmpty()) {
                                    entity.getWorld().spawnEntity(new ItemEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ(), itemStackList.get(i)));
                                    CurioUtils.setEntitySlot(livingEntity, SlotString, i, ItemStack.EMPTY);
                                }
                            } else {
                                CurioUtils.setEntitySlot(livingEntity, SlotString, i, ItemStack.EMPTY);
                            }
                        }
                    }
                    return;
                case "none":
                    return;
                default:
                    ShapeShifterCurseFabric.LOGGER.error("[drop_accessory] accessory_mod is not valid");
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
