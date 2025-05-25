package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationPlayerFeralCatSP {
    private AnimationPlayerFeralCatSP() {
    }
    private static AnimationHolder anim_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_rush = AnimationHolder.EMPTY;
    private static AnimationHolder anim_rush_jump = AnimationHolder.EMPTY;
    private static AnimationHolder anim_climbing = AnimationHolder.EMPTY;


    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_IDLE:
                return anim_idle;
            case ANIM_SNEAK_IDLE:
                return anim_sneak_idle;
            default:
                return anim_idle;
        }
    }

    public static void registerAnims() {
        anim_idle = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_idle"), true);
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "form_feral_common_sneak_idle"), true);
        anim_sneak_rush = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_sneak_rush"), true);
        anim_rush_jump = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_rush_jump"), true);
        anim_climbing = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_climbing"), true);
    }
}
