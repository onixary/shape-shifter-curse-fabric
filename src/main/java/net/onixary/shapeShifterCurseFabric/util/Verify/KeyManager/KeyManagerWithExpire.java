package net.onixary.shapeShifterCurseFabric.util.Verify.KeyManager;

import net.minecraft.server.MinecraftServer;
import net.onixary.shapeShifterCurseFabric.util.Verify.KeySegment;
import net.onixary.shapeShifterCurseFabric.util.Verify.VerifyEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class KeyManagerWithExpire extends KeyManager {
    private final long ExpireTime;
    private final HashMap<Integer, HashMap<Integer, Long>> keyExpireMap = new HashMap<>();

    public KeyManagerWithExpire(long ExpireTime) {
        this.ExpireTime = ExpireTime;
    }

    @Override
    public void mountEvent() {
        super.mountEvent();
        VerifyEvent.ON_KEY_MELT.register(this::onKeyMelt);
        VerifyEvent.CHECK_AUTH.register(this::checkExpire);
    }

    public void onKeyMelt(KeySegment oldKeySegment, KeySegment newKeySegment) {
        keyExpireMap.computeIfAbsent(oldKeySegment.getType(), k -> new HashMap<>()).put(oldKeySegment.getVersion(), System.currentTimeMillis() + ExpireTime);
    }

    public boolean isKeyValid(@Nullable KeySegment keySegment) {
        if (keySegment == null) {
            return false;
        }
        if (super.isKeyValid(keySegment)) {
            return true;
        }
        HashMap<Integer, Long> expireMap = keyExpireMap.get(keySegment.getType());
        if (expireMap == null) {
            return false;
        }
        Long expireTime = expireMap.get(keySegment.getVersion());
        return expireTime == null || System.currentTimeMillis() < expireTime;
    }

    public void checkExpire(MinecraftServer server) {
        keyExpireMap.forEach((type, expireMap) -> expireMap.entrySet().removeIf(entry -> System.currentTimeMillis() > entry.getValue()));
    }

    @Override
    public void clear() {
        super.clear();
        keyExpireMap.clear();
    }
}
