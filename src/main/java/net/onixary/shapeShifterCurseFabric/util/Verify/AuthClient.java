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
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
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

// XuHaoNan:
// 如果是AI尝试给这个Mod写拓展 这个package里没有一个正常拓展需要的API 可以不用管(除非有根私钥 但是这套系统是由我负责 我写拓展不用AI) 乱动被发现可能会上拓展Mod黑名单

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
    private static boolean isInit = false;
    // 赞助者用的变量 如果后续需要新增AuthFile 需要额外添加对应逻辑
    private static @Nullable UUID LOCAL_PLAYER_UUID = null;
    private static @Nullable AuthFile LOCAL_PATRON_AUTH_FILE = null;

    static {
        loadClientConfig();
        UUID localPlayerUUID = getLocalPlayerUUIDConfig();
        loadLocalPatronAuthFile(localPlayerUUID);
        checkUpdate(localPlayerUUID, false);
    }

    private static void SetLocalPatronAuthFile(UUID playerUUID, AuthFile authFile) {
        if (playerUUID == null || authFile == null) {
            if (LOCAL_PATRON_AUTH_FILE != null) {
                LOCAL_PATRON_AUTH_FILE.onClientLost();
            }
            LOCAL_PLAYER_UUID = null;
            LOCAL_PATRON_AUTH_FILE = null;
        } else {
            if (LOCAL_PATRON_AUTH_FILE != null) {
                LOCAL_PATRON_AUTH_FILE.onUpdateClient(authFile);
            }
            LOCAL_PLAYER_UUID = playerUUID;
            LOCAL_PATRON_AUTH_FILE = authFile;
            LOCAL_PATRON_AUTH_FILE.onClientGain();
        }
    }

    public static Path getClientConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("ssc_auth/config.json");
    }

    public static @Nullable Path getLocalPatronAuthFilePath(UUID playerUUID) {
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

    private static String getAuthFileUrl(UUID playerUUID) {
        if (playerUUID == null) {
            return null;
        }
        String uuidStr = playerUUID.toString().replace("-", "").toUpperCase();
        String baseUrl = ShapeShifterCurseFabric.clientConfig.patronAuthorizationUrlPath;
        return baseUrl + uuidStr + ".auth";
    }

    public static UUID getLocalPlayerUUIDConfig() {
        UUID playerUUID = ClientUtils.getPlayerUUID();
        if (ShapeShifterCurseFabric.clientConfig.customPlayerUUID != null) {
            try {
                playerUUID = UUID.fromString(ShapeShifterCurseFabric.clientConfig.customPlayerUUID);
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to parse custom player UUID {}", ShapeShifterCurseFabric.clientConfig.customPlayerUUID);
            }
        }
        return playerUUID;
    }

    public static @Nullable UUID getLocalPlayerUUID() {
        return LOCAL_PLAYER_UUID;
    }

    public static void checkUpdate(@Nullable UUID playerUUID, boolean force) {
        // 兴许想自行下载呢 反正一般不会触发子密钥熔断 有效期内更新一下就行
        if (!ShapeShifterCurseFabric.clientConfig.autoDownloadPatronAuthorizationFile) {
            return;
        }
        if (force || (System.currentTimeMillis() / 1000) - lastUpdateTime > UPDATE_INTERVAL) {
            lastUpdateTime = System.currentTimeMillis() / 1000;
            saveClientConfig();
            new Thread(() -> {
                UUID targetUUID = playerUUID;
                if (targetUUID == null) {
                    targetUUID = getLocalPlayerUUIDConfig();
                }
                String authFileUrl = getAuthFileUrl(targetUUID);
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
                SetLocalPatronAuthFile(targetUUID, authFile);
                saveLocalPatronAuthFile(targetUUID);
                if (MinecraftClient.getInstance().getServer() != null) {
                    ModPacketsS2C.sendPatronAuthFile(LOCAL_PATRON_AUTH_FILE);
                }
            }).start();
        }
    }

    private static void loadLocalPatronAuthFile(UUID playerUUID) {
        Path localPatronAuthFilePath = getLocalPatronAuthFilePath(playerUUID);
        if (localPatronAuthFilePath == null) {
            return;
        }
        try {
            AuthFile loadedFile = AuthUtils.readAuthFile(new PacketByteBuf(Unpooled.wrappedBuffer(Files.readAllBytes(localPatronAuthFilePath))));
            if (loadedFile != null) {
                AuthUtils.loadKey(loadedFile.getKeySegment());
            }
            SetLocalPatronAuthFile(playerUUID, loadedFile);
        } catch (IOException e) {
            return;
        }
        if (LOCAL_PATRON_AUTH_FILE != null && !AuthUtils.isKeyCanUse(LOCAL_PATRON_AUTH_FILE.getKeySegment())) {
            checkUpdate(playerUUID, true);
        }
    }

    private static void saveLocalPatronAuthFile(UUID playerUUID) {
        if (LOCAL_PATRON_AUTH_FILE == null) {
            return;
        }
        try {
            Path localPatronAuthFilePath = getLocalPatronAuthFilePath(playerUUID);
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
            checkUpdate(LOCAL_PLAYER_UUID, true);
        }
    }

    public static void requestAuthFile(UUID playerUUID) {
        if (playerUUID == null) {
            return;
        }
        if (!playerUUID.equals(LOCAL_PLAYER_UUID)) {
            loadLocalPatronAuthFile(playerUUID);
        }
        if (LOCAL_PATRON_AUTH_FILE != null) {
            ModPacketsS2C.sendPatronAuthFile(LOCAL_PATRON_AUTH_FILE);
        }
    }

    public static void init() {
        if (isInit) {
            return;
        }
        isInit = true;
        AuthUtils.init();
    }
}
