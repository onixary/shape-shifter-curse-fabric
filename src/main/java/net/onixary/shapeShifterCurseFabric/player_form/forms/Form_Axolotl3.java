package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_Axolotl3 extends PlayerFormBase {
    public Form_Axolotl3(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_idle = new AnimationHolder();
    private static AnimationHolder anim_walking = AnimationHolder.EMPTY;
    private static AnimationHolder anim_running = AnimationHolder.EMPTY;
    private static AnimationHolder anim_jump = AnimationHolder.EMPTY;
    private static AnimationHolder anim_swimming = AnimationHolder.EMPTY;
    private static AnimationHolder anim_swimming_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_crawling = AnimationHolder.EMPTY;
    private static AnimationHolder anim_crawling_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_crawling_attack_once = AnimationHolder.EMPTY;
    private static AnimationHolder anim_crawling_tool_swing = AnimationHolder.EMPTY;
    private static AnimationHolder anim_crawling_jump = AnimationHolder.EMPTY;
    private static AnimationHolder anim_rush_jump = AnimationHolder.EMPTY;


    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_JUMP:
            case ANIM_FALL:
                return anim_jump;
            case ANIM_SNEAK_JUMP:
                return anim_crawling_jump;
            case ANIM_SNEAK_FALL:
                return anim_crawling_idle;
            case ANIM_RUSH_JUMP:
                return anim_rush_jump;

            case ANIM_WALK:
                return anim_walking;
            case ANIM_RUN:
                return anim_running;
            case ANIM_IDLE:
                return anim_idle;

            case ANIM_SWIM:
                return anim_swimming;
            case ANIM_SWIM_IDLE:
                return anim_swimming_idle;

            case ANIM_SNEAK_WALK:
                return anim_crawling;

            case ANIM_SNEAK_IDLE:
                return anim_crawling_idle;

            case ANIM_SNEAK_ATTACK_ONCE:
                return anim_crawling_attack_once;

            case ANIM_SNEAK_TOOL_SWING:
                return anim_crawling_tool_swing;

            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_swimming = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_swimming"), true);
        anim_swimming_idle = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_swimming_idle"), true);
        anim_crawling = new AnimationHolder(new Identifier(MOD_ID, "axolotl_3_crawling"), true);
        anim_crawling_idle = new AnimationHolder(new Identifier(MOD_ID, "axolotl_3_crawling_idle"), true);
        anim_crawling_attack_once = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_crawling_attack_once"), true);
        anim_crawling_tool_swing = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_crawling_tool_swing"), true);
        anim_crawling_jump = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_crawling_jump"), true);
        anim_walking = new AnimationHolder(new Identifier(MOD_ID, "axolotl_3_walk"), true);
        anim_running = new AnimationHolder(new Identifier(MOD_ID, "axolotl_3_run"), true);
        anim_jump = new AnimationHolder(new Identifier(MOD_ID, "axolotl_3_jump"), true);
        anim_idle = new AnimationHolder(new Identifier(MOD_ID, "axolotl_3_idle"), true);
        anim_rush_jump = new AnimationHolder(new Identifier(MOD_ID, "axolotl_3_rush_jump"), true, 1, 10);
    }
}
