package net.onixary.shapeShifterCurseFabric.player_form_render;

import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;

public interface OriginEvents {


    @Unique
    Event<OriginRegistryAdded> ORIGIN_REGISTRY_ADDED_EVENT = EventFactory.createArrayBacked(OriginRegistryAdded.class, callbacks -> (origin, id) -> {
        for (OriginRegistryAdded callback : callbacks) {
            callback.onOriginAddedToRegistry(origin,id);
        }
    });

    @Unique
    Event<OriginGivenToPlayer> ORIGIN_GIVEN_TO_PLAYER = EventFactory.createArrayBacked(OriginGivenToPlayer.class, callbacks -> (origin, id) -> {
        for (OriginGivenToPlayer callback : callbacks) {
            callback.onOriginGivenToPlayer(origin,id);
        }
    });

    @FunctionalInterface
    public interface OriginRegistryAdded {
        void onOriginAddedToRegistry(Origin origin, Identifier id);
    }
    @FunctionalInterface
    public interface OriginGivenToPlayer {
        void onOriginGivenToPlayer(Origin origin, Identifier id);
    }
}
