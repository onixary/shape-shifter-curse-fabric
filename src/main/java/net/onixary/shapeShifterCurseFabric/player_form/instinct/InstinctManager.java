package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Objects;

public class InstinctManager {
    private static ServerWorld world;

    public static void getServerWorld(ServerWorld world) {
        InstinctManager.world = world;
    }

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

    public static void applySustainedEffect(PlayerEntity player, InstinctEffect effect) {
        if (effect.isSustained()) {
            PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
            comp.sustainedEffects.removeIf(e -> Objects.equals(e.ID, effect.ID));
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

    public static void removeSustainedEffect(PlayerEntity player, InstinctEffect effect) {
        PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
        comp.sustainedEffects.removeIf(e -> Objects.equals(e.ID, effect.ID));
        RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.sync(player);
    }

    public static void saveInstinctComp(PlayerEntity player) {
        PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
        comp.instinctValue = InstinctTicker.currentInstinctValue;
        RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.sync(player);
    }
}
