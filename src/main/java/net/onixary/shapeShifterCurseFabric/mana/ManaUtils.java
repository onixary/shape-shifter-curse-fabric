package net.onixary.shapeShifterCurseFabric.mana;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

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

        public Modifier of(@Nullable Double add, @Nullable Double multiply, @Nullable Double add_total) {
            return new Modifier(add == null ? 0.0d : add, multiply == null ? 1.0d : multiply, add_total == null ? 0.0d : add_total);
        }
    }

    public static class ModifierList {
        public double lastValue = 0.0d;
        public boolean needSync = false;
        private final LinkedHashMap<Identifier, Pair<Function<PlayerEntity, Boolean>, Modifier>> modifiers;

        @SafeVarargs
        public ModifierList(Pair<Identifier, Pair<Function<PlayerEntity, Boolean>, Modifier>>... modifier) {
            modifiers = new LinkedHashMap<>();
            if (modifier != null) {
                for (Pair<Identifier, Pair<Function<PlayerEntity, Boolean>, Modifier>> modifierEntry : modifier) {
                    modifiers.put(modifierEntry.getLeft(), modifierEntry.getRight());
                }
            }
        }

        public ModifierList(ModifierList other) {
            modifiers = new LinkedHashMap<>(other.getModifiers());
        }

        public LinkedHashMap<Identifier, Pair<Function<PlayerEntity, Boolean>, Modifier>> getModifiers() {
            return modifiers;
        }

        public ModifierList copy() {
            return new ModifierList(this);
        }

        public void add(Identifier identifier, Function<PlayerEntity, Boolean> condition, Modifier modifier) {
            modifiers.put(identifier, new Pair<>(condition, modifier));
        }

        public double apply(PlayerEntity player, double value) {
            for(Map.Entry<Identifier, Pair<Function<PlayerEntity, Boolean>, Modifier>> modifierEntry : modifiers.entrySet()) {
                if(modifierEntry.getValue().getLeft().apply(player)) {
                    value = modifierEntry.getValue().getRight().applyAdd(value);
                }
            }
            for (Map.Entry<Identifier, Pair<Function<PlayerEntity, Boolean>, Modifier>> modifierEntry : modifiers.entrySet()) {
                if(modifierEntry.getValue().getLeft().apply(player)) {
                    value = modifierEntry.getValue().getRight().applyMultiply(value);
                }
            }
            for (Map.Entry<Identifier, Pair<Function<PlayerEntity, Boolean>, Modifier>> modifierEntry : modifiers.entrySet()) {
                if (modifierEntry.getValue().getLeft().apply(player)) {
                    value = modifierEntry.getValue().getRight().applyAddTotal(value);
                }
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
    }

    public static ManaComponent getManaComponent(PlayerEntity player) {
        return RegManaComponent.MANA.get(player);
    }

    public static void addMaxManaModifier(PlayerEntity player, Identifier identifier, Function<PlayerEntity, Boolean> condition, Modifier modifier) {
        getManaComponent(player).MaxManaModifier.add(identifier, condition, modifier);
    }

    public static void addRegenManaModifier(PlayerEntity player, Identifier identifier, Function<PlayerEntity, Boolean> condition, Modifier modifier) {
        getManaComponent(player).ManaRegenModifier.add(identifier, condition, modifier);
    }

    public static double getPlayerMana(PlayerEntity player) {
        return getManaComponent(player).getMana();
    }

    public static double getPlayerManaPercent(PlayerEntity player) {
        ManaComponent manaComponent = getManaComponent(player);
        return manaComponent.getMana() / manaComponent.getMaxMana(player);
    }

    public static Identifier getPlayerManaTypeID(PlayerEntity player) {
        return getManaComponent(player).getManaTypeID();
    }

    public static double setPlayerMana(PlayerEntity player, double mana) {
        return getManaComponent(player).setMana(player, mana);
    }

    public static double gainPlayerMana(PlayerEntity player, double mana) {
        return getManaComponent(player).gainMana(player, mana);
    }

    public static double consumePlayerMana(PlayerEntity player, double mana) {
        return getManaComponent(player).consumeMana(player, mana);
    }

    public static void gainPlayerManaWithTime(PlayerEntity player, double mana, int time) {
        if (time <= 0) {
            getManaComponent(player).gainMana(player, mana * time);
        } else {
            getManaComponent(player).gainManaWithTime(player, mana, time);
        }
    }

    public static boolean isPlayerManaAbove(PlayerEntity player, double mana) {
        return getManaComponent(player).isManaAbove(player, mana);
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
        manaComponent.tick(player);
        if (!player.getWorld().isClient && manaComponent.isNeedSync()) {
            RegManaComponent.MANA.sync(player);
        }
    }
}
