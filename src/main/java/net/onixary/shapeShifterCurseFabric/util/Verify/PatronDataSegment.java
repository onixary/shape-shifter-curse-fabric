package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.UUID;

public final class PatronDataSegment implements IDataSegment {
    private final int type;
    private final int version;

    private final UUID uuid;
    private final int level;
    private final long expireTime;
    private final HashMap<String, byte[]> extraData = new HashMap<>();

    PatronDataSegment(PacketByteBuf buf) {
        this.type = buf.readVarInt();
        this.version = buf.readVarInt();
        buf.skipBytes(4);
        this.uuid = buf.readUuid();
        this.level = buf.readShort();
        long startTime = buf.readLong();
        this.expireTime = startTime + buf.readLong();
        int extraDataCount = buf.readShort();
        for (int i = 0; i < extraDataCount; i++) {
            String key = buf.readString(256);
            byte[] value = buf.readByteArray(4096);
            extraData.put(key, value);
        }
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public boolean isSameSlot(IDataSegment newDataSegment) {
        return IDataSegment.super.isSameSlot(newDataSegment) && this.uuid.equals(((PatronDataSegment) newDataSegment).uuid);
    }

    public int getLevel() {
        return level;
    }

    public long getExpireTime() {
        return expireTime;
    }

    @Override
    public void onGain() {
        // TODO
    }

    @Override
    public void onLost() {
        // TODO 还原
    }
}
