package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.player_form.utils.IPatronForm;
import net.onixary.shapeShifterCurseFabric.util.Verify.KeyManager.KeyManagerWithExpire;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class PatronDataSegment implements IDataSegment {
    private static final HashMap<UUID, PatronDataSegment> PATRON_AUTH_DATA = new HashMap<>();
    private static final KeyManagerWithExpire KEY_MANAGER = new KeyManagerWithExpire(60 * 30 * 1000);

    static {
        KEY_MANAGER.mountEvent();
        VerifyEvent.CHECK_AUTH.register(PatronDataSegment::checkExpire_STATIC);
    }

    private final int type;
    private final int version;

    private final UUID uuid;
    private final int level;
    private final long expireTime;
    private final HashMap<String, byte[]> extraData = new HashMap<>();

    private final KeySegment key;

    PatronDataSegment(KeySegment key, PacketByteBuf buf) {
        this.type = buf.readInt();
        this.version = buf.readInt();
        buf.skipBytes(4);
        this.uuid = buf.readUuid();
        this.level = buf.readShort();
        long startTime = buf.readLong();
        long expiresIn = buf.readLong();
        this.expireTime = startTime + expiresIn;
        int extraDataCount = buf.readShort();
        for (int i = 0; i < extraDataCount; i++) {
            String k = buf.readString(256);
            byte[] v = buf.readByteArray(4096);
            extraData.put(k, v);
        }
        this.key = key;
        if (!KEY_MANAGER.isKeyValid(key)) {
            return;
        }
        PATRON_AUTH_DATA.put(uuid, this);
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

    public byte[] getExtraData(String key) {
        return extraData.get(key);
    }


    public static boolean isPatronFormCanUse(@Nullable PlayerEntity player, @NotNull IPatronForm form) {
        if (player == null) return false;
        UUID uuid = null;
        if (player.isMainPlayer()) {
            uuid = AuthClient.getLocalPlayerUUID();
        }
        if (uuid == null) {
            uuid = player.getUuid();
        }
        PatronDataSegment dataSegment = PATRON_AUTH_DATA.get(uuid);
        return form.checkCanUse(player, uuid, dataSegment);
    }

    public static @Nullable PatronDataSegment getPatronDataSegment(PlayerEntity player) {
        return getPatronDataSegment(player.getUuid());
    }

    public static @Nullable PatronDataSegment getPatronDataSegment(UUID uuid) {
        return PATRON_AUTH_DATA.get(uuid);
    }

    private static void checkExpire_STATIC(MinecraftServer server) {
        List<UUID> shouldRemove = new ArrayList<>();
        for (PatronDataSegment dataSegment : PATRON_AUTH_DATA.values()) {
            if (dataSegment.checkExpire(server)) {
                shouldRemove.add(dataSegment.uuid);
            }
        }
        for (UUID uuid : shouldRemove) {
            PatronDataSegment dataSegment = PATRON_AUTH_DATA.get(uuid);
            PATRON_AUTH_DATA.remove(uuid);
            dataSegment.onLost(server);
        }
    }

    private boolean checkExpire(MinecraftServer server) {
        long realExpireTime = expireTime * 1000;
        if (realExpireTime < System.currentTimeMillis()) {
            return true;
        }
        if (!KEY_MANAGER.isKeyValid(key)) {
            return true;
        }
        return false;
    }

    private void onLost(MinecraftServer server) {
        PlayerEntity currentPlayer = server.getPlayerManager().getPlayer(uuid);
        if (currentPlayer == null) {
            return;
        }
        if (!FormUtils.isFormCanUse(currentPlayer, FormUtils.getPlayerForm(currentPlayer))) {
            FormUtils.applyFallback(currentPlayer);
        }
    }
}