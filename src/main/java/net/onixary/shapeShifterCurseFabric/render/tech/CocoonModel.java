package net.onixary.shapeShifterCurseFabric.render.tech;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import software.bernie.geckolib.model.GeoModel;

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
