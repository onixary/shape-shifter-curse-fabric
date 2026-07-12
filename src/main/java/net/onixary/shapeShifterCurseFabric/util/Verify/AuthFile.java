package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.entity.player.PlayerEntity;
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
        this.raw = AuthUtils.getBufArray(buf);;
        try {
            this.read(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void read(PacketByteBuf buf) throws IOException {
        int rollBack = 0;
        AuthUtils.requireTrue(Arrays.equals(AuthUtils.getBufArray(buf.readBytes(MAGIC_NUMBER_LENGTH)), MAGIC_NUMBER), "MAGIC_NUMBER does not match");
        int version = buf.readInt();
        AuthUtils.requireTrue(version == 0, "Unsupported version: " + version);
        // 读取秘钥部分
        rollBack = buf.readerIndex();
        int keySegmentLength = buf.readInt();
        buf.setIndex(rollBack, buf.capacity());
        // 解析密钥段
        PacketByteBuf keyBuf = new PacketByteBuf(buf.readBytes(keySegmentLength));
        this.keySegment = new KeySegment(keyBuf);
        // 读取数据段Bytes
        rollBack = buf.readerIndex();
        int dataSegmentLength = buf.readInt();
        buf.setIndex(rollBack, buf.capacity());
        PacketByteBuf dataBuf = new PacketByteBuf(buf.readBytes(dataSegmentLength));
        // 验证数据段
        byte[] signature = AuthUtils.getBufArray(buf.readBytes(114));
        byte[] data = AuthUtils.getBufArray(dataBuf.readBytes(dataBuf.readableBytes()));
        AuthUtils.requireTrue(this.keySegment.verify(data, signature), "Signature does not match");
        dataBuf.setIndex(0, dataBuf.capacity());
        dataBuf.skipBytes(4);  // dataLength
        int dataCount = dataBuf.readShort();
        for (int i = 0; i < dataCount; i++) {
            int dataType = dataBuf.readInt();
            dataBuf.skipBytes(4);  // dataVersion
            int dataLength = dataBuf.readInt();
            dataBuf.skipBytes(dataLength - 12);  // 包含 Header
            AuthUtils.requireTrue(this.keySegment.isDataTypeValid(dataType), "Invalid data type for key: " + dataType);
        }
        dataBuf.setIndex(0, dataBuf.capacity());
        dataBuf.skipBytes(4);  // dataLength
        dataCount = dataBuf.readShort();
        this.dataSegments = new IDataSegment[dataCount];
        for (int i = 0; i < dataCount; i++) {
            rollBack = dataBuf.readerIndex();
            dataBuf.skipBytes(8);
            int dataLength = dataBuf.readInt();
            dataBuf.setIndex(rollBack, dataBuf.capacity());
            this.dataSegments[i] = AuthUtils.readDataSegment(new PacketByteBuf(dataBuf.readBytes(dataLength)));
        }
    }

    public void onGain(PlayerEntity player) {
        for (IDataSegment segment : this.dataSegments) {
            if (segment == null) {
                return;
            }
            segment.onGain(player);
        }
    }

    public void onLost(PlayerEntity player) {
        for (IDataSegment segment : this.dataSegments) {
            if (segment == null) {
                return;
            }
            segment.onLost(player);
        }
    }

    public void onUpdate(PlayerEntity player, AuthFile newAuthFile) {
        for (IDataSegment segment : this.dataSegments) {
            for (IDataSegment newSegment : newAuthFile.dataSegments) {
                if (segment != null && newSegment != null && segment.isSameSlot(newSegment)) {
                    segment.onUpdate_Old(player, newSegment);
                    newSegment.onUpdate_New(player, segment);
                }
            }
        }
    };

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

    public boolean removeDataSegment(PlayerEntity player, IDataSegment dataSegment) {
        for (int i = 0; i < this.dataSegments.length; i++) {
            if (this.dataSegments[i].equals(dataSegment)) {
                this.dataSegments[i].onLost(player);
                this.dataSegments[i] = null;
                return true;
            }
        }
        return false;
    }
}
