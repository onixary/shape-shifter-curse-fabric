package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimFSM;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimRegistries.*;

public class UseItemFSM extends AbstractAnimFSM {
    @Override
    public @Nullable Identifier getNextFSM(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        if (!(player.isUsingItem() || player.handSwinging)) {
            return FSM_ON_GROUND;
        }
        return null;
    }

    @Override
    public @NotNull Identifier getStateID(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        @Nullable Identifier UniversalStateResult = FSMUtils.ProcessUniversalAnim(player, animSystemData);
        if (UniversalStateResult != null) {
            return UniversalStateResult;
        }
        if (player.isUsingItem()) {
            return ANIM_STATE_USE_ITEM;
        }
        // 到这里player.handSwinging一定是true
        if (animSystemData.ContinueSwingAnimCounter >= 10) {
            return ANIM_STATE_MINING;
        }
        return ANIM_STATE_ATTACK;
    }
}
