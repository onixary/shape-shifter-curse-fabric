package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationPlayerAllaySP {
    private AnimationPlayerAllaySP() {
    }

    private static AnimationHolder anim_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_run = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_digging = AnimationHolder.EMPTY;
    private static AnimationHolder anim_flying = AnimationHolder.EMPTY;
    private static AnimationHolder anim_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attack = AnimationHolder.EMPTY;

    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_WALK:
                return anim_walk;

            case ANIM_RUN:
                return anim_run;

            case ANIM_SNEAK_IDLE:
                return anim_sneak_idle;

            case ANIM_SNEAK_WALK:
                return anim_sneak_walk;

            case ANIM_IDLE:
                return anim_idle;

            case ANIM_FLYING:
            case ANIM_JUMP:
            case ANIM_FALLING:
            case ANIM_SLOW_FALLING:
                return anim_flying;

            case ANIM_TOOL_SWING:
                return anim_digging;

            case ANIM_ATTACK_ONCE:
                return anim_attack;
            default:
                return null;
        }
    }

    public static void registerAnims() {
        anim_walk = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_moving"), true);
        anim_run = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_run"), true);
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_sneaking"), true);
        anim_sneak_walk = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_sneaking_walk"), true);
        anim_digging = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_digging"), true);
        anim_flying = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_fly"), true);
        anim_idle = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_idle"), true, 1, 0);
        anim_attack = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_attack"), true, 1, 0);
    }
}
