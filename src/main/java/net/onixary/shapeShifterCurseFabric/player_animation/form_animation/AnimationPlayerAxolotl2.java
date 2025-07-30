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
    private static AnimationHolder anim_crawling_attack_once = AnimationHolder.EMPTY;
    private static AnimationHolder anim_crawling_tool_swing = AnimationHolder.EMPTY;
    private static AnimationHolder anim_crawling_jump = AnimationHolder.EMPTY;


    public static AnimationHolder getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SWIMMING:
                return anim_swimming;

            case ANIM_SWIM_IDLE:
                return anim_swimming_idle;

            case ANIM_SNEAK_WALK:
                return anim_crawling;

            case ANIM_SNEAK_IDLE:
                return anim_crawling_idle;

            case ANIM_SNEAK_JUMP:
                return anim_crawling_jump;

            case ANIM_SNEAK_ATTACK_ONCE:
                return anim_crawling_attack_once;

            case ANIM_SNEAK_TOOL_SWING:
                return anim_crawling_tool_swing;

            default:
                return null;
        }
    }

    public static void registerAnims() {
        anim_swimming = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_swimming"), true);
        anim_swimming_idle = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_swimming_idle"), true);
        anim_crawling = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_crawling_new"), true);
        anim_crawling_idle = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_crawling_idle_new"), true);
        anim_crawling_attack_once = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_crawling_attack_once"), true);
        anim_crawling_tool_swing = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_crawling_tool_swing"), true);
        anim_crawling_jump = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_crawling_jump"), true);
    }
}
