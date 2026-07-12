package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RegFormLayer {
    public static HashMap<Identifier, IFormLayerGroup> layerGroupRegistry = new HashMap<>();
    public static HashMap<Identifier, IFormLayer> layerRegistry = new HashMap<>();

    public static @Nullable IFormLayer getLayer(Identifier id) {
        return layerRegistry.get(id);
    }

    public static @Nullable IFormLayerGroup getLayerGroup(Identifier id) {
        return layerGroupRegistry.get(id);
    }

    public static @NotNull IFormLayer getLayerOrDefault(Identifier id, @NotNull IFormLayer defaultLayer) {
        return layerRegistry.getOrDefault(id, defaultLayer);
    }

    public static @NotNull IFormLayerGroup getLayerGroupOrDefault(Identifier id, @NotNull IFormLayerGroup defaultLayerGroup) {
        return layerGroupRegistry.getOrDefault(id, defaultLayerGroup);
    }
}
