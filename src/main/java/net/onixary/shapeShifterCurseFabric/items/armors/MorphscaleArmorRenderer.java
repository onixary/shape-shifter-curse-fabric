package net.onixary.shapeShifterCurseFabric.items.armors;

import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MorphscaleArmorRenderer extends GeoArmorRenderer<MorphScaleArmor> {
    private static final Identifier MODEL = new Identifier(ShapeShifterCurseFabric.MOD_ID,"morphscale_armor");

    public MorphscaleArmorRenderer() {
        super(new DefaultedItemGeoModel<>(MODEL));
    }
}
