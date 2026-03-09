package net.onixary.shapeShifterCurseFabric.player_form_render;

import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;

import java.util.ArrayList;

public interface IPlayerEntityMixins {
    default boolean betterCombat$isSwinging() {
        return false;
    }

    default void betterCombat$setSwinging(boolean value) {
    }

    default boolean originalFur$isPlayerInvisible() {
        return false;
    }

    default ArrayList<Origin> originalFur$currentOrigins() {
        var a = new ArrayList<Origin>();
        a.add(Origin.EMPTY);
        return a;
    }

    default ArrayList<OriginFurModel> originalFur$getCurrentModels() {
        ArrayList<OriginFurModel> mdls = new ArrayList<>();
        for (var fur : originalFur$getCurrentFurs()){
            var geoModel = fur.getGeoModel();
            if (geoModel != null) {
                mdls.add((OriginFurModel) geoModel);
            }
        }
        return mdls;
    }

    default ArrayList<OriginalFurClient.OriginFur> originalFur$getCurrentFurs() {
        return new ArrayList<>();
    }
}
