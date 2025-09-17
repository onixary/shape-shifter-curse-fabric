package net.onixary.shapeShifterCurseFabric.player_form.forms;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class Form_Axolotl1 extends PlayerFormBase {
    public Form_Axolotl1(Identifier formID) {
        super(formID);
    }

    private static AnimationHolder anim_swimming_idle = AnimationHolder.EMPTY;


    public AnimationHolder Anim_getFormAnimToPlay(PlayerAnimState currentState) {
        switch (currentState) {
            case ANIM_SWIM_IDLE:
                return anim_swimming_idle;
            default:
                return null;
        }
    }

    public void Anim_registerAnims() {
        anim_swimming_idle = new AnimationHolder(new Identifier(MOD_ID, "axolotl_2_swimming_idle"), true);
    }
}
