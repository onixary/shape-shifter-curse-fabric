package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public final class DebuggerDataSegment implements IDataSegment {
    private static final HashMap<UUID, DebuggerDataSegment> PATRON_AUTH_DATA = new HashMap<>();
    private final int type;
    private final int version;
    private final UUID uuid;
    private final int level;
    private final long expireTime;

    DebuggerDataSegment(KeySegment key, PacketByteBuf buf) {
        int type = buf.readInt();
        if (type != 2) {
            throw new RuntimeException("Invalid Debugger Data Segment");
        }
        this.type = type;
        this.version = buf.readInt();
        buf.skipBytes(4);
        this.uuid = buf.readUuid();
        this.level = buf.readShort();
        long startTime = buf.readLong();
        long expiresIn = buf.readLong();
        this.expireTime = startTime + expiresIn;
        long realExpireTime = expireTime * 1000;
        if (realExpireTime < System.currentTimeMillis()) {
            return;
        }
        if (AuthUtils.isKeyValid(key)) {
            PATRON_AUTH_DATA.put(uuid, this);
        }
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    public int getLevel() {
        return this.level;
    }

    public static int getLevel(@Nullable PlayerEntity player) {
        if (player == null) {
            return 0;
        }
        DebuggerDataSegment dataSegment = PATRON_AUTH_DATA.get(player.getUuid());
        if (dataSegment == null) {
            return 0;
        }
        return dataSegment.getLevel();
    }

    @Override
    public boolean isTypeEqual(IDataSegment newDataSegment) {
        return IDataSegment.super.isTypeEqual(newDataSegment);
    }

    @Override
    public boolean isSameSlot(IDataSegment newDataSegment) {
        return IDataSegment.super.isSameSlot(newDataSegment) && this.uuid.equals(((DebuggerDataSegment) newDataSegment).uuid);
    }
}
