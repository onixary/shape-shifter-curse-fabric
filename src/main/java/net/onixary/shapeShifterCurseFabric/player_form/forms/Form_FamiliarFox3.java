package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RideAnimController;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_FamiliarFox3 extends PlayerFormBase {
    public Form_FamiliarFox3(Identifier formID) {
        super(formID);
        this.setBodyType(PlayerFormBodyType.FERAL);
    }

    public static final AbstractAnimStateController RIDE_CONTROLLER = new RideAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("familiar_fox_3_riding")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_sneak_idle")));

    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_SLEEP:
                    return Form_FeralBase.SLEEP_CONTROLLER;
                case ANIM_STATE_CLIMB:
                    return Form_FeralBase.CLIMB_CONTROLLER;
                case ANIM_STATE_FALL:
                    return Form_FeralBase.FALL_CONTROLLER;
                case ANIM_STATE_JUMP:
                    return Form_FeralBase.JUMP_CONTROLLER;
                case ANIM_STATE_RIDE:
                    return RIDE_CONTROLLER;
                case ANIM_STATE_SWIM:
                    return Form_FeralBase.SWIM_CONTROLLER;
                case ANIM_STATE_USE_ITEM:
                    return Form_FeralBase.USE_ITEM_CONTROLLER;
                case ANIM_STATE_WALK:
                    return Form_FeralBase.WALK_CONTROLLER;
                case ANIM_STATE_SPRINT:
                    return Form_FeralBase.SPRINT_CONTROLLER;
                case ANIM_STATE_IDLE:
                    return Form_FeralBase.IDLE_CONTROLLER;
                case ANIM_STATE_MINING:
                    return Form_FeralBase.MINING_CONTROLLER;
                case ANIM_STATE_ATTACK:
                    return Form_FeralBase.ATTACK_CONTROLLER;
                case ANIM_STATE_FLYING:
                case ANIM_STATE_FALL_FLYING:
                    return Form_FeralBase.FALL_FLYING_CONTROLLER;
                default:
                    return Form_FeralBase.IDLE_CONTROLLER;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
