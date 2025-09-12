package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Objects;

import static net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage.loadPlayerInstinctComponent;
import static net.onixary.shapeShifterCurseFabric.data.PlayerNbtStorage.savePlayerInstinctComponent;

public class InstinctManager {
    private static ServerWorld world;

    public static void getServerWorld(ServerWorld world) {
        InstinctManager.world = world;
    }
    // 添加立即效果
    public static void applyImmediateEffect(PlayerEntity player, InstinctEffect effect) {
        if (!effect.isSustained()) {
            PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
            comp.immediateEffects.add(effect);
            RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.sync(player);
        }
    }

    public static void applyImmediateEffect(PlayerEntity player, String effectID, float effectValue) {
        if (!effectID.isEmpty()) {
            InstinctEffect effect = new InstinctEffect(effectID, effectValue, 0, false);
            applyImmediateEffect(player, effect);
        }
    }

    // 添加持续效果
    public static void applySustainedEffect(PlayerEntity player, InstinctEffect effect) {
        if (effect.isSustained()) {
            PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
            // 同一种类效果只保留一个
            comp.sustainedEffects.removeIf(e -> Objects.equals(e.ID, effect.ID));
            //ShapeShifterCurseFabric.LOGGER.info("applySustainedEffect in InstinctManager: " + effect);
            comp.sustainedEffects.add(effect);
            RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.sync(player);
        }
    }

    public static void applySustainedEffect(PlayerEntity player, String effectID, float effectValue, int duration) {
        if (!effectID.isEmpty()) {
            InstinctEffect effect = new InstinctEffect(effectID, effectValue, duration, true);
            applySustainedEffect(player, effect);
        }
    }

    public static void applyEffect(PlayerEntity player, String effectID, float effectValue, int duration) {
        if (duration == 0) {
            applyImmediateEffect(player, effectID, effectValue);
        } else {
            applySustainedEffect(player, effectID, effectValue, duration);
        }
    }

    // 移除持续效果
    public static void removeSustainedEffect(PlayerEntity player, InstinctEffect effect) {
        PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
//        comp.sustainedEffects.remove(effect);
        comp.sustainedEffects.removeIf(e -> Objects.equals(e.ID, effect.ID));
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
