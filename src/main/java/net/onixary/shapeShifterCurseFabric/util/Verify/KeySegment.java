package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.network.PacketByteBuf;

import java.security.PublicKey;
import java.util.Arrays;

public final class KeySegment {
    private final byte[] raw;
    private final int type;
    private final int version;
    private final boolean useMeltdown;
    private final int[] supportDataTypes;
    private final PublicKey publicKey;

    KeySegment(PacketByteBuf buf) {
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
        this.publicKey = AuthUtils.readEd448PublicKey(buf.readBytes(57).array());
        if (this.publicKey == null) {
            throw new RuntimeException("Invalid Ed448 public key");
        }
        int keyDataEnd = buf.readerIndex();
        byte[] signature = buf.readBytes(114).array();
        byte[] keyData = new byte[keyDataEnd];
        buf.getBytes(0, keyData);
        AuthUtils.requireTrue(AuthUtils.verifyEd448Signature(keyData, signature, AuthUtils.rootPublickey), "Invalid signature");
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

    public boolean verify(byte[] data, byte[] signature) {
        return AuthUtils.verifyEd448Signature(data, signature, publicKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeySegment other) {
            return Arrays.equals(this.raw, other.raw);
        }
        return false;
    }

    public boolean softEquals(Object obj) {
        if (obj instanceof KeySegment other) {
            return this.type == other.type && this.version == other.version;
        }
        return false;
    }

    public byte[] getRaw() {
        return raw.clone();
    }

}
