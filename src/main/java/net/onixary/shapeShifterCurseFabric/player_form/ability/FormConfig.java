package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_animation.PlayerAnimState;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormPhase;

import java.util.Set;

public class FormConfig {
    //private final float maxHealth;
    //private final float armor;
    private final String formOriginLayerID;
    private final String formOriginID;
    //private final float scale;
    private PlayerFormPhase phase;
    private PlayerFormBodyType bodyType;
    // mark witch anim states to override
    //private final Identifier powerId;

    public FormConfig(String formOriginLayerID, String formOriginID, PlayerFormPhase phase, PlayerFormBodyType bodyType) {
        this.formOriginLayerID = formOriginLayerID;
        this.formOriginID = formOriginID;
        //this.scale = scale;
        this.phase = phase;
        this.bodyType = bodyType;
    }

    //public float getScale() { return scale; }
    public String getFormOriginLayerID() { return formOriginLayerID; }
    public String getFormOriginID() { return formOriginID; }
    public PlayerFormPhase getPhase() { return phase; }
    public PlayerFormBodyType getBodyType() { return bodyType; }
}
