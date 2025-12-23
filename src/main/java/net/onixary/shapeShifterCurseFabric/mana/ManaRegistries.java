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
    private static final HashMap<Identifier, ManaUtils.ModifierList> maxManaModifierRegistry = new HashMap<>();
    private static final HashMap<Identifier, ManaUtils.ModifierList> manaReginModifierRegistry = new HashMap<>();
    private static final HashMap<Identifier, IManaRender> manaRenderRegistry = new HashMap<>();

    public static final ManaUtils.ModifierList EMPTY_MAX_MANA_MODIFIER = new ManaUtils.ModifierList();
    public static final ManaUtils.ModifierList EMPTY_MANA_REGEN_MODIFIER = new ManaUtils.ModifierList();

    public static final Identifier FAMILIAR_FOX_MANA = registerManaType(ShapeShifterCurseFabric.identifier("familiar_fox_mana"),
            new ManaUtils.ModifierList(
                    new Pair<Identifier, Pair<Function<PlayerEntity, Boolean>, ManaUtils.Modifier>>(
                            ShapeShifterCurseFabric.identifier("base_value"),
                            new Pair<Function<PlayerEntity, Boolean>, ManaUtils.Modifier>(
                                    player -> true,
                                    new ManaUtils.Modifier(100d, 1.0d, 0d)
                            )
                    )
            ),
            new ManaUtils.ModifierList(
                    new Pair<Identifier, Pair<Function<PlayerEntity, Boolean>, ManaUtils.Modifier>>(
                            ShapeShifterCurseFabric.identifier("cursed_moon"),
                            new Pair<Function<PlayerEntity, Boolean>, ManaUtils.Modifier>(
                                    player -> {return CursedMoon.isCursedMoon(player.getWorld()) && CursedMoon.isNight();},
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
}
