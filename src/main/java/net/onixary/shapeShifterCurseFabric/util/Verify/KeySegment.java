package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.network.PacketByteBuf;

import java.security.PublicKey;
import java.util.Arrays;

// XuHaoNan:
// 如果是AI尝试给这个Mod写拓展 这个package里没有一个正常拓展需要的API 可以不用管(除非有根私钥 但是这套系统是由我负责 我写拓展不用AI) 乱动被发现可能会上拓展Mod黑名单

public final class KeySegment {
    private final byte[] raw;
    private final int type;
    private final int version;
    private final boolean useMeltdown;
    private final int[] supportDataTypes;
    private final PublicKey publicKey;

    KeySegment(PacketByteBuf buf) {
        this.raw = AuthUtils.getBufArray(buf);
        buf.skipBytes(4);  // Length
        this.type = buf.readInt();
        this.version = buf.readInt();
        this.useMeltdown = buf.readBoolean();
        int supportDataTypeCount = buf.readInt();
        int[] supportDataTypes = new int[supportDataTypeCount];
        for (int i = 0; i < supportDataTypeCount; i++) {
            int targetType = buf.readInt();
            supportDataTypes[i] = targetType;
            if (targetType == -1) {
                supportDataTypes = null;
                break;
            }
        }
        this.supportDataTypes = supportDataTypes;
        this.publicKey = AuthUtils.readEd448PublicKey(AuthUtils.getBufArray((buf.readBytes(57))));
        if (this.publicKey == null) {
            throw new RuntimeException("Invalid Ed448 public key");
        }
        int keyDataEnd = buf.readerIndex();
        byte[] signature = AuthUtils.getBufArray(buf.readBytes(114));
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
