package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import net.minecraft.entity.player.PlayerEntity;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.DEBUG_UUID;
import static net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage.loadPlayerInstinctComponent;
import static net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage.savePlayerInstinctComponent;

public class InstinctManager {
    // 添加立即效果
    public static void applyImmediateEffect(PlayerEntity player, InstinctEffectType effect) {
        if (!effect.isSustained()) {
            PlayerInstinctComponent comp = RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.get(player);
            comp.immediateEffects.add(effect);
        }
    }

    // 添加持续效果
    public static void applySustainedEffect(PlayerEntity player, InstinctEffectType effect) {
        if (effect.isSustained()) {
            PlayerInstinctComponent comp = RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.get(player);
            comp.sustainedEffects.add(effect);
        }
    }

    // 移除持续效果
    public static void removeSustainedEffect(PlayerEntity player, InstinctEffectType effect) {
        PlayerInstinctComponent comp = RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.get(player);
        comp.sustainedEffects.remove(effect);
    }

    public static void saveInstinctComp(PlayerEntity player) {
        PlayerInstinctComponent comp = RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.get(player);
        savePlayerInstinctComponent(DEBUG_UUID == null? player.getUuid().toString() : DEBUG_UUID, comp);
    }

    public static PlayerInstinctComponent loadInstinctComp(PlayerEntity player) {
        return loadPlayerInstinctComponent(DEBUG_UUID == null? player.getUuid().toString() : DEBUG_UUID);
    }
}
