package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v2.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.*;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Form_Spider3 extends PlayerFormBase {
    
    public Form_Spider3(Identifier formID) {
        super(formID);
    }

    // v3动画系统
    public static final AnimUtils.AnimationHolderData ANIM_IDLE = 
        new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("spider_3_idle"));
    public static final AnimUtils.AnimationHolderData ANIM_RUN =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("spider_3_run")).setSpeed(2.0f);
    public static final AnimUtils.AnimationHolderData ANIM_WALK =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("spider_3_walk"));
    public static final AnimUtils.AnimationHolderData ANIM_SNEAK_IDLE =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("spider_3_sneak_idle"));
    public static final AnimUtils.AnimationHolderData ANIM_JUMP =
            new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("spider_3_jump"));

    public static final AbstractAnimStateController IDLE_CONTROLLER = 
        new WithSneakAnimController(ANIM_IDLE, ANIM_SNEAK_IDLE);
    public static final AbstractAnimStateController WALK_CONTROLLER =
            new WithSneakAnimController(ANIM_WALK, ANIM_SNEAK_IDLE);
    public static final AbstractAnimStateController RUN_CONTROLLER =
            new WithSneakAnimController(ANIM_RUN, ANIM_SNEAK_IDLE);
    public static final AbstractAnimStateController JUMP_CONTROLLER =
            new OneAnimController(ANIM_JUMP);


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
                    return WALK_CONTROLLER;
                case ANIM_STATE_SPRINT:
                    return RUN_CONTROLLER;
                case ANIM_STATE_JUMP:
                    return JUMP_CONTROLLER;
                default:
                    return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }

    // v2动画兼容
    @Override
    public void Anim_registerAnims() {
        // 动画注册（如需要）
    }

    @Override
    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        return null;
    }
}
