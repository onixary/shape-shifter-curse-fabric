package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.*;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// 通用四足形态
public class Form_FeralBase extends PlayerFormBase {
    public Form_FeralBase(Identifier formID) {
        super(formID);
        this.setBodyType(PlayerFormBodyType.FERAL);
    }

    // 共用动画 其他类尽量使用private
    public static final AnimUtils.AnimationHolderData ANIM_IDLE = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_idle"));
    public static final AnimUtils.AnimationHolderData ANIM_SNEAK_IDLE = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_sneak_idle"));
    public static final AnimUtils.AnimationHolderData ANIM_WALK = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_walk"), 1.2f, 2);
    public static final AnimUtils.AnimationHolderData ANIM_SNEAK_WALK = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_sneak_walk"));
    public static final AnimUtils.AnimationHolderData ANIM_RUN = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_run"), 2.3f);
    public static final AnimUtils.AnimationHolderData ANIM_FLOAT = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_float"));
    public static final AnimUtils.AnimationHolderData ANIM_SWIM = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_swim"));
    public static final AnimUtils.AnimationHolderData ANIM_DIG = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_dig"));
    public static final AnimUtils.AnimationHolderData ANIM_JUMP = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_jump"));
    public static final AnimUtils.AnimationHolderData ANIM_CLIMB = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_climb"));
    public static final AnimUtils.AnimationHolderData ANIM_FALL = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_fall"));
    public static final AnimUtils.AnimationHolderData ANIM_ATTACK = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_attack"));
    public static final AnimUtils.AnimationHolderData ANIM_SLEEP = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_sleep"));
    public static final AnimUtils.AnimationHolderData ANIM_ELYTRA_FLY = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_elytra_fly"));
    public static final AnimUtils.AnimationHolderData ANIM_SNEAK_RUSH = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_run"), 2.3f);

    public static final AbstractAnimStateController IDLE_CONTROLLER = new WithSneakAnimController(ANIM_IDLE, ANIM_SNEAK_IDLE);
    public static final AbstractAnimStateController WALK_CONTROLLER = new WithSneakAnimController(ANIM_WALK, ANIM_SNEAK_WALK);
    public static final AbstractAnimStateController SPRINT_CONTROLLER = new WithSneakAnimController(ANIM_RUN, ANIM_SNEAK_WALK);
    public static final AbstractAnimStateController WALK_CONTROLLER_SNEAK_RUSH = new SneakRushAnimController(ANIM_WALK, ANIM_SNEAK_WALK, ANIM_SNEAK_RUSH);
    public static final AbstractAnimStateController SPRINT_CONTROLLER_SNEAK_RUSH = new SneakRushAnimController(ANIM_RUN, ANIM_SNEAK_WALK, ANIM_SNEAK_RUSH);
    /* 还是留一下ConditionAnimController的使用方法把 逻辑有些复杂
    public static final AbstractAnimStateController SWIM_CONTROLLER = new ConditionAnimController(new LinkedList<>() {
        {
            this.add(new Pair<>((player, data) -> player.isSwimming(), ANIM_SWIM));
        }
    }, ANIM_FLOAT);
     */
    public static final AbstractAnimStateController SWIM_CONTROLLER = new SwimAnimController(ANIM_FLOAT, ANIM_SWIM);
    public static final AbstractAnimStateController MINING_CONTROLLER = new OneAnimController(ANIM_DIG);
    public static final AbstractAnimStateController ATTACK_CONTROLLER = new OneAnimController(ANIM_ATTACK);
    public static final AbstractAnimStateController SLEEP_CONTROLLER = new OneAnimController(ANIM_SLEEP);
    public static final AbstractAnimStateController FALL_FLYING_CONTROLLER = new OneAnimController(ANIM_ELYTRA_FLY);
    public static final AbstractAnimStateController CLIMB_CONTROLLER = new OneAnimController(ANIM_CLIMB);
    public static final AbstractAnimStateController JUMP_CONTROLLER = new OneAnimController(ANIM_JUMP);
    public static final AbstractAnimStateController FALL_CONTROLLER = new OneAnimController(ANIM_FALL);
    // UseItemAnimControllerPro的参数确实是ANIM_IDLE, ANIM_IDLE, ANIM_SNEAK_IDLE, ANIM_SNEAK_WALK 由于ANIM_SNEAK_IDLE为坐下动画 所以SNEAK动画需要区分 如果有单独的动画可以使用WithSneakAnimController
    public static final AbstractAnimStateController USE_ITEM_CONTROLLER = new UseItemAnimControllerPro(ANIM_IDLE, ANIM_IDLE, ANIM_SNEAK_IDLE, ANIM_SNEAK_WALK);
    public static final AbstractAnimStateController RIDE_CONTROLLER = new OneAnimController(ANIM_SNEAK_IDLE);

    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_SLEEP:
                    return SLEEP_CONTROLLER;
                case ANIM_STATE_CLIMB:
                    return CLIMB_CONTROLLER;
                case ANIM_STATE_FALL:
                    return FALL_CONTROLLER;
                case ANIM_STATE_JUMP:
                    return JUMP_CONTROLLER;
                case ANIM_STATE_RIDE:
                    return RIDE_CONTROLLER;
                case ANIM_STATE_SWIM:
                    return SWIM_CONTROLLER;
                case ANIM_STATE_USE_ITEM:
                    return USE_ITEM_CONTROLLER;
                case ANIM_STATE_WALK:
                    if (this.getCanSneakRush()) {
                        return WALK_CONTROLLER_SNEAK_RUSH;
                    }
                    else {
                        return WALK_CONTROLLER;
                    }
                case ANIM_STATE_SPRINT:
                    if (this.getCanSneakRush()) {
                        return SPRINT_CONTROLLER_SNEAK_RUSH;
                    }
                    else {
                        return SPRINT_CONTROLLER;
                    }
                case ANIM_STATE_IDLE:
                    return IDLE_CONTROLLER;
                case ANIM_STATE_MINING:
                    return MINING_CONTROLLER;
                case ANIM_STATE_ATTACK:
                    return ATTACK_CONTROLLER;
                case ANIM_STATE_FLYING:
                case ANIM_STATE_FALL_FLYING:
                    return FALL_FLYING_CONTROLLER;
                default:
                    return Form_FeralBase.IDLE_CONTROLLER;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
