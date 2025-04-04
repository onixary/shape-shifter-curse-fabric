package net.onixary.shapeShifterCurseFabric.player_animation.form_animation;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class AnimationPlayerAxolotl2 {
    private AnimationPlayerAxolotl2() {
    }

    private static AnimationHolder anim_swimming = AnimationHolder.EMPTY;
    private static AnimationHolder anim_swimming_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_crawling = AnimationHolder.EMPTY;
    private static AnimationHolder anim_crawling_idle = AnimationHolder.EMPTY;

    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SWIMMING:
                return anim_swimming;

            case ANIM_SWIM_IDLE:
                return anim_swimming_idle;

            case ANIM_CRAWLING:
                return anim_crawling;

            case ANIM_CRAWLING_IDLE:
                return anim_crawling_idle;

            default:
                return null;
        }
    }

    public static void registerAnims() {
        anim_swimming = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_swimming"), true);
        anim_swimming_idle = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_swimming_idle"), true);
        anim_crawling = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_crawling"), true);
        anim_crawling_idle = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_crawling_idle"), true);
    }
}
