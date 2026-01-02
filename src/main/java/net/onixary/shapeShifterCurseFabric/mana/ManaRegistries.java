package net.onixary.shapeShifterCurseFabric.mana;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class ManaRegistries {
    // 不可变 防止某些天才操作同时把其他的ManaHandler给修改了 导致程序异常 当然Immutable为可选项 无论是否Immutable都支持注册 推荐为公共ManaHandler加上setImmutable
    public static final ManaHandler EMPTY_MANA_HANDLER = new ManaHandler().setImmutable();
    public static final ManaHandler DEBUG_MANA_HANDLER = new ManaHandler()
            // 所有Hook执行时间不保证在同一Tick
            .setOnClientManaFull((component, player) -> {
                player.sendMessage(Text.literal("[Client] 魔力值已满!").formatted(Formatting.GREEN));
            })
            .setOnClientManaEmpty((component, player) -> {
                player.sendMessage(Text.literal("[Client] 魔力值已空!").formatted(Formatting.RED));
            })
            .setOnServerManaFull((component, player) -> {
                player.sendMessage(Text.literal("[Server] 魔力值已满!").formatted(Formatting.GREEN));
            })
            .setOnServerManaEmpty((component, player) -> {
                player.sendMessage(Text.literal("[Server] 魔力值已空!").formatted(Formatting.RED));
            })
            .setImmutable();

    private static final HashMap<Identifier, Function<PlayerEntity, Boolean>> manaConditionTypeRegistry = new HashMap<>();
    private static final HashMap<Identifier, ManaUtils.ModifierList> maxManaModifierRegistry = new HashMap<>();
    private static final HashMap<Identifier, ManaUtils.ModifierList> manaReginModifierRegistry = new HashMap<>();
    private static final HashMap<Identifier, ManaHandler> manaHandlerRegistry = new HashMap<>();

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
            EMPTY_MANA_HANDLER
    );

    public static Identifier registerManaType(Identifier identifier, ManaUtils.ModifierList defaultMaxManaModifier, ManaUtils.ModifierList defaultManaRegenModifier, @Nullable ManaHandler handler) {
        if (defaultManaRegenModifier == null) {
            defaultManaRegenModifier = EMPTY_MANA_REGEN_MODIFIER;
        }
        if (defaultMaxManaModifier == null) {
            defaultMaxManaModifier = EMPTY_MAX_MANA_MODIFIER;
        }
        maxManaModifierRegistry.put(identifier, defaultMaxManaModifier);
        manaReginModifierRegistry.put(identifier, defaultManaRegenModifier);
        if (handler != null) {
            manaHandlerRegistry.put(identifier, handler);
        }
        return identifier;
    }


    public static Identifier registerManaConditionType(Identifier identifier, Function<PlayerEntity, Boolean> condition) {
        manaConditionTypeRegistry.put(identifier, condition);
        return identifier;
    }

    public static void register() {}

    public static @NotNull ManaUtils.ModifierList getMaxManaModifier(@Nullable Identifier identifier) {
        return maxManaModifierRegistry.getOrDefault(identifier, EMPTY_MAX_MANA_MODIFIER).copy();
    }

    public static @NotNull ManaUtils.ModifierList getManaRegenModifier(@Nullable Identifier identifier) {
        return manaReginModifierRegistry.getOrDefault(identifier, EMPTY_MANA_REGEN_MODIFIER).copy();
    }

    public static @Nullable ManaHandler getManaHandler(@Nullable Identifier identifier) {
        return manaHandlerRegistry.get(identifier);
    }

    public static @NotNull ManaHandler getManaHandlerOrDefault(@Nullable Identifier identifier) {
        return getManaHandlerOrDefault(identifier, EMPTY_MANA_HANDLER);
    }

    public static @NotNull ManaHandler getManaHandlerOrDefault(@Nullable Identifier identifier, @NotNull ManaHandler defaultHandler) {
        return manaHandlerRegistry.getOrDefault(identifier, defaultHandler);
    }

    public static boolean ManaConditionCheck(@Nullable Identifier identifier, PlayerEntity player) {
        return manaConditionTypeRegistry.getOrDefault(identifier, (p) -> false).apply(player);
    }
}
