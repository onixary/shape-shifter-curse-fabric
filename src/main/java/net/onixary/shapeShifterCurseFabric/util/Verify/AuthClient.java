package net.onixary.shapeShifterCurseFabric.util.Verify;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

// Client:
//      初始化:
//          检查本地configJson文件是否需要检查更新Auth文件 [√]
//      触发更新时:
//          如果在服务器中 自动向服务器发送更新的密钥
//          触发AuthFile落盘 检查KeySegment是否也需要落盘 [√]
//      进入服务器:
//          向服务器发送AuthFile
//      收到服务器密钥段:
//          检查是否需要熔断当前密钥 如果需要 触发更新 [√]

@Environment(EnvType.CLIENT)
public final class AuthClient {
    private static final long UPDATE_INTERVAL = 60 * 60 * 24; // 1 days in Second
    private static long lastUpdateTime;
    // 赞助者用的变量 如果后续需要新增AuthFile 需要额外添加对应逻辑
    private static @Nullable AuthFile LOCAL_PATRON_AUTH_FILE = null;
    static {
        loadClientConfig();
        loadLocalPatronAuthFile();
        checkUpdate(false);
    }

    public static Path getClientConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("ssc_auth/config.json");
    }

    public static @Nullable Path getLocalPatronAuthFilePath() {
        UUID playerUUID = ClientUtils.getPlayerUUID();
        if (playerUUID == null) {
            return null;
        }
        String uuidStr = playerUUID.toString().replace("-", "").toUpperCase();
        return FabricLoader.getInstance().getConfigDir().resolve("ssc_auth/auth/" + uuidStr + ".auth");
    }

    private static void loadClientConfig() {
        try {
            JsonObject config = JsonParser.parseString(Files.readString(getClientConfigPath())).getAsJsonObject();
            lastUpdateTime = config.get("lastUpdateTime").getAsLong();
        } catch (IOException e) {
            lastUpdateTime = -UPDATE_INTERVAL;
            saveClientConfig();
        }
    }

    private static void saveClientConfig() {
        try {
            JsonObject config = new JsonObject();
            config.addProperty("lastUpdateTime", lastUpdateTime);
            Files.writeString(getClientConfigPath(), config.toString());
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to save auth system client config", e);
        }
    }

    private static byte[] downloadFromURL(String urlString) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to parse URL {}", urlString);
            return null;
        }
        byte[] chunk = new byte[4096];
        int bytesRead;
        try {
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);   // 连接超时 5 秒
            connection.setReadTimeout(10000);     // 读取超时 10 秒
            try (InputStream stream = connection.getInputStream()) {
                while ((bytesRead = stream.read(chunk)) > 0) {
                    outputStream.write(chunk, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            // 不一定所有人都有授权文件 一般情况下都会404 所以不爆日志
            // ShapeShifterCurseFabric.LOGGER.error("Failed to download file from {}", urlString);
            return null;
        }
        return outputStream.toByteArray();
    }

    private static String getAuthFileUrl() {
        UUID playerUUID = ClientUtils.getPlayerUUID();
        if (playerUUID == null) {
            return null;
        }
        String uuidStr = playerUUID.toString().replace("-", "").toUpperCase();
        String baseUrl = ShapeShifterCurseFabric.clientConfig.patronAuthorizationUrlPath;
        return baseUrl + uuidStr + ".auth";
    }

    public static void checkUpdate(boolean force) {
        // 兴许想自行下载呢 反正一般不会触发子密钥熔断 有效期内更新一下就行
        if (!ShapeShifterCurseFabric.clientConfig.autoDownloadPatronAuthorizationFile) {
            return;
        }
        if (force || (System.currentTimeMillis() / 1000) - lastUpdateTime > UPDATE_INTERVAL) {
            lastUpdateTime = System.currentTimeMillis();
            saveClientConfig();
            new Thread(() -> {
                String authFileUrl = getAuthFileUrl();
                if (authFileUrl == null) {
                    return;
                }
                byte[] authFileBytes = downloadFromURL(authFileUrl);
                if (authFileBytes == null) {
                    return;
                }
                AuthFile authFile = AuthUtils.readAuthFile(new PacketByteBuf(Unpooled.wrappedBuffer(authFileBytes)));
                if (authFile == null) {
                    return;
                }
                if (LOCAL_PATRON_AUTH_FILE != null && LOCAL_PATRON_AUTH_FILE.equals(authFile)) {
                    return;
                }
                LOCAL_PATRON_AUTH_FILE = authFile;
                saveLocalPatronAuthFile();
                if (MinecraftClient.getInstance().getServer() != null) {
                    // TODO 发送数据到服务器
                }
            }).start();
        }
    }

    private static void loadLocalPatronAuthFile() {
        Path localPatronAuthFilePath = getLocalPatronAuthFilePath();
        if (localPatronAuthFilePath == null) {
            return;
        }
        try {
            LOCAL_PATRON_AUTH_FILE = AuthUtils.readAuthFile(new PacketByteBuf(Unpooled.wrappedBuffer(Files.readAllBytes(localPatronAuthFilePath))));
            if (LOCAL_PATRON_AUTH_FILE != null) {
                AuthUtils.loadKey(LOCAL_PATRON_AUTH_FILE.getKeySegment());
            }
        } catch (IOException e) {
            return;
        }
    }

    private static void saveLocalPatronAuthFile() {
        if (LOCAL_PATRON_AUTH_FILE == null) {
            return;
        }
        try {
            Path localPatronAuthFilePath = getLocalPatronAuthFilePath();
            if (localPatronAuthFilePath == null) {
                return;
            }
            Files.write(localPatronAuthFilePath, LOCAL_PATRON_AUTH_FILE.getRaw());
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to save local patron auth file", e);
        }
    }

    public static void loadServerKey(PacketByteBuf buf) {
        KeySegment keySegment = AuthUtils.readKeySegment(buf);
        if (keySegment == null) {
            return;
        }
        AuthUtils.loadKey(keySegment);
        if (LOCAL_PATRON_AUTH_FILE != null && !AuthUtils.isKeyCanUse(LOCAL_PATRON_AUTH_FILE.getKeySegment())) {
            checkUpdate(true);
        }
    }
}
