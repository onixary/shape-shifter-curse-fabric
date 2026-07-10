package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.util.Arrays;
public final class AuthFile {
    // Const
    private final int MAGIC_NUMBER_LENGTH = 8;
    private final byte[] MAGIC_NUMBER = new byte[] { 0x58, 0x55, 0x53, 0x53, 0x43, 0x4B, 0x45, 0x59 };  // XUSSCKEY

    // Data
    private final byte[] raw;  // 因为在加载时已经调用了read了(验证过了) 所以这个字段可以public 就算修改了客户端的raw 但是服务器端会验证失败 符合最差仅会导致验证失败的设计

    private KeySegment keySegment;
    private IDataSegment[] dataSegments;

    AuthFile(PacketByteBuf buf) {
        this.raw = buf.array();
        try {
            this.read(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void read(PacketByteBuf buf) throws IOException {
        int rollBack = 0;
        AuthUtils.requireTrue(Arrays.equals(buf.readBytes(MAGIC_NUMBER_LENGTH).array(), MAGIC_NUMBER), "MAGIC_NUMBER does not match");
        int version = buf.readVarInt();
        AuthUtils.requireTrue(version == 1, "Unsupported version: " + version);
        // 读取秘钥部分
        rollBack = buf.readerIndex();
        int keySegmentLength = buf.readVarInt();
        buf.setIndex(rollBack, rollBack);
        // 解析密钥段
        PacketByteBuf keyBuf = new PacketByteBuf(buf.readBytes(keySegmentLength));
        this.keySegment = new KeySegment(keyBuf);
        // 读取数据段Bytes
        rollBack = buf.readerIndex();
        int dataSegmentLength = buf.readVarInt();
        buf.setIndex(rollBack, rollBack);
        PacketByteBuf dataBuf = new PacketByteBuf(buf.readBytes(dataSegmentLength));
        // 验证数据段
        byte[] signature = dataBuf.readBytes(114).array();
        byte[] data = dataBuf.readBytes(dataBuf.readableBytes()).array();
        AuthUtils.requireTrue(this.keySegment.verify(data, signature), "Signature does not match");
        dataBuf.setIndex(0, 0);
        dataBuf.skipBytes(4);  // dataLength
        int dataCount = dataBuf.readVarInt();
        for (int i = 0; i < dataCount; i++) {
            int dataType = dataBuf.readVarInt();
            dataBuf.skipBytes(4);  // dataVersion
            int dataLength = dataBuf.readVarInt();
            dataBuf.skipBytes(dataLength);
            AuthUtils.requireTrue(this.keySegment.isDataTypeValid(dataType), "Invalid data type for key: " + dataType);
        }
        dataBuf.setIndex(0, 0);
        dataBuf.skipBytes(4);  // dataLength
        dataCount = dataBuf.readVarInt();
        this.dataSegments = new IDataSegment[dataCount];
        for (int i = 0; i < dataCount; i++) {
            rollBack = dataBuf.readerIndex();
            dataBuf.skipBytes(8);
            int dataLength = dataBuf.readVarInt();
            dataBuf.setIndex(rollBack, rollBack);
            this.dataSegments[i] = AuthUtils.readDataSegment(new PacketByteBuf(dataBuf.readBytes(dataLength)));
        }
    }

    public void onGain() {
        for (IDataSegment segment : this.dataSegments) {
            segment.onGain();
        }
    }

    public void onLost() {
        for (IDataSegment segment : this.dataSegments) {
            segment.onLost();
        }
    }

    public KeySegment getKeySegment() {
        return this.keySegment;
    }

    public byte[] getRaw() {
        return raw.clone();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AuthFile && Arrays.equals(((AuthFile) obj).raw, this.raw);
    }
}
