package net.onixary.shapeShifterCurseFabric.items.armors;


import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class MorphscaleArmorRenderer extends AzArmorRenderer {
    private static final Identifier MODEL = new Identifier(ShapeShifterCurseFabric.MOD_ID,"geo/item/morphscale_armor.geo.json");
    private static final Identifier TEXTURE = new Identifier(ShapeShifterCurseFabric.MOD_ID,"textures/item/morphscale_armor.png");

    public MorphscaleArmorRenderer() {
        super(AzArmorRendererConfig.builder(MODEL, TEXTURE).build());
    }
}
