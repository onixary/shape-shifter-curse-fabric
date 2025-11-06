package net.onixary.shapeShifterCurseFabric.util;

import com.mojang.datafixers.util.Pair;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.additional_power.CustomEdiblePower;

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

    public static void ReloadPlayerCustomEdible(PlayerEntity user) {
        try {
            customEdibleMap.computeIfAbsent(user.getUuid(), k -> new HashMap<>()).clear();
            HashMap<Identifier, FoodComponent> customEdible = customEdibleMap.get(user.getUuid());
            PowerHolderComponent.getPowers(user, CustomEdiblePower.class).forEach(
                    customEdiblePower -> {
                        for (Identifier itemId : customEdiblePower.getItemIdList()) {
                            customEdible.put(itemId, customEdiblePower.getFoodComponent());
                        }
                    }
            );
        } catch (Exception e) {
            // ShapeShifterCurseFabric.LOGGER.error("Reload Player Custom Edible Failed", e);
        }
    }

    public static void WriteFoodComponent(PacketByteBuf buff, FoodComponent foodComponent) {
        buff.writeInt(foodComponent.getHunger());
        buff.writeFloat(foodComponent.getSaturationModifier());
        buff.writeBoolean(foodComponent.isMeat());
        buff.writeBoolean(foodComponent.isAlwaysEdible());
        buff.writeBoolean(foodComponent.isSnack());
        NbtList statusEffects = new NbtList();
        for (Pair<StatusEffectInstance, Float> statusEffectPair : foodComponent.getStatusEffects()) {
            NbtCompound statusEffect = new NbtCompound();
            statusEffect.putFloat("chance", statusEffectPair.getSecond());
            statusEffect.put("effect", statusEffectPair.getFirst().writeNbt(new NbtCompound()));
            statusEffects.add(statusEffect);
        }
        NbtCompound nbt = new NbtCompound();
        nbt.put("statusEffects", statusEffects);
        buff.writeNbt(nbt);
    }

    public static FoodComponent ReadFoodComponent(PacketByteBuf buff) {
        int Hunger = buff.readInt();
        float Saturation = buff.readFloat();
        boolean Meat = buff.readBoolean();
        boolean AlwaysEdible = buff.readBoolean();
        boolean Snack = buff.readBoolean();
        NbtCompound nbt = buff.readNbt();
        NbtList statusEffects = nbt.getList("statusEffects", 10);
        List<Pair<StatusEffectInstance, Float>> effects = statusEffects.stream().map(
                nbt1 -> {
                    NbtCompound statusEffect = (NbtCompound) nbt1;
                    return Pair.of(StatusEffectInstance.fromNbt(statusEffect.getCompound("effect")), statusEffect.getFloat("chance"));
                }
        ).toList();
        FoodComponent.Builder builder = new FoodComponent.Builder();
        builder.hunger(Hunger).saturationModifier(Saturation);
        if (Meat) builder.meat();
        if (AlwaysEdible) builder.alwaysEdible();
        if (Snack) builder.snack();
        for (Pair<StatusEffectInstance, Float> effect : effects) {
            builder.statusEffect(effect.getFirst(), effect.getSecond());
        }
        return builder.build();
    }
}
