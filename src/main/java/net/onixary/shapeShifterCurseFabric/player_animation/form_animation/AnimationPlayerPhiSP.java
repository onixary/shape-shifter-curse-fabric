package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationPlayerPhiSP {
    private AnimationPlayerPhiSP() {
    }
    private static AnimationHolder anim_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_run = AnimationHolder.EMPTY;
    private static AnimationHolder anim_float = AnimationHolder.EMPTY;
    private static AnimationHolder anim_swim = AnimationHolder.EMPTY;
    private static AnimationHolder anim_dig = AnimationHolder.EMPTY;
    private static AnimationHolder anim_jump = AnimationHolder.EMPTY;
    private static AnimationHolder anim_climb = AnimationHolder.EMPTY;
    private static AnimationHolder anim_fall = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attack = AnimationHolder.EMPTY;


    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_IDLE:
                return anim_idle;
            case ANIM_SNEAK_IDLE:
            case ANIM_BOAT_IDLE:
                return anim_sneak_idle;
            case ANIM_WALK:
                return anim_walk;
            case ANIM_SNEAK_WALK:
                return anim_sneak_walk;
            case ANIM_RUN:
                return anim_run;
            case ANIM_SWIM_IDLE:
                return anim_float;
            case ANIM_SWIMMING:
                return anim_swim;
            case ANIM_TOOL_SWING:
                return anim_dig;
            case ANIM_JUMP:
                return anim_jump;
            case ANIM_CLIMBING_IDLE:
            case ANIM_CLIMBING:
                return anim_climb;
            case ANIM_FALLING:
                return anim_fall;

            case ANIM_ATTACK_ONCE:
                return anim_attack;

            default:
                return anim_idle;
        }
    }

    public static void registerAnims() {
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
    }
}
