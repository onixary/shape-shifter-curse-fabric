package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RegFormLayer {
    public static HashMap<Identifier, IFormLayerGroup> layerGroupRegistry = new HashMap<>();
    public static HashMap<Identifier, IFormLayer> layerRegistry = new HashMap<>();

    public static @Nullable IFormLayer getLayer(Identifier id) {
        return layerRegistry.get(id);
    }
}
