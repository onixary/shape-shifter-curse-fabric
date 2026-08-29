package net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimFSM;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimFSM;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimRegistries.*;

public class InAirFSM extends AbstractAnimFSM {
    @Override
    public @Nullable Identifier getNextFSM(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        if (animSystemData.IsOnGround) {
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
        // TODO 需要想一个对网络友好的同步方式 最好能客户端单独处理的
        if (player.getAbilities().flying) {
            return ANIM_STATE_FLYING;
        }
        if (player.isFallFlying()) {
            return ANIM_STATE_FALL_FLYING;
        }
        if (animSystemData.fakeVelocity.getY() < 0 && (FormUtils.HasSlowFall.hasFlag(animSystemData.playerForm) || animSystemData.fallDistance > 0.6f)) {
            return ANIM_STATE_FALL;
        }
        return ANIM_STATE_JUMP;
    }
}
