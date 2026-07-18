package net.onixary.shapeShifterCurseFabric.render.form_render;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class FormRenderer extends GeoObjectRenderer<FormAnimatable> {
    public FormAnimatable realAnimatable = null;
    public FormModel realModel = null;

    public FormRenderer(JsonObject modelJson) {
        super(new FormModel(modelJson));
        this.realModel = (FormModel) this.model;
        this.realAnimatable = new FormAnimatable();
        this.animatable = this.realAnimatable;
    }

    public void setPlayer(PlayerEntity player, boolean slim) {
        this.realAnimatable.setPlayer(player);
        this.realModel.setPlayer(player, slim);
    }
}
