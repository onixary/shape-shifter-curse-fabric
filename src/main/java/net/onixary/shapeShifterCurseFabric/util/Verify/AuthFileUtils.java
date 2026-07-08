package net.onixary.shapeShifterCurseFabric.util.Verify;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

// XuHaoNan:
// 在此警告一下 任何拓展未经允许不得修改此Package中的任何函数/类 理论上调用也没有必要 毕竟没有对应根私钥 无法创建auth文件
// 这个package仅用于验证是否有对应权限 我认为除非想要破解 否则应该没有任何理由修改此类
// 也就是我拓展后续可能会给赞助者加点功能 所以部分field改为了public 否则全都是protected/包protected 以及使用部分注册表系统
// 如果发现 会直接使用检查ModID或其他方式检测来阻止与对应拓展一起启动(让SSC与对应Mod强行不兼容)
// 如果真的有人破解完 并且公开发布的话(私底下破解我不反对 只要别公开/分享出去) 我后续可能会使用一些特殊技术来防止破解 不过我个人十分讨厌在代码里整这种东西 否则按理说应该得给验证逻辑整点加密/混淆

public class AuthFileUtils {
    protected static final List<Pair<BiPredicate<Integer, Integer>, Consumer<PacketByteBuf>>> authFileDataReaders = new ArrayList<>();
    public static final @NotNull KeyFactory Ed448KeyFactory;
    public static final @NotNull KeyPairGenerator Ed448KeyPairGenerator;
    public static final @NotNull String rootPublicKeyPEM = "MEMwBQYDK2VxAzoA775GpvHNH+fuvZ0k293H6TBNCNGVyWaVv50XtEjIeWsupe3/VfxNlOTvuQiIETZy3MDo3Rb/ynwA";
    public static final @NotNull PublicKey rootPublickey;
    static {
        try {
            Ed448KeyFactory = KeyFactory.getInstance("Ed448");
            Ed448KeyPairGenerator = KeyPairGenerator.getInstance("Ed448");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // 现在还没整根密钥 先用测试密钥 为了防止发布时使用公开的测试密钥 随机生成一个 保证任何数据都无法通过验证
        if (ShapeShifterCurseFabric.IsDevelopmentEnvironment()) {
            byte[] publicKeyBytes = Base64.getDecoder().decode(rootPublicKeyPEM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            try {
                rootPublickey = Ed448KeyFactory.generatePublic(keySpec);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        } else {
            rootPublickey = Ed448KeyPairGenerator.generateKeyPair().getPublic();
        }
    }

    public static void requireTrue(boolean condition, String message) {
        if (!condition) { throw new RuntimeException(message); }
    }

    public static void requireTrue(boolean condition) {
        requireTrue(!condition, "Something Wrong");
    }

    public static void requireFalse(boolean condition, String message) {
        requireTrue(!condition, message);
    }

    public static void requireFalse(boolean condition) {
        requireTrue(!condition);
    }

    public static @Nullable PublicKey readEd448PublicKey(byte[] keyBytes) {
        if (keyBytes.length != 57) {
            throw new RuntimeException("Invalid Ed448 public key length");
        }
        try {
            byte[] der = buildEd448PublicKeyDer(keyBytes);
            X509EncodedKeySpec derSpec = new X509EncodedKeySpec(der);
            return Ed448KeyFactory.generatePublic(derSpec);
        } catch (InvalidKeySpecException e) {
            return null;
        }
    }

    public static byte[] buildEd448PublicKeyDer(byte[] rawKey) {
        // 为了文件大小极限的小 所以去除了DER 但是JAVA的Ed448库不支持直接使用Ed448的公钥 所以需要手动构建DER
        byte[] oid = new byte[] {0x06, 0x03, 0x2B, 0x65, 0x70}; // OID 1.3.101.113
        byte[] algSeq = new byte[2 + oid.length];
        algSeq[0] = 0x30;
        algSeq[1] = (byte) oid.length;
        System.arraycopy(oid, 0, algSeq, 2, oid.length);
        int bitStringLen = 1 + rawKey.length;
        byte[] bitString = new byte[2 + bitStringLen];
        bitString[0] = 0x03;
        bitString[1] = (byte) bitStringLen;
        bitString[2] = 0x00; // 未使用位
        System.arraycopy(rawKey, 0, bitString, 3, rawKey.length);
        int totalLen = algSeq.length + bitString.length;
        byte[] der = new byte[2 + totalLen];
        der[0] = 0x30;
        der[1] = (byte) totalLen;
        System.arraycopy(algSeq, 0, der, 2, algSeq.length);
        System.arraycopy(bitString, 0, der, 2 + algSeq.length, bitString.length);
        return der;
    }

    public static boolean verifyEd448Signature(byte[] data, byte[] signature, PublicKey publicKey) {
        // 涉及到线程安全 所以动态getInstance 毕竟有可能从网络线程调用
        try {
            Signature sig = Signature.getInstance("Ed448");
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (SignatureException | InvalidKeyException | NoSuchAlgorithmException e) {
            return false;
        }
    }

    public static void registerAuthFileDataReader(BiPredicate<Integer, Integer> typeVersionPredicate, Consumer<PacketByteBuf> reader) {
        authFileDataReaders.add(new Pair<>(typeVersionPredicate, reader));
    }

    protected static void invokeAuthFileDataReader(PacketByteBuf buf) {
        int type = buf.readVarInt();
        int version = buf.readVarInt();
        for (Pair<BiPredicate<Integer, Integer>, Consumer<PacketByteBuf>> reader : authFileDataReaders) {
            if (reader.getLeft().test(type, version)) {
                buf.setIndex(0, 0);
                reader.getRight().accept(buf);
            }
        }
    }

    // 客户端需要 isVirtual = True 服务器需要 isVirtual = False 当isVirtual=True时 仅会进行读取+验证 不会执行任何回调
    public static @Nullable AuthFile loadAuthFile(InputStream fileStream, boolean isVirtual) {
        try {
            byte[] fileBytes = fileStream.readAllBytes();
            return new AuthFile(fileBytes, isVirtual);
        } catch (Exception e) {
            return null;
        }
    }
}
