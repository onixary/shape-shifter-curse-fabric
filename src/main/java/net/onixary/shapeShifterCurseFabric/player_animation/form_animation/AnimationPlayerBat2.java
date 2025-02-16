package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationPlayerBat2 {
    private AnimationPlayerBat2() {
    }

    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;


    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SNEAK_IDLE:
                return anim_sneak_idle;
            default:
                return null;
        }
    }

    public static void registerAnims() {
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "bat_1_sneak_idle"), true);
    }
}
