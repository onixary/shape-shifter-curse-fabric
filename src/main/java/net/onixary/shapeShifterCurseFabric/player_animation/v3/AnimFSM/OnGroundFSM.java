package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimFSM;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimRegistries.*;  // 导入量比较多 用静态导入比较方便

public class OnGroundFSM extends AbstractAnimFSM {
    @Override
    public @Nullable Identifier getNextFSM(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        if (!animSystemData.IsOnGround) {
            return FSM_IN_AIR;
        } else if (player.isUsingItem() || player.handSwinging) {
            return FSM_USE_ITEM;
        }
        return null;
    }

    @Override
    public @NotNull Identifier getStateID(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        @Nullable Identifier UniversalStateResult = FSMUtils.ProcessUniversalAnim(player, animSystemData);
        if (UniversalStateResult != null) {
            return UniversalStateResult;
        }
        if (!player.getPos().equals(animSystemData.LastPosition)) {
            if (player.isSprinting()) {
                return ANIM_STATE_SPRINT;
            } else {
                return ANIM_STATE_WALK;
            }
        }
        return ANIM_STATE_IDLE;
    }
}
