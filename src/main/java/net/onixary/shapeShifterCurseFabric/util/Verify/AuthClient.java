package net.onixary.shapeShifterCurseFabric.util.Verify;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

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

    private static byte[] downloadFormURL(String urlString) {
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
        try (InputStream stream = url.openStream()) {
            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to download file from {}", urlString);
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
        // TODO 发布时记得改URL 还得加一个配置项
        return "https://raw.githubusercontent.com/Onixary/ShapeShifterCurseFabricAuth/main/auth/" + uuidStr + ".auth";
    }

    public static void checkUpdate(boolean force) {
        // TODO 需要加一个配置来开关是否自动更新 毕竟是离线系统 想手动更新也行
        if (force || (System.currentTimeMillis() / 1000) - lastUpdateTime > UPDATE_INTERVAL) {
            lastUpdateTime = System.currentTimeMillis();
            saveClientConfig();
            new Thread(() -> {
                String authFileUrl = getAuthFileUrl();
                if (authFileUrl == null) {
                    return;
                }
                byte[] authFileBytes = downloadFormURL(authFileUrl);
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
                // TODO 检查是否在服务器里
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
        } catch (IOException e) {
            return;
        }
    }
}
