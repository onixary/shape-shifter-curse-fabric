package net.onixary.shapeShifterCurseFabric.util.Verify;

import io.netty.buffer.Unpooled;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

// XuHaoNan:
// 在此警告一下 任何拓展未经允许不得修改此Package中的任何函数/类 理论上调用也没有必要 毕竟没有对应根私钥 无法创建auth文件
// 这个package仅用于验证是否有对应权限 我认为除非想要破解 否则应该没有任何理由修改此类
// 也就是我拓展后续可能会给赞助者加点功能 所以部分field改为了public 否则全都是protected/包protected 以及使用部分注册表系统
// 如果发现 会直接使用检查ModID或其他方式检测来阻止与对应拓展一起启动(让SSC与对应Mod强行不兼容)
// 如果真的有人破解完 并且公开发布的话(私底下破解我不反对 只要别公开/分享出去) 我后续可能会使用一些特殊技术来防止破解 不过我个人十分讨厌在代码里整这种东西 否则按理说应该得给验证逻辑整点加密/混淆

// Java端额外设计指标
// 在这个package里 任意public函数 或非final的public类field 外部随意调用/修改 不可导致密钥系统被破解(比如调用/修改几个public函数或field导致其他玩家获得赞助者权限) 但可以允许破坏验证(所有验证全部失败是允许的)

// 运行流程
// Common:
//      初始化:
//          初始化 AuthFileUtils 联动调用读取本地Key
// Client:
//      初始化:
//          检查本地configJson文件是否需要检查更新Auth文件
//      触发更新时:
//          如果在服务器中 自动向服务器发送更新的密钥
//          触发AuthFile落盘 检查KeySegment是否也需要落盘
//      进入服务器:
//          向服务器发送AuthFile
//      收到服务器密钥段:
//          检查是否需要熔断当前密钥 如果需要 触发更新
// Server:
//      玩家进入时:
//          开始30s计时 等待AuthFile 先不执行还原
//      收到客户端AuthFile:
//          检查本地Key 如果AuthFile需要熔断当前密钥 触发熔断
//          将AuthFile写入内存
//      密钥熔断时:
//          将旧Key写入forgive组 并使用新Key替换 并将新Key落盘
//          向所有玩家发送新密钥
//      每5s:
//          给每个玩家检查内存中是否有有效认证文件Object 如果没有 触发回调中的还原
//          检查forgive组是否有失效密钥 如果有失效 对当前存储的AuthFile进行检查 如果有AuthFile失效 触发回调中的还原

// 暂存(今天状态不太行了 明天再继续吧)
// 需要把KeySegment/AuthFile加载区分是否为服务器端
// 回调需要读取PacketByteBuf 返回一个Object 拥有撤销等回调


// 公开功能 其余全是package-private 对于验证代码而言 外部所有调用都可能为恶意的 必须得做防护
// - 初始化AuthFileUtils(需要由SSC初始化时初始化) 联动读取本地KeySegment
// - 加载新的AuthFile 参数为buffer 用于网络 返回AuthFile
// - 加载新的KeySegment 参数为buffer 用于网络 返回KeySegment
// - 注册数据段回调(需要把回调改为class 新增失效时的回调)
// - ...


public final class AuthFileUtils {
    private static final List<Pair<BiPredicate<Integer, Integer>, Consumer<PacketByteBuf>>> authFileDataReaders = new ArrayList<>();
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
    private static final HashMap<Integer, KeySegment> storedKeySegments = new HashMap<>();
    private static final List<Pair<Long, KeySegment>> forgiveKeySegments = new ArrayList<>();
    private static final long forgiveTime = 60 * 30;  // 30分钟
    public static @Nullable AuthFile clientSideAuthFile = null;
    static {
        loadLocalKeySegments();
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

    private static @Nullable KeySegment loadKeySegment(InputStream fileStream) {
        try {
            byte[] fileBytes = fileStream.readAllBytes();
            return new KeySegment(new PacketByteBuf(Unpooled.wrappedBuffer(fileBytes)));
        } catch (Exception e) {
            return null;
        }
    }

    private Path getLocalKeyFolderPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("ssc_auth/keys");
    }

    protected static void loadLocalKeySegments() {
        try {
            Path folderPath = new AuthFileUtils().getLocalKeyFolderPath();
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
                for (Path path : stream) {
                    String fileName = path.getFileName().toString();
                    if (fileName.endsWith(".key") && fileName.matches("\\d+\\.key")) {
                        try (InputStream is = Files.newInputStream(path)) {
                            KeySegment segment = loadKeySegment(is);
                            if (segment != null) {
                                storedKeySegments.put(segment.getType(), segment);
                            }
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void updateKeySegmentFromKey(KeySegment keySegment) {
        if (!keySegment.isUseMeltdown()) {
            return;
        }
        KeySegment oldKey = storedKeySegments.put(keySegment.getType(), keySegment);
        if (oldKey != null) {
            long nowTime = System.currentTimeMillis() / 1000;
            forgiveKeySegments.add(new Pair<>(nowTime, oldKey));
        }
        saveKeySegment(keySegment);
    }

    protected static void saveKeySegment(KeySegment keySegment) {
        Path folderPath = new AuthFileUtils().getLocalKeyFolderPath();
        try {
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            Path filePath = folderPath.resolve(keySegment.getType() + ".key");
            Files.write(filePath, keySegment.raw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateForgiveKeySegments() {
        long nowTime = System.currentTimeMillis() / 1000;
        forgiveKeySegments.removeIf(pair -> pair.getLeft() + forgiveTime < nowTime);
    }

    public static Pair<Boolean, Boolean> isKeyCanUse(KeySegment keySegment) {
        // 返回值为 Pair<Boolean, Boolean> 第一个值表示是否可用 第二个值表示是否需要更新
        updateForgiveKeySegments();
        int Type = keySegment.getType();
        int Version = keySegment.getVersion();
        KeySegment storedKeySegment = storedKeySegments.get(Type);
        if (storedKeySegment == null) {
            return new Pair<>(true, true);
        }
        if (storedKeySegment.getVersion() < Version) {
            return new Pair<>(true, true);
        } else if (storedKeySegment.getVersion() == Version) {
            return new Pair<>(true, false);
        } else {
            for (Pair<Long, KeySegment> segmentPair : forgiveKeySegments) {
                if (segmentPair.getLeft() == Type) {
                    return new Pair<>(true, false);
                }
            }
            return new Pair<>(false, false);
        }
    }
}
