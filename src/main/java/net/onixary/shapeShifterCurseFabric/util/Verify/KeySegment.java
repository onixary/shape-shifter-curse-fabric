package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.network.PacketByteBuf;

import java.security.PublicKey;

public class KeySegment {
    private final int type;
    private final int version;
    private final boolean useMeltdown;
    private final int[] supportDataTypes;
    private final PublicKey publicKey;

    public KeySegment(PacketByteBuf buf) {
        this.type = buf.readVarInt();
        this.version = buf.readVarInt();
        this.useMeltdown = buf.readBoolean();
        int supportDataTypeCount = buf.readVarInt();
        this.supportDataTypes = new int[supportDataTypeCount];
        for (int i = 0; i < supportDataTypeCount; i++) {
            this.supportDataTypes[i] = buf.readVarInt();
        }
        this.publicKey = AuthFileUtils.readEd448PublicKey(buf.readBytes(57).array());
        if (this.publicKey == null) {
            throw new RuntimeException("Invalid Ed448 public key");
        }
        int keyDataEnd = buf.readerIndex();
        byte[] signature = buf.readBytes(114).array();
        byte[] keyData = new byte[keyDataEnd];
        buf.getBytes(0, keyData);
        AuthFileUtils.requireTrue(AuthFileUtils.verifyEd448Signature(keyData, signature, AuthFileUtils.rootPublickey), "Invalid signature");
    }

    public boolean verify(byte[] data, byte[]signature) {
        return AuthFileUtils.verifyEd448Signature(data, signature, publicKey);
    }
}
