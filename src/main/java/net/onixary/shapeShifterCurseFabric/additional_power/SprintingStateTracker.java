package net.onixary.shapeShifterCurseFabric.additional_power;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SprintingStateTracker {
    private static final Map<UUID, Boolean> sprintingStates = new HashMap<>();
    private static final Map<UUID, Boolean> hasTriggered = new HashMap<>(); // 新增：标记是否已触发

    public static boolean wasSprintingLastTick(PlayerEntity player) {
        return sprintingStates.getOrDefault(player.getUuid(), false);
    }

    public static void updateSprintingState(PlayerEntity player, boolean isSprinting) {
        UUID uuid = player.getUuid();
        boolean wasSprintingBefore = sprintingStates.getOrDefault(uuid, false);

        sprintingStates.put(uuid, isSprinting);

        // 当开始疾跑时，重置触发标志
        if (!wasSprintingBefore && isSprinting) {
            hasTriggered.put(uuid, false);
            ShapeShifterCurseFabric.LOGGER.debug("Player {} started sprinting, reset trigger flag", player.getName().getString());
        }
    }

    public static boolean canTrigger(PlayerEntity player) {
        // 检查是否已经触发过
        return !hasTriggered.getOrDefault(player.getUuid(), false);
    }

    public static void setTriggered(PlayerEntity player) {
        hasTriggered.put(player.getUuid(), true);
        ShapeShifterCurseFabric.LOGGER.debug("Player {} trigger flag set to true", player.getName().getString());
    }

    public static void removePlayer(PlayerEntity player) {
        UUID uuid = player.getUuid();
        sprintingStates.remove(uuid);
        hasTriggered.remove(uuid);
    }

    // 移除resetSprintingState方法，不再需要
}

