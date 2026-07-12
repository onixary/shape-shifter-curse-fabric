package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.player_form.utils.IPatronForm;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public final class PatronDataSegment implements IDataSegment {
    private static final HashMap<UUID, PatronDataSegment> PATRON_AUTH_DATA = new HashMap<>();

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
    public void onGain(PlayerEntity player) {
        PATRON_AUTH_DATA.put(uuid, this);
    }

    @Override
    public void onLost(PlayerEntity player) {
        PATRON_AUTH_DATA.remove(uuid);
        if (!FormUtils.isFormCanUse(player, FormUtils.getPlayerForm(player))) {
            FormUtils.applyFallback(player);
        }
    }

    @Override
    public void onUpdate_New(PlayerEntity player, IDataSegment newDataSegment) {
        if (!(newDataSegment instanceof PatronDataSegment patronDataSegment)) {
            ShapeShifterCurseFabric.LOGGER.error("Invalid data segment type");
            return;
        }
        PATRON_AUTH_DATA.put(uuid, patronDataSegment);
        if (!FormUtils.isFormCanUse(player, FormUtils.getPlayerForm(player))) {
            FormUtils.applyFallback(player);
        }
    }

    public static boolean isPatronFormCanUse(PlayerEntity player, IPatronForm form) {
        UUID uuid = player.getUuid();
        PatronDataSegment dataSegment = PATRON_AUTH_DATA.get(uuid);
        return form.checkCanUse(player, dataSegment);
    }

    public static @Nullable PatronDataSegment getPatronDataSegment(PlayerEntity player) {
        return getPatronDataSegment(player.getUuid());
    }

    public static @Nullable PatronDataSegment getPatronDataSegment(UUID uuid) {
        return PATRON_AUTH_DATA.get(uuid);
    }
}
