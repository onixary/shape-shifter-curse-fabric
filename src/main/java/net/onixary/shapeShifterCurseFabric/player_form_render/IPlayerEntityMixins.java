package net.onixary.shapeShifterCurseFabric.player_form_render;

import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;

import java.util.ArrayList;

public interface IPlayerEntityMixins {
    public default boolean betterCombat$isSwinging() {return false;}
    public default void betterCombat$setSwinging(boolean value) {}
    public default boolean originalFur$isPlayerInvisible() {return false;};
    public default ArrayList<Origin> originalFur$currentOrigins() {
        var a = new ArrayList<Origin>();
        a.add(Origin.EMPTY);
        return a;
    }
    public default ArrayList<OriginFurModel> originalFur$getCurrentModels() {
        ArrayList<OriginFurModel> mdls = new ArrayList<>();
        for (var fur : originalFur$getCurrentFurs()){
            mdls.add((OriginFurModel) fur.getGeoModel());
        }
        return mdls;
    }
    public default ArrayList<OriginalFurClient.OriginFur> originalFur$getCurrentFurs() {return new ArrayList<>();}
}
