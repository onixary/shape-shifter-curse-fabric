package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class VerifyEvent {
    @FunctionalInterface
    public static interface KeyMelt {
        void onKeyMelt(KeySegment oldKeySegment, KeySegment newKeySegment);
    }

    public static final Event<KeyMelt> ON_KEY_MELT = EventFactory.createArrayBacked(KeyMelt.class, callbacks -> (oldKeySegment, newKeySegment) -> {
        for (KeyMelt callback : callbacks) {
            callback.onKeyMelt(oldKeySegment, newKeySegment);
        }
    });
}
