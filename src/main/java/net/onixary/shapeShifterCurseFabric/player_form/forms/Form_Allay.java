package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_Allay extends PlayerFormBase {
    public Form_Allay(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_run = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_walk = AnimationHolder.EMPTY;
    private static AnimationHolder anim_digging = AnimationHolder.EMPTY;
    private static AnimationHolder anim_flying = AnimationHolder.EMPTY;
    private static AnimationHolder anim_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_attack = AnimationHolder.EMPTY;

    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
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

            case ANIM_FLY:
            case ANIM_JUMP:
            case ANIM_FALL:
            case ANIM_SNEAK_FALL:
            case ANIM_SLOW_FALL:
                return anim_flying;

            case ANIM_TOOL_SWING:
                return anim_digging;

            case ANIM_ATTACK_ONCE:
                return anim_attack;
            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_walk = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_moving"), true);
        anim_run = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_run"), true);
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_sneaking"), true);
        anim_sneak_walk = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_sneaking_walk"), true);
        anim_digging = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_digging"), true);
        anim_flying = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_fly"), true);
        anim_idle = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_idle"), true);
        anim_attack = new AnimationHolder(new Identifier(MOD_ID, "allay_sp_attack"), true);
    }
}
