package net.onixary.shapeShifterCurseFabric.mana;

import com.google.gson.JsonObject;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ManaUtils {
    public record Modifier(double add, double multiply, double add_total) {
        public double applyAdd(double mana) {
            return mana + add;
        }

        public double applyMultiply(double mana) {
            return mana * multiply;
        }

        public double applyAddTotal(double mana) {
            return mana + add_total;
        }

        public static Modifier of(@Nullable Double add, @Nullable Double multiply, @Nullable Double add_total) {
            return new Modifier(add == null ? 0.0d : add, multiply == null ? 1.0d : multiply, add_total == null ? 0.0d : add_total);
        }

        public static Modifier readFromNbt(NbtCompound nbtCompound) {
            return of(nbtCompound.getDouble("add"), nbtCompound.getDouble("multiply"), nbtCompound.getDouble("add_total"));
        }

        public void writeToNbt(NbtCompound nbtCompound) {
            nbtCompound.putDouble("add", add);
            nbtCompound.putDouble("multiply", multiply);
            nbtCompound.putDouble("add_total", add_total);
        }
    }

    public static final SerializableDataType<Modifier> SDT_ManaModifier = SerializableDataType.compound(
            Modifier.class,
            new SerializableData()
                    .add("add", SerializableDataTypes.DOUBLE, 0.0d)
                    .add("multiply", SerializableDataTypes.DOUBLE, 1.0d)
                    .add("add_total", SerializableDataTypes.DOUBLE, 0.0d),
            (serializableData) -> Modifier.of(serializableData.get("add"), serializableData.get("multiply"), serializableData.get("add_total")),
            (serializableData, modifier) -> {
                JsonObject jsonData = new JsonObject();
                jsonData.addProperty("add", modifier.add);
                jsonData.addProperty("multiply", modifier.multiply);
                jsonData.addProperty("add_total", modifier.add_total);
                return serializableData.read(jsonData);
            }
    );

    public static final SerializableDataType<List<Modifier>> SDT_ManaModifierList = SerializableDataType.list(SDT_ManaModifier);

    public static class ModifierList {
        public double lastValue = 0.0d;
        public boolean needSync = false;
        private final LinkedHashMap<Identifier, Pair<Identifier, Modifier>> modifiers;

        @SafeVarargs
        public ModifierList(Pair<Identifier, Pair<Identifier, Modifier>>... modifier) {
            modifiers = new LinkedHashMap<>();
            if (modifier != null) {
                for (Pair<Identifier, Pair<Identifier, Modifier>> modifierEntry : modifier) {
                    modifiers.put(modifierEntry.getLeft(), modifierEntry.getRight());
                }
            }
        }

        public ModifierList(ModifierList other) {
            modifiers = new LinkedHashMap<>(other.getModifiers());
        }

        public LinkedHashMap<Identifier, Pair<Identifier, Modifier>> getModifiers() {
            return modifiers;
        }

        public ModifierList copy() {
            return new ModifierList(this);
        }

        public void add(Identifier identifier, Identifier conditionID, Modifier modifier) {
            modifiers.put(identifier, new Pair<>(conditionID, modifier));
        }

        public void remove(Identifier identifier) {
            modifiers.remove(identifier);
        }

        private double applyAdd(PlayerEntity player, double value) {
            for(Map.Entry<Identifier, Pair<Identifier, Modifier>> modifierEntry : modifiers.entrySet()) {
                Identifier conditionID = modifierEntry.getValue().getLeft();
                if(ManaRegistries.ManaConditionCheck(conditionID, player)) {
                    value = modifierEntry.getValue().getRight().applyAdd(value);
                }
            }
            return value;
        }

        private double applyMultiply(PlayerEntity player, double value) {
            for(Map.Entry<Identifier, Pair<Identifier, Modifier>> modifierEntry : modifiers.entrySet()) {
                Identifier conditionID = modifierEntry.getValue().getLeft();
                if(ManaRegistries.ManaConditionCheck(conditionID, player)) {
                    value = modifierEntry.getValue().getRight().applyMultiply(value);
                }
            }
            return value;
        }

        private double applyAddTotal(PlayerEntity player, double value) {
            for(Map.Entry<Identifier, Pair<Identifier, Modifier>> modifierEntry : modifiers.entrySet()) {
                Identifier conditionID = modifierEntry.getValue().getLeft();
                if(ManaRegistries.ManaConditionCheck(conditionID, player)) {
                    value = modifierEntry.getValue().getRight().applyAddTotal(value);
                }
            }
            return value;
        }

        public double apply(PlayerEntity player, double value, ModifierList... otherModifiers) {
            value = this.applyAdd(player, value);
            for (ModifierList otherModifier : otherModifiers) {
                value = otherModifier.applyAdd(player, value);
            }
            value = this.applyMultiply(player, value);
            for (ModifierList otherModifier : otherModifiers) {
                value = otherModifier.applyMultiply(player, value);
            }
            value = this.applyAddTotal(player, value);
            for (ModifierList otherModifier : otherModifiers) {
                value = otherModifier.applyAddTotal(player, value);
            }
            if (value != lastValue) {
                needSync = true;
            }
            lastValue = value;
            return value;
        }

        public void clear() {
            lastValue = 0.0d;
            needSync = false;
            modifiers.clear();
        }

        public void readFromNbt(NbtCompound nbtCompound) {
            modifiers.clear();
            if (nbtCompound.contains("modifiers")) {
                NbtList nbtList = nbtCompound.getList("modifiers", NbtElement.COMPOUND_TYPE);
                for (NbtElement nbtElement : nbtList) {
                    NbtCompound modifierEntryNbt = (NbtCompound) nbtElement;
                    Identifier identifier = new Identifier(modifierEntryNbt.getString("identifier"));
                    Identifier conditionID = new Identifier(modifierEntryNbt.getString("conditionID"));
                    Modifier modifier = Modifier.readFromNbt(modifierEntryNbt.getCompound("modifier"));
                    modifiers.put(identifier, new Pair<>(conditionID, modifier));
                }
            }
        }

        public void writeToNbt(NbtCompound nbtCompound) {
            NbtList nbtList = new NbtList();
            for (Map.Entry<Identifier, Pair<Identifier, Modifier>> modifierEntry : modifiers.entrySet()) {
                NbtCompound modifierEntryNbt = new NbtCompound();
                modifierEntryNbt.putString("identifier", modifierEntry.getKey().toString());
                modifierEntryNbt.putString("conditionID", modifierEntry.getValue().getLeft().toString());
                NbtCompound modifierNbt = new NbtCompound();
                modifierEntry.getValue().getRight().writeToNbt(modifierNbt);
                modifierEntryNbt.put("modifier", modifierNbt);
                nbtList.add(modifierEntryNbt);
            }
            nbtCompound.put("modifiers", nbtList);
        }
    }

    public static ManaComponent getManaComponent(PlayerEntity player) {
        return RegManaComponent.MANA.get(player);
    }

    public static void addMaxManaModifier(PlayerEntity player, Identifier identifier, Identifier conditionID, Modifier modifier, boolean playerSide) {
        if (playerSide) {
            getManaComponent(player).MaxManaModifierPlayerSide.add(identifier, conditionID, modifier);
        } else {
            getManaComponent(player).MaxManaModifier.add(identifier, conditionID, modifier);
        }
    }

    public static void addMaxManaModifier(PlayerEntity player, Identifier identifier, Modifier modifier, boolean playerSide) {
        addMaxManaModifier(player, identifier, ManaRegistries.MC_AlwaysTrue, modifier, playerSide);
    }

    public static void removeMaxManaModifier(PlayerEntity player, Identifier identifier, boolean playerSide) {
        if (playerSide) {
            getManaComponent(player).MaxManaModifierPlayerSide.remove(identifier);
        } else {
            getManaComponent(player).MaxManaModifier.remove(identifier);
        }
    }

    public static void addRegenManaModifier(PlayerEntity player, Identifier identifier, Identifier conditionID, Modifier modifier, boolean playerSide) {
        if (playerSide) {
            getManaComponent(player).ManaRegenModifierPlayerSide.add(identifier, conditionID, modifier);
        } else {
            getManaComponent(player).ManaRegenModifier.add(identifier, conditionID, modifier);
        }
    }

    public static void addRegenManaModifier(PlayerEntity player, Identifier identifier, Modifier modifier, boolean playerSide) {
        addRegenManaModifier(player, identifier, ManaRegistries.MC_AlwaysTrue, modifier, playerSide);
    }

    public static void removeRegenManaModifier(PlayerEntity player, Identifier identifier, boolean playerSide) {
        if (playerSide) {
            getManaComponent(player).ManaRegenModifierPlayerSide.remove(identifier);
        } else {
            getManaComponent(player).ManaRegenModifier.remove(identifier);
        }
    }

    public static double getPlayerMana(PlayerEntity player) {
        return getManaComponent(player).getMana();
    }

    public static double getPlayerManaPercent(PlayerEntity player) {
        ManaComponent manaComponent = getManaComponent(player);
        // 突然想起来，如果maxMana为0，那么会导致除0错误。
        double maxMana = manaComponent.getMaxMana();
        if (maxMana == 0.0d) {
            return 0.0d;
        }
        return manaComponent.getMana() / maxMana;
    }

    public static Identifier getPlayerManaTypeID(PlayerEntity player) {
        return getManaComponent(player).getManaTypeID();
    }

    public static double setPlayerMana(PlayerEntity player, double mana) {
        return getManaComponent(player).setMana(mana);
    }

    public static double gainPlayerMana(PlayerEntity player, double mana) {
        return getManaComponent(player).gainMana( mana);
    }

    public static double consumePlayerMana(PlayerEntity player, double mana) {
        return getManaComponent(player).consumeMana(mana);
    }

    public static void gainPlayerManaWithTime(PlayerEntity player, double mana, int time) {
        if (time <= 0) {
            getManaComponent(player).gainMana(mana * time);
        } else {
            getManaComponent(player).gainManaWithTime(mana, time);
        }
    }

    public static boolean isPlayerManaAbove(PlayerEntity player, double mana) {
        return getManaComponent(player).isManaAbove(mana);
    }

    // 用于Power系统
    public static void gainManaTypeID(PlayerEntity player, Identifier manaTypeID) {
        getManaComponent(player).gainManaTypeID(manaTypeID);
    }

    public static void loseManaTypeID(PlayerEntity player, Identifier manaTypeID) {
        getManaComponent(player).loseManaTypeID(manaTypeID);
    }

    // 用于其他非Power系统
    public static void setManaTypeID(PlayerEntity player, Identifier manaTypeID) {
        getManaComponent(player).setManaTypeID(manaTypeID);
    }

    public static void manaTick(PlayerEntity player) {
        ManaComponent manaComponent = getManaComponent(player);
        manaComponent.tick();
        if (!player.getWorld().isClient && manaComponent.isNeedSync()) {
            RegManaComponent.MANA.sync(player);
        }
    }
}
