package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.*;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_Spider2 extends PlayerFormBase {
    
    public Form_Spider2(Identifier formID) {
        super(formID);
    }

    // v3动画系统
    public static final AnimUtils.AnimationHolderData ANIM_IDLE = 
        new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("spider_2_idle"));

    public static final AbstractAnimStateController IDLE_CONTROLLER = 
        new WithSneakAnimController(ANIM_IDLE, null);

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
                default:
                    return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
