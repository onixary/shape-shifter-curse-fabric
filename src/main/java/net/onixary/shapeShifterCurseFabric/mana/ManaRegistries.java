package net.onixary.shapeShifterCurseFabric.mana;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class ManaRegistries {
    private static final HashMap<Identifier, Function<PlayerEntity, Boolean>> manaConditionTypeRegistry = new HashMap<>();
    private static final HashMap<Identifier, ManaUtils.ModifierList> maxManaModifierRegistry = new HashMap<>();
    private static final HashMap<Identifier, ManaUtils.ModifierList> manaReginModifierRegistry = new HashMap<>();
    private static final HashMap<Identifier, IManaRender> manaRenderRegistry = new HashMap<>();

    public static final ManaUtils.ModifierList EMPTY_MAX_MANA_MODIFIER = new ManaUtils.ModifierList();
    public static final ManaUtils.ModifierList EMPTY_MANA_REGEN_MODIFIER = new ManaUtils.ModifierList();

    public static final Identifier MC_AlwaysTrue = registerManaConditionType(ShapeShifterCurseFabric.identifier("always_true"), player -> true);
    public static final Identifier MC_AlwaysFalse = registerManaConditionType(ShapeShifterCurseFabric.identifier("always_false"), player -> false);
    public static final Identifier MC_IsCursedMoon = registerManaConditionType(ShapeShifterCurseFabric.identifier("is_cursed_moon"), player -> CursedMoon.isCursedMoon(player.getWorld()) && CursedMoon.isNight());

    public static final Identifier FAMILIAR_FOX_MANA = registerManaType(ShapeShifterCurseFabric.identifier("familiar_fox_mana"),
            new ManaUtils.ModifierList(
                    new Pair<Identifier, Pair<Identifier, ManaUtils.Modifier>>(
                            ShapeShifterCurseFabric.identifier("base_value"),
                            new Pair<Identifier, ManaUtils.Modifier>(
                                    MC_AlwaysTrue,
                                    new ManaUtils.Modifier(100d, 1.0d, 0d)
                            )
                    )
            ),
            new ManaUtils.ModifierList(
                    new Pair<Identifier, Pair<Identifier, ManaUtils.Modifier>>(
                            ShapeShifterCurseFabric.identifier("cursed_moon"),
                            new Pair<Identifier, ManaUtils.Modifier>(
                                    MC_IsCursedMoon,
                                    new ManaUtils.Modifier(0.5d, 1.0d, 0d)
                            )
                    )
            ),
            new InstinctBarLikeManaBar()
    );

    public static Identifier registerManaType(Identifier identifier, ManaUtils.ModifierList defaultMaxManaModifier, ManaUtils.ModifierList defaultManaRegenModifier, @Nullable IManaRender render) {
        if (defaultManaRegenModifier == null) {
            defaultManaRegenModifier = EMPTY_MANA_REGEN_MODIFIER;
        }
        if (defaultMaxManaModifier == null) {
            defaultMaxManaModifier = EMPTY_MAX_MANA_MODIFIER;
        }
        maxManaModifierRegistry.put(identifier, defaultMaxManaModifier);
        manaReginModifierRegistry.put(identifier, defaultManaRegenModifier);
        if (render != null) {
            manaRenderRegistry.put(identifier, render);
        }
        return identifier;
    }

    public static Identifier registerManaConditionType(Identifier identifier, Function<PlayerEntity, Boolean> condition) {
        manaConditionTypeRegistry.put(identifier, condition);
        return identifier;
    }

    public static @NotNull ManaUtils.ModifierList getMaxManaModifier(@Nullable Identifier identifier) {
        return maxManaModifierRegistry.getOrDefault(identifier, EMPTY_MAX_MANA_MODIFIER).copy();
    }

    public static @NotNull ManaUtils.ModifierList getManaRegenModifier(@Nullable Identifier identifier) {
        return manaReginModifierRegistry.getOrDefault(identifier, EMPTY_MANA_REGEN_MODIFIER).copy();
    }

    public static boolean hasManaRender(@Nullable Identifier identifier) {
        return manaRenderRegistry.containsKey(identifier);
    }

    public static @Nullable IManaRender getManaRender(@Nullable Identifier identifier) {
        return manaRenderRegistry.get(identifier);
    }

    public static boolean ManaConditionCheck(@Nullable Identifier identifier, PlayerEntity player) {
        return manaConditionTypeRegistry.getOrDefault(identifier, (p) -> false).apply(player);
    }
}
