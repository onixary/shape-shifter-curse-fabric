package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.util.Identifier;

public class FormConfig {
    //private final float maxHealth;
    //private final float armor;
    private final String formOriginLayerID;
    private final String formOriginID;
    private final float scale;
    //private final Identifier powerId;

    public FormConfig(String formOriginLayerID,String formOriginID, float scale) {
        this.formOriginLayerID = formOriginLayerID;
        this.formOriginID = formOriginID;
        this.scale = scale;
    }

    public float getScale() { return scale; }
    public String getFormOriginLayerID() { return formOriginLayerID; }
    public String getFormOriginID() { return formOriginID; }
}
