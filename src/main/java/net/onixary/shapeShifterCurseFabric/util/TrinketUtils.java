package net.onixary.shapeShifterCurseFabric.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.util.Accessory.AccessoryUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrinketUtils {
    public interface CustomPowerTrinketInterface {
        void onFormChange(ItemStack stack, AccessoryItem.SlotData slot, PlayerEntity entity);
    }

    public static class TrinketPowerData {
        public final List<Identifier> accessoryPowers;
        public final List<Identifier> allFormPowerAdd;
        public final List<Identifier> allFormPowerRemove;
        public final HashMap<Identifier, List<Identifier>> formPowerAdd;
        public final HashMap<Identifier, List<Identifier>> formPowerRemove;
        public final HashMap<Identifier, HashMap<Identifier, List<Identifier>>> layerPowerAddMap;
        public final HashMap<Identifier, HashMap<Identifier, List<Identifier>>> layerPowerRemoveMap;

        private Pair<List<Identifier>, List<Identifier>> parsePowerList(JsonObject jsonObject) {
            List<Identifier> allFormPowerAdd = new ArrayList<>();
            List<Identifier> allFormPowerRemove = new ArrayList<>();
            if (jsonObject.has("add")) {
                jsonObject.get("add").getAsJsonArray().forEach(jsonElement -> {
                    Identifier powerID = Identifier.tryParse(jsonElement.getAsString());
                    if (powerID != null) {
                        allFormPowerAdd.add(powerID);
                    }
                });
            }
            if (jsonObject.has("remove")) {
                jsonObject.get("remove").getAsJsonArray().forEach(jsonElement -> {
                    Identifier powerID = Identifier.tryParse(jsonElement.getAsString());
                    if (powerID != null) {
                        allFormPowerRemove.add(powerID);
                    }
                });
            }
            return new Pair<>(allFormPowerAdd, allFormPowerRemove);
        }

        public TrinketPowerData(JsonObject jsonObject) {
            if (jsonObject == null) {
                this.accessoryPowers = new ArrayList<>();
                this.allFormPowerAdd = new ArrayList<>();
                this.allFormPowerRemove = new ArrayList<>();
                this.formPowerAdd = new HashMap<>();
                this.formPowerRemove = new HashMap<>();
                this.layerPowerAddMap = new HashMap<>();
                this.layerPowerRemoveMap = new HashMap<>();
                return;
            }
            List<Identifier> accessoryPowers = new ArrayList<>();
            List<Identifier> allFormPowerAdd = new ArrayList<>();
            List<Identifier> allFormPowerRemove = new ArrayList<>();
            HashMap<Identifier, List<Identifier>> formPowerAdd = new HashMap<>();
            HashMap<Identifier, List<Identifier>> formPowerRemove = new HashMap<>();
            HashMap<Identifier, HashMap<Identifier, List<Identifier>>> layerPowerAddMap = new HashMap<>();
            HashMap<Identifier, HashMap<Identifier, List<Identifier>>> layerPowerRemoveMap = new HashMap<>();
            if (jsonObject.has("accessory_powers") && jsonObject.get("accessory_powers").isJsonArray()) {
                JsonArray accessoryPowerArray = jsonObject.get("accessory_powers").getAsJsonArray();
                accessoryPowerArray.forEach(jsonElement -> {
                    Identifier powerID = Identifier.tryParse(jsonElement.getAsString());
                    if (powerID != null) {
                        accessoryPowers.add(powerID);
                    }
                });
            }
            if (jsonObject.has("all_form") && jsonObject.get("all_form").isJsonObject()) {
                Pair<List<Identifier>, List<Identifier>> allFormPowerList = parsePowerList(jsonObject.get("all_form").getAsJsonObject());
                allFormPowerAdd = allFormPowerList.getLeft();
                allFormPowerRemove = allFormPowerList.getRight();
            }
            if (jsonObject.has("forms") && jsonObject.get("forms").isJsonObject()) {
                JsonObject formData = jsonObject.get("forms").getAsJsonObject();
                for (String formID : formData.keySet()) {
                    Identifier currentFormID = Identifier.tryParse(formID);
                    if (currentFormID == null) {
                        ShapeShifterCurseFabric.LOGGER.warn("Error On Parsing Trinket Power Data: Invalid Form ID: {}", formID);
                        continue;
                    }
                    JsonObject formPowerData = formData.get(formID).getAsJsonObject();
                    Pair<List<Identifier>, List<Identifier>> formPowerList = parsePowerList(formPowerData);
                    formPowerAdd.put(currentFormID, formPowerList.getLeft());
                    formPowerRemove.put(currentFormID, formPowerList.getRight());
                }
            }
            if (jsonObject.has("layers") && jsonObject.get("layers").isJsonObject()) {
                JsonObject layerGroupData = jsonObject.get("layers").getAsJsonObject();
                for (String layerGroupID : layerGroupData.keySet()) {
                    Identifier currentLayerGroupID = Identifier.tryParse(layerGroupID);
                    if (currentLayerGroupID == null) {
                        ShapeShifterCurseFabric.LOGGER.warn("Error On Parsing Trinket Power Data: Invalid Layer Group ID: {}", layerGroupID);
                    }
                    HashMap<Identifier, List<Identifier>> layerPowerAddMap2 = new HashMap<>();
                    HashMap<Identifier, List<Identifier>> layerPowerRemoveMap2 = new HashMap<>();
                    JsonObject layerData = layerGroupData.get(layerGroupID).getAsJsonObject();
                    for (String layerID : layerData.keySet()) {
                        Identifier currentLayerID = Identifier.tryParse(layerID);
                        if (currentLayerID == null) {
                            ShapeShifterCurseFabric.LOGGER.warn("Error On Parsing Trinket Power Data: Invalid Layer ID: {}", layerID);
                            continue;
                        }
                        JsonObject layerPowerData = layerData.get(layerID).getAsJsonObject();
                        Pair<List<Identifier>, List<Identifier>> layerPowerList = parsePowerList(layerPowerData);
                        layerPowerAddMap2.put(currentLayerID, layerPowerList.getLeft());
                        layerPowerRemoveMap2.put(currentLayerID, layerPowerList.getRight());
                    }
                    layerPowerAddMap.put(currentLayerGroupID, layerPowerAddMap2);
                    layerPowerRemoveMap.put(currentLayerGroupID, layerPowerRemoveMap2);
                }
            }

            this.accessoryPowers = accessoryPowers;
            this.allFormPowerAdd = allFormPowerAdd;
            this.allFormPowerRemove = allFormPowerRemove;
            this.formPowerAdd = formPowerAdd;
            this.formPowerRemove = formPowerRemove;
            this.layerPowerAddMap = layerPowerAddMap;
            this.layerPowerRemoveMap = layerPowerRemoveMap;
        }

        public TrinketPowerData Merge(TrinketPowerData other) {
            for (Identifier powerID : other.accessoryPowers) {
                if (!accessoryPowers.contains(powerID)) {
                    accessoryPowers.add(powerID);
                }
            }
            for (Identifier powerID : other.allFormPowerAdd) {
                if (!allFormPowerAdd.contains(powerID)) {
                    allFormPowerAdd.add(powerID);
                }
            }
            for (Identifier powerID : other.allFormPowerRemove) {
                if (!allFormPowerRemove.contains(powerID)) {
                    allFormPowerRemove.add(powerID);
                }
            }
            for (Identifier formID : other.formPowerAdd.keySet()) {
                List<Identifier> selfAddPowerList = formPowerAdd.computeIfAbsent(formID, k -> new ArrayList<>());
                List<Identifier> addPowerList = other.formPowerAdd.get(formID);
                for (Identifier powerID : addPowerList) {
                    if (!selfAddPowerList.contains(powerID)) {
                        selfAddPowerList.add(powerID);
                    }
                }
            }
            for (Identifier formID : other.formPowerRemove.keySet()) {
                List<Identifier> selfRemovePowerList = this.formPowerRemove.computeIfAbsent(formID, k -> new ArrayList<>());
                List<Identifier> removePowerList = other.formPowerRemove.get(formID);
                for (Identifier powerID : removePowerList) {
                    if (!selfRemovePowerList.contains(powerID)) {
                        selfRemovePowerList.add(powerID);
                    }
                }
            }
            for (Identifier layerGroupID : other.layerPowerAddMap.keySet()) {
                HashMap<Identifier, List<Identifier>> selfLayerGroupPowerAddMap = this.layerPowerAddMap.computeIfAbsent(layerGroupID, k -> new HashMap<>());
                HashMap<Identifier, List<Identifier>> layerPowerAddMap = other.layerPowerAddMap.get(layerGroupID);
                for (Identifier layerID : layerPowerAddMap.keySet()) {
                    List<Identifier> selfLayerPowerAddList = selfLayerGroupPowerAddMap.computeIfAbsent(layerID, k -> new ArrayList<>());
                    List<Identifier> layerPowerAddList = layerPowerAddMap.get(layerID);
                    for (Identifier powerID : layerPowerAddList) {
                        if (!selfLayerPowerAddList.contains(powerID)) {
                            selfLayerPowerAddList.add(powerID);
                        }
                    }
                }
            }
            for (Identifier layerGroupID : other.layerPowerRemoveMap.keySet()) {
                HashMap<Identifier, List<Identifier>> selfLayerGroupPowerRemoveMap = this.layerPowerRemoveMap.computeIfAbsent(layerGroupID, k -> new HashMap<>());
                HashMap<Identifier, List<Identifier>> layerPowerRemoveMap = other.layerPowerRemoveMap.get(layerGroupID);
                for (Identifier layerID : layerPowerRemoveMap.keySet()) {
                    List<Identifier> selfLayerPowerRemoveList = selfLayerGroupPowerRemoveMap.computeIfAbsent(layerID, k -> new ArrayList<>());
                    List<Identifier> layerPowerRemoveList = layerPowerRemoveMap.get(layerID);
                    for (Identifier powerID : layerPowerRemoveList) {
                        if (!selfLayerPowerRemoveList.contains(powerID)) {
                            selfLayerPowerRemoveList.add(powerID);
                        }
                    }
                }
            }
            return this;
        }

        private void AddPower(PlayerEntity player, Identifier powerID, Identifier sourceID) {
            PowerType<?> powerType = PowerTypeRegistry.get(powerID);
            if (powerType != null) {
                PowerHolderComponent powerHolder = PowerHolderComponent.KEY.get(player);
                powerHolder.addPower(powerType, sourceID);
            }
        }

        private void RemovePower(PlayerEntity player, Identifier powerID, Identifier sourceID) {
            PowerType<?> powerType = PowerTypeRegistry.get(powerID);
            if (powerType != null) {
                PowerHolderComponent powerHolder = PowerHolderComponent.KEY.get(player);
                powerHolder.removePower(powerType, sourceID);
            }
        }

        public void onPlayerFormChangeReApply(PlayerEntity player) {
            IForm form = FormUtils.getPlayerForm(player);
            Pair<Identifier, Identifier> currentFormLayer = form.getFormLayer();
            Identifier currentFormID = form.getFormID();
            Identifier currentOriginsID = currentFormLayer.getRight();
            for (Identifier powerID : allFormPowerAdd) {
                this.AddPower(player, powerID, currentOriginsID);
            }
            for (Identifier powerID : allFormPowerRemove) {
                this.RemovePower(player, powerID, currentOriginsID);
            }
            List<Identifier> formPowerAddList = formPowerAdd.get(currentFormID);
            List<Identifier> formPowerRemoveList = formPowerRemove.get(currentFormID);
            List<Identifier> layerGroupPowerAddList = layerPowerAddMap.getOrDefault(currentFormLayer.getLeft(), new HashMap<>()).get(currentFormLayer.getRight());
            List<Identifier> layerGroupPowerRemoveList = layerPowerRemoveMap.getOrDefault(currentFormLayer.getLeft(), new HashMap<>()).get(currentFormLayer.getRight());
            if (formPowerAddList != null) {
                for (Identifier powerID : formPowerAddList) {
                    this.AddPower(player, powerID, currentOriginsID);
                }
            }
            if (layerGroupPowerAddList != null) {
                for (Identifier powerID : layerGroupPowerAddList) {
                    this.AddPower(player, powerID, currentOriginsID);
                }
            }
            if (formPowerRemoveList != null) {
                for (Identifier powerID : formPowerRemoveList) {
                    this.RemovePower(player, powerID, currentOriginsID);
                }
            }
            if (layerGroupPowerRemoveList != null) {
                for (Identifier powerID : layerGroupPowerRemoveList) {
                    this.RemovePower(player, powerID, currentOriginsID);
                }
            }
        }

        public void onPlayerEquip(PlayerEntity player, Identifier itemID) {
            for (Identifier powerID : accessoryPowers) {
                this.AddPower(player, powerID, itemID);
            }
            this.onPlayerFormChangeReApply(player);
        }

        public void onPlayerUnEquip(PlayerEntity player, Identifier itemID) {
            IForm form = FormUtils.getPlayerForm(player);
            Pair<Identifier, Identifier> currentFormLayer = form.getFormLayer();
            Identifier currentFormID = form.getFormID();
            Identifier currentOriginsID = currentFormLayer.getRight();
            for (Identifier powerID : accessoryPowers) {
                this.RemovePower(player, powerID, itemID);
            }
            for (Identifier powerID : allFormPowerAdd) {
                this.RemovePower(player, powerID, currentOriginsID);
            }
            for (Identifier powerID : allFormPowerRemove) {
                this.AddPower(player, powerID, currentOriginsID);
            }
            List<Identifier> formPowerAddList = formPowerAdd.get(currentFormID);
            List<Identifier> formPowerRemoveList = formPowerRemove.get(currentFormID);
            List<Identifier> layerGroupPowerAddList = layerPowerAddMap.getOrDefault(currentFormLayer.getLeft(), new HashMap<>()).get(currentFormLayer.getRight());
            List<Identifier> layerGroupPowerRemoveList = layerPowerRemoveMap.getOrDefault(currentFormLayer.getLeft(), new HashMap<>()).get(currentFormLayer.getRight());
            if (formPowerAddList != null) {
                for (Identifier powerID : formPowerAddList) {
                    this.RemovePower(player, powerID, currentOriginsID);
                }
            }
            if (layerGroupPowerAddList != null) {
                for (Identifier powerID : layerGroupPowerAddList) {
                    this.RemovePower(player, powerID, currentOriginsID);
                }
            }
            if (formPowerRemoveList != null) {
                for (Identifier powerID : formPowerRemoveList) {
                    this.AddPower(player, powerID, currentOriginsID);
                }
            }
            if (layerGroupPowerRemoveList != null) {
                for (Identifier powerID : layerGroupPowerRemoveList) {
                    this.AddPower(player, powerID, currentOriginsID);
                }
            }
        }
    }

    public static final HashMap<Identifier, TrinketPowerData> accessoryPowerRegistry = new HashMap<>();
    private static final HashMap<Identifier, Boolean> accessoryMixinAutoRegistry = new HashMap<>();

    public static void registerAccessoryPower(Identifier itemIdentifier, TrinketPowerData powerData) {
        if (accessoryPowerRegistry.containsKey(itemIdentifier)) {
            accessoryPowerRegistry.get(itemIdentifier).Merge(powerData);
        } else {
            accessoryPowerRegistry.put(itemIdentifier, powerData);
        }
    }

    public static void clearAccessoryPower() {
        accessoryPowerRegistry.clear();
    }

    public static void registerAccessoryMixinAuto(Identifier itemIdentifier, boolean auto) {
        accessoryMixinAutoRegistry.put(itemIdentifier, auto);
    }

    public static @Nullable TrinketPowerData getAccessoryPower(Identifier itemIdentifier) {
        return accessoryPowerRegistry.get(itemIdentifier);
    }

    public static boolean getAccessoryMixinAuto(Identifier itemIdentifier) {
        return accessoryMixinAutoRegistry.getOrDefault(itemIdentifier, true);
    }

    public static void ApplyAccessoryPowerOnPlayerFormChange(PlayerEntity player, Identifier accessoryID) {
        if (player.getWorld().isClient) {
            return;  // 仅在服务器端执行
        }
        TrinketPowerData powerData = getAccessoryPower(accessoryID);
        if (powerData == null) {
            return;
        }
        powerData.onPlayerFormChangeReApply(player);
    }

    public static List<Pair<AccessoryItem.SlotData, ItemStack>> getAllAccessory(PlayerEntity player) {
        List<Pair<AccessoryItem.SlotData, ItemStack>> allAccessory = new ArrayList<>();

        for (Map.Entry<String, AccessoryUtils.AccessoryIO> accessoryReg : AccessoryUtils.activeAccessoryModInterfaces.entrySet()) {
            String ioName = accessoryReg.getKey();
            AccessoryUtils.AccessoryIO accessoryIO = accessoryReg.getValue();
            @Nullable Map<Pair<@Nullable String, String>, List<ItemStack>> allSlots = accessoryIO.getEntitySlots(player);
            if (allSlots != null) {
                for (Map.Entry<Pair<@Nullable String, String>, List<ItemStack>> entry : allSlots.entrySet()) {
                    Pair<@Nullable String, String> slotPair = entry.getKey();
                    List<ItemStack> stacks = entry.getValue();
                    int Index = 0;
                    for (ItemStack stack : stacks) {
                        if (stack.getItem() instanceof AccessoryItem && accessoryIO != AccessoryUtils.nowAccessoryMod) {
                            continue;
                        }
                        AccessoryItem.SlotData data = null;
                        if (slotPair.getLeft() == null) {
                            data = new AccessoryItem.SlotData(new Identifier(ioName, slotPair.getRight()), Index);
                        } else {
                            data = new AccessoryItem.SlotData(new Identifier(ioName, "%s/%s".formatted(slotPair.getLeft(), slotPair.getRight())), Index);
                        }
                        allAccessory.add(new Pair<>(data, stack));
                        Index++;
                    }
                }
            }
        }
        return allAccessory;
    }

    // 适配新架构的函数 等完工后再改 现在为了不影响正在使用的功能 先注释掉
    public static void ReApplyAccessoryPowerOnPlayerFormChange(PlayerEntity player) {
        List<Pair<AccessoryItem.SlotData, ItemStack>> allAccessory = getAllAccessory(player);
        for (Pair<AccessoryItem.SlotData, ItemStack> accessoryPair : allAccessory) {
            AccessoryItem.SlotData slot = accessoryPair.getLeft();
            ItemStack stack = accessoryPair.getRight();
            if (stack.getItem() instanceof CustomPowerTrinketInterface cpti) {
                cpti.onFormChange(stack, slot, player);
            } else {
                ApplyAccessoryPowerOnPlayerFormChange(player, Registries.ITEM.getId(stack.getItem()));
            }
        }
    }

    // public static void ReApplyAccessoryPowerOnPlayerFormChange(PlayerEntity player) {
    //     Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
    //     if (component.isEmpty()) {
    //         return;
    //     }
    //     for (Pair<SlotReference, ItemStack> accessoryPair : component.get().getAllEquipped()) {
    //         SlotReference slot = accessoryPair.getLeft();
    //         ItemStack stack = accessoryPair.getRight();
    //         if (stack.getItem() instanceof CustomPowerTrinketInterface cpti) {
    //             cpti.onFormChange(stack, slot, player);
    //         } else {
    //             ApplyAccessoryPowerOnPlayerFormChange(player, Registries.ITEM.getId(stack.getItem()));
    //         }
    //     }
    // }

    public static void ApplyAccessoryPowerOnEquip(PlayerEntity player, Identifier accessoryID) {
        if (player.getWorld().isClient) {
            return;  // 仅在服务器端执行
        }
        TrinketPowerData powerData = getAccessoryPower(accessoryID);
        if (powerData == null) {
            return;
        }
        powerData.onPlayerEquip(player, accessoryID);
    }

    public static void ApplyAccessoryPowerOnUnEquip(PlayerEntity player, Identifier accessoryID) {
        if (player.getWorld().isClient) {
            return;  // 仅在服务器端执行
        }
        TrinketPowerData powerData = getAccessoryPower(accessoryID);
        if (powerData == null) {
            return;
        }
        powerData.onPlayerUnEquip(player, accessoryID);
    }

    public static void loadAccessoryPowerData(JsonObject jsonObject) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String ItemIDRaw = entry.getKey();
            Identifier itemID = Identifier.tryParse(ItemIDRaw);
            if (itemID == null) {
                continue;
            }
            JsonElement jsonElement = entry.getValue();
            if (!jsonElement.isJsonObject()) {
                continue;
            }
            JsonObject jsonPowerData = jsonElement.getAsJsonObject();
            TrinketPowerData powerData = new TrinketPowerData(jsonPowerData);
            registerAccessoryPower(itemID, powerData);
        }
    }
}
