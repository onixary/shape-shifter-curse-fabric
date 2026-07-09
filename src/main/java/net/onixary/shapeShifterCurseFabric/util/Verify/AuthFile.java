package net.onixary.shapeShifterCurseFabric.util.Verify;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.util.Arrays;

// 由于Java不用签名数据 没写对应的save函数 所以需要存储原始bytes
public final class AuthFile {
    private final int MAGIC_NUMBER_LENGTH = 8;
    private final byte[] MAGIC_NUMBER = new byte[] { 0x58, 0x55, 0x53, 0x53, 0x43, 0x4B, 0x45, 0x59 };  // XUSSCKEY
    final byte[] raw;
    byte[] keySegment;
    KeySegment keySegmentObject;

    AuthFile(byte[] raw, boolean isVirtual) {
        this.raw = raw;
        try {
            this.read(new PacketByteBuf(Unpooled.wrappedBuffer(raw)), isVirtual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void read(PacketByteBuf buf, boolean isVirtual) throws IOException {
        int rollBack = 0;
        AuthFileUtils.requireTrue(Arrays.equals(buf.readBytes(MAGIC_NUMBER_LENGTH).array(), MAGIC_NUMBER), "MAGIC_NUMBER does not match");
        int version = buf.readVarInt();
        AuthFileUtils.requireTrue(version == 1, "Unsupported version: " + version);
        // 读取秘钥部分
        rollBack = buf.readerIndex();
        int keySegmentLength = buf.readVarInt();
        buf.setIndex(rollBack, rollBack);
        this.keySegment = buf.readBytes(keySegmentLength).array();
        // 解析密钥段
        PacketByteBuf keyBuf = new PacketByteBuf(Unpooled.wrappedBuffer(this.keySegment));
        this.keySegmentObject = new KeySegment(keyBuf);
        // 读取数据段Bytes
        rollBack = buf.readerIndex();
        int dataSegmentLength = buf.readVarInt();
        buf.setIndex(rollBack, rollBack);
        PacketByteBuf dataBuf = new PacketByteBuf(buf.readBytes(dataSegmentLength));
        // 验证数据段
        byte[] signature = dataBuf.readBytes(114).array();
        byte[] data = dataBuf.readBytes(dataBuf.readableBytes()).array();
        AuthFileUtils.requireTrue(this.keySegmentObject.verify(data, signature), "Signature does not match");
        dataBuf.setIndex(0, 0);
        dataBuf.skipBytes(4);  // dataLength
        int dataCount = dataBuf.readVarInt();
        for (int i = 0; i < dataCount; i++) {
            int dataType = dataBuf.readVarInt();
            dataBuf.skipBytes(4);  // dataVersion
            int dataLength = dataBuf.readVarInt();
            dataBuf.skipBytes(dataLength);
            AuthFileUtils.requireTrue(this.keySegmentObject.isDataTypeValid(dataType), "Invalid data type for key: " + dataType);
        }
        // 调用authFileDataReaders中的回调
        if (!isVirtual) {
            dataBuf.setIndex(0, 0);
            dataBuf.skipBytes(4);  // dataLength
            dataCount = dataBuf.readVarInt();
            for (int i = 0; i < dataCount; i++) {
                rollBack = dataBuf.readerIndex();
                dataBuf.skipBytes(8);
                int dataLength = dataBuf.readVarInt();
                dataBuf.setIndex(rollBack, rollBack);
                AuthFileUtils.invokeAuthFileDataReader(new PacketByteBuf(dataBuf.readBytes(dataLength)));
            }
        }
    }
}
