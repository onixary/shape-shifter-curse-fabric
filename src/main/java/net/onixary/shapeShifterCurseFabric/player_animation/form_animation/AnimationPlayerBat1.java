package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AbstractFormAnimationPlayer;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import java.util.EnumSet;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationPlayerBat1{
    private AnimationPlayerBat1() {
    }

    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_walk = AnimationHolder.EMPTY;


    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            //case ANIM_SNEAK_IDLE:
            //    return anim_sneak_idle;
            //case ANIM_SNEAK_WALK:
            //    return anim_sneak_walk;
            default:
                return null;
        }
    }

    public static void registerAnims() {
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "bat_1_sneak_idle"), true);
        anim_sneak_walk = new AnimationHolder(new Identifier(MOD_ID, "bat_1_sneak_walk"), true);
    }
}
