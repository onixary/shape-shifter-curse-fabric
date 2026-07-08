package net.onixary.shapeShifterCurseFabric.util.Verify;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.util.Arrays;

// 由于Java不用签名数据 没写对应的save函数 所以需要存储原始bytes
public class AuthFile {
    private final int MAGIC_NUMBER_LENGTH = 8;
    private final byte[] MAGIC_NUMBER = new byte[] { 0x58, 0x55, 0x53, 0x53, 0x43, 0x4B, 0x45, 0x59 };  // XUSSCKEY
    public final byte[] raw;
    public byte[] keySegment;
    public KeySegment keySegmentObject;

    public AuthFile(byte[] raw) {
        this.raw = raw;
        try {
            this.read(new PacketByteBuf(Unpooled.wrappedBuffer(raw)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void read(PacketByteBuf buf) throws IOException {
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
        keySegmentObject = new KeySegment(keyBuf);
        // 读取数据段Bytes
        // 验证数据段
        // 调用authFileDataReaders中的回调
    }
}
