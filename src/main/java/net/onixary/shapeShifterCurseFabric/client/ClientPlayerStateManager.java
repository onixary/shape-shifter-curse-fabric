package net.onixary.shapeShifterCurseFabric.client;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.onixary.shapeShifterCurseFabric.additional_power.BatBlockAttachPower;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientPlayerStateManager {
    private static final Map<UUID, PlayerAttachState> PLAYER_ATTACH_STATES = new HashMap<>();
    public static boolean shouldForceSneak = false;

    public static class PlayerAttachState {
        public boolean isAttached;
        public BatBlockAttachPower.AttachType attachType;
        public BlockPos attachedPos;
        public Direction attachedSide;
    }

    public static void updatePlayerAttachState(UUID playerUuid, boolean isAttached,
                                               int attachTypeOrdinal, BlockPos attachedPos, Direction attachedSide) {
        PlayerAttachState state = PLAYER_ATTACH_STATES.computeIfAbsent(playerUuid, k -> new PlayerAttachState());
        state.isAttached = isAttached;
        state.attachType = BatBlockAttachPower.AttachType.values()[attachTypeOrdinal];
        state.attachedPos = attachedPos;
        state.attachedSide = attachedSide;
    }

    public static PlayerAttachState getPlayerAttachState(UUID playerUuid) {
        return PLAYER_ATTACH_STATES.get(playerUuid);
    }

    public static void clearPlayerState(UUID playerUuid) {
        PLAYER_ATTACH_STATES.remove(playerUuid);
    }
}
