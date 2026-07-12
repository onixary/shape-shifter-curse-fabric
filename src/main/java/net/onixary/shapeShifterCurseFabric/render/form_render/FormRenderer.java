package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import mod.azure.azurelib.renderer.GeoObjectRenderer;
import net.minecraft.entity.player.PlayerEntity;

public class FormRenderer extends GeoObjectRenderer<FormAnimatable> {
    public FormModel realModel = null;

    public FormRenderer(JsonObject modelJson) {
        super(new FormModel(modelJson));
        this.realModel = (FormModel) this.model;
        this.animatable = new FormAnimatable();
    }

    public void setPlayer(PlayerEntity player, boolean slim) {
        this.animatable.setPlayer(player);
        this.realModel.setPlayer(player, slim);
    }
}
