package net.onixary.shapeShifterCurseFabric.player_form.ability;

import net.minecraft.util.Identifier;

import java.util.HashMap;

public class RegFormLayer {
    public static HashMap<Identifier, IFormLayerGroup> layerGroupRegistry = new HashMap<>();
    public static HashMap<Identifier, IFormLayer> layerRegistry = new HashMap<>();
}
