package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;

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
}
