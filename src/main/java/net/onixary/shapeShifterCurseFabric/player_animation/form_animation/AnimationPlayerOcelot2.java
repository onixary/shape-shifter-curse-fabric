package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationPlayerOcelot2 {
    private AnimationPlayerOcelot2() {
    }

    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_rush = AnimationHolder.EMPTY;
    private static AnimationHolder anim_rush_jump = AnimationHolder.EMPTY;


    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SNEAK_IDLE:
                return anim_sneak_idle;
            case ANIM_SNEAK_RUSH:
                return anim_sneak_rush;
            case ANIM_RUSH_JUMP:
                return anim_rush_jump;

            default:
                return null;
        }
    }

    public static void registerAnims() {
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_sneak_idle"), true);
        anim_sneak_rush = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_sneak_rush_2"), true, 3.3f);
        anim_rush_jump = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_rush_jump"), true);
    }
}
