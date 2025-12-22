package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v2.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.*;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RideAnimController;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_FeralCatSP extends PlayerFormBase {
    public Form_FeralCatSP(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_ride = AnimationHolder.EMPTY;
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
                return anim_sneak_idle;
            case ANIM_RIDE_IDLE:
                return anim_ride;
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
        anim_ride = new AnimationHolder(new Identifier(MOD_ID, "feral_cat_sp_riding"), true);
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

    public static final AbstractAnimStateController RIDE_CONTROLLER = new RideAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("feral_cat_sp_riding")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("form_feral_common_sneak_idle")));

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
