package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.NormalForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_Spider1 extends NormalForm {
    
    public Form_Spider1(Identifier formID) {
        super(formID);
    }

    public static final AnimUtils.AnimationHolderData ANIM_IDLE =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("spider_1_idle"));

    public static final AnimUtils.AnimationHolderData ANIM_MOVE =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("spider_1_move"));

    public static final AbstractAnimStateController IDLE_CONTROLLER =
            new WithSneakAnimController(ANIM_IDLE, ANIM_IDLE);
    public static final AbstractAnimStateController MOVE_CONTROLLER =
            new WithSneakAnimController(ANIM_MOVE, ANIM_IDLE);


    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(
            PlayerEntity player, 
            AnimSystem.AnimSystemData animSystemData, 
            @NotNull Identifier animStateID) {
        
        AnimStateEnum state = AnimStateEnum.getStateEnum(animStateID);
        if (state != null) {
            switch (state) {
                case ANIM_STATE_IDLE:
                    return IDLE_CONTROLLER;
                case ANIM_STATE_WALK:
                    return MOVE_CONTROLLER;
                case ANIM_STATE_SPRINT:
                    return MOVE_CONTROLLER;
                default:
                    return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
