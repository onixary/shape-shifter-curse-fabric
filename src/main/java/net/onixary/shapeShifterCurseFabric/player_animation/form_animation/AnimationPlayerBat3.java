package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationPlayerBat3 {
    private AnimationPlayerBat3() {
    }

    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_jump = AnimationHolder.EMPTY;
    private static AnimationHolder anim_slow_falling = AnimationHolder.EMPTY;
    private static AnimationHolder anim_tool_swing = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attack = AnimationHolder.EMPTY;
    private static AnimationHolder anim_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_run = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attach_side = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attach_bottom = AnimationHolder.EMPTY;


    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SNEAK_IDLE:
            case ANIM_RIDE_IDLE:
                return anim_sneak_idle;

            case ANIM_SNEAK_WALK:
                return anim_sneak_walk;

            case ANIM_JUMP:
            case ANIM_SNEAK_JUMP:
                return anim_jump;
            case ANIM_WALK:
                return anim_walk;
            case ANIM_RUN:
                return anim_run;

            case ANIM_IDLE:
                return anim_idle;
            case ANIM_ATTACH_SIDE:
                return anim_attach_side;
            case ANIM_ATTACH_BOTTOM:
                return anim_attach_bottom;


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

    public static void registerAnims() {
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "bat_1_sneak_idle"), true);
        anim_jump = new AnimationHolder(new Identifier(MOD_ID, "bat_3_jump"), true, 1.5f, 2);
        anim_slow_falling = new AnimationHolder(new Identifier(MOD_ID, "bat_2_slow_falling"), true);
        anim_tool_swing = new AnimationHolder(new Identifier(MOD_ID, "bat_3_digging"), true, 1.5f, 2);
        anim_attack = new AnimationHolder(new Identifier(MOD_ID, "bat_3_attack"), true,1.5f, 2);
        anim_idle = new AnimationHolder(new Identifier(MOD_ID, "bat_3_idle"), true);
        anim_walk = new AnimationHolder(new Identifier(MOD_ID, "bat_3_walk"), true, 1.7f, 4);
        anim_sneak_walk = new AnimationHolder(new Identifier(MOD_ID, "bat_3_sneak_walk"), true);
        anim_attach_side = new AnimationHolder(new Identifier(MOD_ID, "bat_3_attach_side"), true);
        anim_attach_bottom = new AnimationHolder(new Identifier(MOD_ID, "bat_3_attach_bottom"), true);
        anim_run = new AnimationHolder(new Identifier(MOD_ID, "bat_3_walk"), true, 2.4f, 4);
    }
}
