package net.onixary.shapeShifterCurseFabric.render.tech;

import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class CocoonModel extends GeoModel<EmptyAnimatable> {
    @Override
    public Identifier getModelResource(EmptyAnimatable animatable) {
        return ShapeShifterCurseFabric.identifier("geo/tech/enemy_cocoon.geo.json");
    }

    @Override
    public Identifier getTextureResource(EmptyAnimatable animatable) {
        return ShapeShifterCurseFabric.identifier("textures/tech/enemy_cocoon.png");
    }

    @Override
    public Identifier getAnimationResource(EmptyAnimatable animatable) {
        return ShapeShifterCurseFabric.identifier("animations/missing.animation.json");
    }
}
