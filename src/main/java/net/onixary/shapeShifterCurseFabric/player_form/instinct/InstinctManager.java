package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import static net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage.loadPlayerInstinctComponent;
import static net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage.savePlayerInstinctComponent;

public class InstinctManager {
    private static ServerWorld world;

    public static void getServerWorld(ServerWorld world) {
        InstinctManager.world = world;
    }
    // 添加立即效果
    public static void applyImmediateEffect(PlayerEntity player, InstinctEffectType effect) {
        if (!effect.isSustained()) {
            PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
            comp.immediateEffects.add(effect);
            RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.sync(player);
        }
    }

    // 添加持续效果
    public static void applySustainedEffect(PlayerEntity player, InstinctEffectType effect) {
        if (effect.isSustained()) {
            PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
            comp.sustainedEffects.add(effect);
            RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.sync(player);
        }
    }

    // 移除持续效果
    public static void removeSustainedEffect(PlayerEntity player, InstinctEffectType effect) {
        PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
        comp.sustainedEffects.remove(effect);
        RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.sync(player);
    }

    public static void saveInstinctComp(PlayerEntity player) {
        PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
        comp.instinctValue = InstinctTicker.currentInstinctValue;
        savePlayerInstinctComponent(world, player.getUuid().toString(), comp);
    }

    public static PlayerInstinctComponent loadInstinctComp(PlayerEntity player) {
        return loadPlayerInstinctComponent(world, player.getUuid().toString());
    }
}
