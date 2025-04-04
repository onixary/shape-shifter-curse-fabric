package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationPlayerAxolotl1 {
    private AnimationPlayerAxolotl1() {
    }

    private static AnimationHolder anim_swimming_idle = AnimationHolder.EMPTY;

    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SWIM_IDLE:
                return anim_swimming_idle;
            default:
                return null;
        }
    }

    public static void registerAnims() {
        anim_swimming_idle = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_swimming_idle"), true);
    }
}
