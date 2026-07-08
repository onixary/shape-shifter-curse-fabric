package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Pair;

import java.security.PublicKey;
import java.util.Arrays;

public final class KeySegment {
    public final byte[] raw;
    private final int type;
    private final int version;
    private final boolean useMeltdown;
    private final int[] supportDataTypes;
    private final PublicKey publicKey;

    protected KeySegment(PacketByteBuf buf) {
        this.raw = buf.array();
        this.type = buf.readVarInt();
        this.version = buf.readVarInt();
        this.useMeltdown = buf.readBoolean();
        int supportDataTypeCount = buf.readVarInt();
        int[] supportDataTypes = new int[supportDataTypeCount];
        for (int i = 0; i < supportDataTypeCount; i++) {
            int targetType = buf.readVarInt();
            supportDataTypes[i] = targetType;
            if (targetType == -1) {
                supportDataTypes = null;
                break;
            }
        }
        this.supportDataTypes = supportDataTypes;
        this.publicKey = AuthFileUtils.readEd448PublicKey(buf.readBytes(57).array());
        if (this.publicKey == null) {
            throw new RuntimeException("Invalid Ed448 public key");
        }
        int keyDataEnd = buf.readerIndex();
        byte[] signature = buf.readBytes(114).array();
        byte[] keyData = new byte[keyDataEnd];
        buf.getBytes(0, keyData);
        AuthFileUtils.requireTrue(AuthFileUtils.verifyEd448Signature(keyData, signature, AuthFileUtils.rootPublickey), "Invalid signature");
        if (this.isUseMeltdown()) {
            Pair<Boolean, Boolean> canUse = AuthFileUtils.isKeyCanUse(this);
            if (!canUse.getLeft()) {
                throw new RuntimeException("Key has been meltdown");
            }
            if (canUse.getRight()) {
                AuthFileUtils.updateKeySegmentFromKey(this);
            }
        }
    }
    public int getType() {
        return type;
    }

    public int getVersion() {
        return version;
    }

    public boolean isUseMeltdown() {
        return useMeltdown;
    }

    public boolean isDataTypeValid(int dataType) {
        if (supportDataTypes == null) {
            return true;
        }
        return Arrays.stream(supportDataTypes).anyMatch(i -> i == dataType);
    }

    public boolean verify(byte[] data, byte[]signature) {
        return AuthFileUtils.verifyEd448Signature(data, signature, publicKey);
    }
}
