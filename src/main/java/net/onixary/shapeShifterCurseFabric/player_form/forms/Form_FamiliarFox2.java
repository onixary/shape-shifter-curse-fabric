package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_FamiliarFox2 extends PlayerFormBase {
    public Form_FamiliarFox2(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_sneak_idle = AnimationHolder.EMPTY;
    private static AnimationHolder anim_ride = AnimationHolder.EMPTY;


    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SNEAK_IDLE:
                return anim_sneak_idle;
            case ANIM_RIDE_VEHICLE_IDLE:
            case ANIM_RIDE_IDLE:
                return anim_ride;

            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_sneak_idle = new AnimationHolder(new Identifier(MOD_ID, "ocelot_2_sneak_idle"), true);
        anim_ride = new AnimationHolder(new Identifier(MOD_ID, "familiar_fox_2_riding"), true);
    }
}
