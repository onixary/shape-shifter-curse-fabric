package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.OneAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.RideAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateControllerDP.WithSneakAnimController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimStateEnum;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_Bat2 extends PlayerFormBase {
    public Form_Bat2(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_ride = AnimationHolder.EMPTY;
    private static AnimationHolder anim_jump = AnimationHolder.EMPTY;
    private static AnimationHolder anim_slow_falling = AnimationHolder.EMPTY;
    private static AnimationHolder anim_tool_swing = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attack = AnimationHolder.EMPTY;


    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SNEAK_IDLE:
            case ANIM_RIDE_VEHICLE_IDLE:
                return anim_sneak_idle;
            case ANIM_RIDE_IDLE:
                return anim_ride;

            case ANIM_JUMP:
                return anim_jump;

            case ANIM_SLOW_FALL:
            case ANIM_CREATIVE_FLY:
                return anim_slow_falling;

            case ANIM_TOOL_SWING:
            case ANIM_SNEAK_TOOL_SWING:
                return anim_tool_swing;

            case ANIM_ATTACK_ONCE:
            case ANIM_SNEAK_ATTACK_ONCE:
                return anim_attack;

            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "bat_1_sneak_idle"), true);
        anim_ride = new AnimationHolder(new Identifier(MOD_ID, "bat_2_riding"), true);
        anim_jump = new AnimationHolder(new Identifier(MOD_ID, "bat_2_jump"), true);
        anim_slow_falling = new AnimationHolder(new Identifier(MOD_ID, "bat_2_slow_falling"), true);
        anim_tool_swing = new AnimationHolder(new Identifier(MOD_ID, "bat_2_digging"), true);
        anim_attack = new AnimationHolder(new Identifier(MOD_ID, "bat_2_attack"), true);
    }

    public static final AbstractAnimStateController JUMP_CONTROLLER = new WithSneakAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_2_jump")), null);
    public static final AbstractAnimStateController IDLE_CONTROLLER = new WithSneakAnimController(null, new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_1_sneak_idle")));
    public static final AbstractAnimStateController RIDE_CONTROLLER = new RideAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_2_riding")), new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_1_sneak_idle")));
    public static final AbstractAnimStateController FALL_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_2_slow_falling")));
    public static final AbstractAnimStateController FLYING_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_2_slow_falling")));
    public static final AbstractAnimStateController MINING_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_2_digging")));
    public static final AbstractAnimStateController ATTACK_CONTROLLER = new OneAnimController(new AnimUtils.AnimationHolderData(ShapeShifterCurseFabric.identifier("bat_2_attack")));

    @Override
    public @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        @Nullable AnimStateEnum animStateEnum = AnimStateEnum.getStateEnum(animStateID);
        if (animStateEnum != null) {
            switch (animStateEnum) {
                case ANIM_STATE_JUMP:
                    return JUMP_CONTROLLER;
                case ANIM_STATE_IDLE:
                    return IDLE_CONTROLLER;
                case ANIM_STATE_RIDE:
                    return RIDE_CONTROLLER;
                case ANIM_STATE_FALL:
                    return FALL_CONTROLLER;
                case ANIM_STATE_FLYING:
                    return FLYING_CONTROLLER;
                case ANIM_STATE_MINING:
                    return MINING_CONTROLLER;
                case ANIM_STATE_ATTACK:
                    return ATTACK_CONTROLLER;
                default:
                    return null;
            }
        }
        return super.getAnimStateController(player, animSystemData, animStateID);
    }
}
