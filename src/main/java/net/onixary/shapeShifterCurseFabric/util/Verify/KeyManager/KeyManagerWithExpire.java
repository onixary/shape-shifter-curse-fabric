package net.onixary.shapeShifterCurseFabric.util.Verify.KeyManager;

public class KeyManagerWithExpire extends KeyManager {
    private final long ExpireTime;

    public KeyManagerWithExpire(long ExpireTime) {
        this.ExpireTime = ExpireTime;
    }

    // TODO 熔断逻辑
}
