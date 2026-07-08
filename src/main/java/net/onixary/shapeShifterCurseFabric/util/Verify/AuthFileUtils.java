package net.onixary.shapeShifterCurseFabric.util.Verify;

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

public class AuthFileUtils {
    public static final List<Pair<BiPredicate<Integer, Integer>, Consumer<InputStream>>> authFileDataReaders = new ArrayList<>();
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
}
