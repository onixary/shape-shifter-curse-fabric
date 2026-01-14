package net.onixary.shapeShifterCurseFabric.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrinketUtils {
    public interface CustomPowerTrinketInterface {
        void onFormChange(ItemStack stack, SlotReference slot, PlayerEntity entity);
    }

    public static class TrinketPowerData {
        public final List<Identifier> accessoryPowers;
        public final List<Identifier> allFormPowerAdd;
        public final List<Identifier> allFormPowerRemove;
        public final HashMap<Identifier, List<Identifier>> formPowerAdd;
        public final HashMap<Identifier, List<Identifier>> formPowerRemove;

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

        public TrinketPowerData(List<Identifier> accessoryPowers, List<Identifier> allFormPowerAdd, List<Identifier> allFormPowerRemove, HashMap<Identifier, List<Identifier>> formPowerAdd, HashMap<Identifier, List<Identifier>> formPowerRemove) {
            this.accessoryPowers = accessoryPowers;
            this.allFormPowerAdd = allFormPowerAdd;
            this.allFormPowerRemove = allFormPowerRemove;
            this.formPowerAdd = formPowerAdd;
            this.formPowerRemove = formPowerRemove;
        }

        public TrinketPowerData(JsonObject jsonObject) {
            List<Identifier> accessoryPowers = new ArrayList<>();
            List<Identifier> allFormPowerAdd = new ArrayList<>();
            List<Identifier> allFormPowerRemove = new ArrayList<>();
            HashMap<Identifier, List<Identifier>> formPowerAdd = new HashMap<>();
            HashMap<Identifier, List<Identifier>> formPowerRemove = new HashMap<>();
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
            this.accessoryPowers = accessoryPowers;
            this.allFormPowerAdd = allFormPowerAdd;
            this.allFormPowerRemove = allFormPowerRemove;
            this.formPowerAdd = formPowerAdd;
            this.formPowerRemove = formPowerRemove;
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
            Identifier currentFormID = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().FormID;
            Identifier currentOriginsID = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().getFormOriginID();
            for (Identifier powerID : allFormPowerAdd) {
                this.AddPower(player, powerID, currentOriginsID);
            }
            for (Identifier powerID : allFormPowerRemove) {
                this.RemovePower(player, powerID, currentOriginsID);
            }
            List<Identifier> formPowerAddList = formPowerAdd.get(currentFormID);
            List<Identifier> formPowerRemoveList = formPowerRemove.get(currentFormID);
            if (formPowerAddList != null) {
                for (Identifier powerID : formPowerAddList) {
                    this.AddPower(player, powerID, currentOriginsID);
                }
            }
            if (formPowerRemoveList != null) {
                for (Identifier powerID : formPowerRemoveList) {
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
            Identifier currentFormID = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().FormID;
            Identifier currentOriginsID = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm().getFormOriginID();
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
            if (formPowerAddList != null) {
                for (Identifier powerID : formPowerAddList) {
                    this.RemovePower(player, powerID, currentOriginsID);
                }
            }
            if (formPowerRemoveList != null) {
                for (Identifier powerID : formPowerRemoveList) {
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

    public static void ReApplyAccessoryPowerOnPlayerFormChange(PlayerEntity player) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        if (component.isEmpty()) {
            return;
        }
        for (Pair<SlotReference, ItemStack> accessoryPair : component.get().getAllEquipped()) {
            SlotReference slot = accessoryPair.getLeft();
            ItemStack stack = accessoryPair.getRight();
            if (stack.getItem() instanceof CustomPowerTrinketInterface cpti) {
                cpti.onFormChange(stack, slot, player);
            } else {
                ApplyAccessoryPowerOnPlayerFormChange(player, Registries.ITEM.getId(stack.getItem()));
            }
        }
    }

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
