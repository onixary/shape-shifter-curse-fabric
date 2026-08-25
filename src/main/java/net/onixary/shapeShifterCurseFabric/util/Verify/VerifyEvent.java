package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class VerifyEvent {
    @FunctionalInterface
    public static interface KeyMelt {
        void onKeyMelt(KeySegment oldKeySegment, KeySegment newKeySegment);
    }

    @FunctionalInterface
    public static interface KeyLoad {
        void onKeyLoad(KeySegment keySegment);
    }

    public static final Event<KeyMelt> ON_KEY_MELT = EventFactory.createArrayBacked(KeyMelt.class, callbacks -> (oldKeySegment, newKeySegment) -> {
        for (KeyMelt callback : callbacks) {
            callback.onKeyMelt(oldKeySegment, newKeySegment);
        }
    });

    public static final Event<KeyLoad> ON_KEY_LOAD = EventFactory.createArrayBacked(KeyLoad.class, callbacks -> (keySegment) -> {
        for (KeyLoad callback : callbacks) {
            callback.onKeyLoad(keySegment);
        }
    });

    // 每15s检查一次
    public static final Event<ServerTickEvents.EndTick> CHECK_AUTH = EventFactory.createArrayBacked(ServerTickEvents.EndTick.class, callbacks -> (server) -> {
        for (ServerTickEvents.EndTick callback : callbacks) {
            callback.onEndTick(server);
        }
    });
}
