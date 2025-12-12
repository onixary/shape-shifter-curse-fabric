package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateController.ConditionAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.OneAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.SneakRushAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;


// 通用四足形态
public class Form_FeralBase extends PlayerFormBase {
    public Form_FeralBase(Identifier formID) {
        super(formID);
        this.setBodyType(PlayerFormBodyType.FERAL);
    }

    private static AnimationHolder anim_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_rush = AnimationHolder.EMPTY;
    private static AnimationHolder anim_run = AnimationHolder.EMPTY;
    private static AnimationHolder anim_float = AnimationHolder.EMPTY;
    private static AnimationHolder anim_swim = AnimationHolder.EMPTY;
    private static AnimationHolder anim_dig = AnimationHolder.EMPTY;
    private static AnimationHolder anim_jump = AnimationHolder.EMPTY;
    private static AnimationHolder anim_climb = AnimationHolder.EMPTY;
    private static AnimationHolder anim_fall = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attack = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sleep = AnimationHolder.EMPTY;
    private static AnimationHolder anim_elytra_fly = AnimationHolder.EMPTY;


    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_IDLE:
                return anim_idle;
            case ANIM_SNEAK_IDLE:
            case ANIM_RIDE_VEHICLE_IDLE:
            case ANIM_RIDE_IDLE:
                return anim_sneak_idle;
            case ANIM_WALK:
                return anim_walk;
            case ANIM_SNEAK_WALK:
                return anim_sneak_walk;
            case ANIM_SNEAK_RUSH:
                return anim_sneak_rush;
            case ANIM_RUN:
                return anim_run;
            case ANIM_SWIM_IDLE:
                return anim_float;
            case ANIM_SWIM:
                return anim_swim;
            case ANIM_TOOL_SWING:
            case ANIM_SNEAK_TOOL_SWING:
                return anim_dig;
            case ANIM_JUMP:
            case ANIM_SNEAK_JUMP:
                return anim_jump;
            case ANIM_CLIMB_IDLE:
            case ANIM_CLIMB:
                return anim_climb;
            case ANIM_FALL:
            case ANIM_SNEAK_FALL:
                return anim_fall;
            case ANIM_SLEEP:
                return anim_sleep;

            case ANIM_ATTACK_ONCE:
            case ANIM_SNEAK_ATTACK_ONCE:
                return anim_attack;
            case ANIM_ELYTRA_FLY:
            case ANIM_CREATIVE_FLY:
                return anim_elytra_fly;

            default:
                return anim_idle;
        }
    }

    public void Anim_registerAnims() {
        anim_idle = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_idle"), true);
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_sneak_idle"), true);
        anim_walk = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_walk"), true, 1.2f, 2);
        anim_sneak_walk = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_sneak_walk"), true);
        anim_run = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_run"), true, 2.3f);
        anim_float = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_float"), true);
        anim_swim = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_swim"), true);
        anim_dig = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_dig"), true);
        anim_jump = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_jump"), true);
        anim_climb = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_climb"), true);
        anim_fall = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_fall"), true);
        anim_attack = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_attack"), true);
        anim_sleep = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_sleep"), true);
        anim_elytra_fly = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_elytra_fly"), true);
        anim_sneak_rush = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_run"), true, 2.3f);
    }

    private static final AnimUtils.AnimationHolderData ANIM_IDLE = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_idle"));
    private static final AnimUtils.AnimationHolderData ANIM_SNEAK_IDLE = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_sneak_idle"));
    private static final AnimUtils.AnimationHolderData ANIM_WALK = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_walk"), 1.2f, 2);
    private static final AnimUtils.AnimationHolderData ANIM_SNEAK_WALK = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_sneak_walk"));
    private static final AnimUtils.AnimationHolderData ANIM_RUN = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_run"), 2.3f);
    private static final AnimUtils.AnimationHolderData ANIM_FLOAT = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_float"));
    private static final AnimUtils.AnimationHolderData ANIM_SWIM = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_swim"));
    private static final AnimUtils.AnimationHolderData ANIM_DIG = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_dig"));
    private static final AnimUtils.AnimationHolderData ANIM_JUMP = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_jump"));
    private static final AnimUtils.AnimationHolderData ANIM_CLIMB = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_climb"));
    private static final AnimUtils.AnimationHolderData ANIM_FALL = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_fall"));
    private static final AnimUtils.AnimationHolderData ANIM_ATTACK = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_attack"));
    private static final AnimUtils.AnimationHolderData ANIM_SLEEP = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_sleep"));
    private static final AnimUtils.AnimationHolderData ANIM_ELYTRA_FLY = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_elytra_fly"));
    private static final AnimUtils.AnimationHolderData ANIM_SNEAK_RUSH = new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_run"), 2.3f);

    public static final AbstractAnimStateController IDLE_CONTROLLER = new WithSneakAnimController(ANIM_IDLE, ANIM_SNEAK_IDLE);
    public static final AbstractAnimStateController WALK_CONTROLLER = new WithSneakAnimController(ANIM_WALK, ANIM_SNEAK_WALK);
    public static final AbstractAnimStateController SPRINT_CONTROLLER = new WithSneakAnimController(ANIM_RUN, ANIM_SNEAK_WALK);
    public static final AbstractAnimStateController WALK_CONTROLLER_SNEAK_RUSH = new SneakRushAnimController(ANIM_WALK, ANIM_SNEAK_WALK, ANIM_SNEAK_RUSH);
    public static final AbstractAnimStateController SPRINT_CONTROLLER_SNEAK_RUSH = new SneakRushAnimController(ANIM_RUN, ANIM_SNEAK_WALK, ANIM_SNEAK_RUSH);
    public static final AbstractAnimStateController SWIM_CONTROLLER = new ConditionAnimController(new LinkedList<>() {
        {
            this.add(new Pair<>((player, data) -> player.isSwimming(), ANIM_SWIM));
        }
    }, ANIM_FLOAT);
    public static final AbstractAnimStateController MINING_CONTROLLER = new OneAnimController(ANIM_DIG);
    public static final AbstractAnimStateController ATTACK_CONTROLLER = new OneAnimController(ANIM_ATTACK);
    public static final AbstractAnimStateController SLEEP_CONTROLLER = new OneAnimController(ANIM_SLEEP);
    public static final AbstractAnimStateController FALL_FLYING_CONTROLLER = new OneAnimController(ANIM_ELYTRA_FLY);
    public static final AbstractAnimStateController CLIMB_CONTROLLER = new OneAnimController(ANIM_CLIMB);
    public static final AbstractAnimStateController JUMP_CONTROLLER = new OneAnimController(ANIM_JUMP);
    public static final AbstractAnimStateController FALL_CONTROLLER = new OneAnimController(ANIM_FALL);
    public static final AbstractAnimStateController USE_ITEM_CONTROLLER = new ConditionAnimController(new LinkedList<>() {
        {
            this.add(new Pair<>((player, data) -> player.isSneaking() && data.IsWalking, ANIM_SNEAK_WALK));
            this.add(new Pair<>((player, data) -> player.isSneaking(), ANIM_SNEAK_IDLE));
        }
    }, ANIM_IDLE);
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
                    return null;
            }
        }
        return null;
    }
}
