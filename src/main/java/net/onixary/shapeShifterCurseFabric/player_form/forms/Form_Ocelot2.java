package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_Ocelot2 extends PlayerFormBase {
    public Form_Ocelot2(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_sneak_rush = AnimationHolder.EMPTY;
    private static AnimationHolder anim_rush_jump = AnimationHolder.EMPTY;


    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SNEAK_IDLE:
            case ANIM_RIDE_IDLE:
                return anim_sneak_idle;
            case ANIM_SNEAK_RUSH:
                return anim_sneak_rush;
            case ANIM_RUSH_JUMP:
                return anim_rush_jump;
            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_sneak_idle"), true);
        anim_sneak_rush = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_sneak_rush_2"), true, 3.3f);
        anim_rush_jump = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_rush_jump"), true);
    }
}
