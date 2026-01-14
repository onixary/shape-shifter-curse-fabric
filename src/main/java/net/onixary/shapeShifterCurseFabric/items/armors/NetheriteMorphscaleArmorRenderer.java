package net.onixary.shapeShifterCurseFabric.items.armors;


import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class NetheriteMorphscaleArmorRenderer extends AzArmorRenderer {
    private static final Identifier MODEL = new Identifier(ShapeShifterCurseFabric.MOD_ID,"geo/item/netherite_morphscale_armor.geo.json");
    private static final Identifier TEXTURE = new Identifier(ShapeShifterCurseFabric.MOD_ID,"textures/item/netherite_morphscale_armor.png");

    public NetheriteMorphscaleArmorRenderer() {
        super(AzArmorRendererConfig.builder(MODEL, TEXTURE).build());
    }
}
