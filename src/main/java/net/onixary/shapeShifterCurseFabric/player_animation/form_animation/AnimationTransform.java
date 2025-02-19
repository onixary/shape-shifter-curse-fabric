package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationTransform {
    private AnimationTransform() {
    }

    private static AnimationHolder anim_on_transform = AnimationHolder.EMPTY;


    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        return anim_on_transform;
    }

    public static void registerAnims() {
        anim_on_transform = new AnimationHolder(new Identifier(MOD_ID, "player_on_transform"), true);
    }
}
