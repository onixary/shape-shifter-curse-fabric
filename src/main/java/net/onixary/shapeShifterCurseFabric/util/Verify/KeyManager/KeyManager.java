package net.onixary.shapeShifterCurseFabric.util.Verify.KeyManager;

import net.onixary.shapeShifterCurseFabric.util.Verify.KeySegment;
import net.onixary.shapeShifterCurseFabric.util.Verify.VerifyEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class KeyManager {
    // HashMap<KeyType, Key>
    protected HashMap<Integer, KeySegment> keySegments = new HashMap<>();

    public void mountEvent() {
        VerifyEvent.ON_KEY_LOAD.register(this::onKeyLoad);
    }

    public void onKeyLoad(KeySegment keySegment) {
        keySegments.put(keySegment.getType(), keySegment);
    }

    public @Nullable KeySegment getKeySegment(int type) {
        return keySegments.get(type);
    }

    // 感觉没多少用处
    public KeySegment getKeySegment(int type, KeySegment defaultKey) {
        return keySegments.getOrDefault(type, defaultKey);
    }

    public void clear() {
        keySegments.clear();
    }

    public boolean isKeyValid(@Nullable KeySegment keySegment) {
        if (keySegment == null) {
            return false;
        }
        KeySegment oldKeySegment = this.getKeySegment(keySegment.getType());
        return oldKeySegment == null || keySegment.getVersion() >= oldKeySegment.getVersion();
    }
}
