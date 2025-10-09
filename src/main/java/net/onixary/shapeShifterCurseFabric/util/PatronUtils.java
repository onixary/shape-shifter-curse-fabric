package net.onixary.shapeShifterCurseFabric.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.commonConfig;

public class PatronUtils {

    private static final String DataPackVersionName = "SSC-Patron-Data-Version.txt";
    private static final String DataPackName = "SSC-Patron-DataPack.zip";
    private static final String ResourcePackVersionName = "SSC-Patron-Resource-Version.txt";
    private static final String ResourcePackName = "SSC-Patron-ResourcePack.zip";
    private static final String PatronDataName = "SSC-Patron-Data.json";

    private static final Path ResourcePackPath = Path.of("resourcepacks").resolve(ResourcePackName);

    private static int DataPackVersion = -1;
    private static int ResourcePackVersion = -1;

    public static HashMap<UUID, Integer> PatronLevels = new HashMap<>();  // 服务端客户端缓存 通过网络同步

    public static void OnClientInit() {
        if (commonConfig.enablePatronFormSystem) {
            PatronUtils.ApplyNewestResourcePack();
        }
    }

    public static void OnServerLoad(MinecraftServer server) {
        PatronUtils.UpdatePatronData(server);
        if (commonConfig.enablePatronFormSystem) {
            // 开启服务器时无论如何都要更新一次
            PatronUtils.UpdateDataPack(server);
        }
        Thread thread = new Thread(() -> {
            try {
                long SleepTime = commonConfig.CheckUpdateInterval;
                if (SleepTime <= 0) {
                    return;
                }
                Thread.sleep(SleepTime * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            PatronUtils.UpdatePatronData(server);
            if (commonConfig.enablePatronFormSystem) {
                PatronUtils.CheckDataPackUpdate(server);
            }
        });
    }

    private static List<JsonObject> ReadDataPackZip(byte[] dataPackZip) {
        // 单层 <ID.json>
        if (dataPackZip == null) {
            return new LinkedList<>();
        }
        List<JsonObject> jsonObjects = new LinkedList<JsonObject>();
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(dataPackZip));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                // 不支持多层目录
                if (!zipEntry.isDirectory()) {
                    String fileName = zipEntry.getName();
                    if (!fileName.endsWith(".json")) {
                        ShapeShifterCurseFabric.LOGGER.warn("DataPack zip contains non-json file: {}", fileName);
                    }
                    else {
                        try {
                            // 读取json文件
                            byte[] bytesChunk = new byte[1024 * 32];
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            int bytesRead;
                            while ((bytesRead = zipInputStream.read(bytesChunk)) > 0) {
                                outputStream.write(bytesChunk, 0, bytesRead);
                            }
                            byte[] bytes = outputStream.toByteArray();
                            if (bytes.length > 32767) {
                                ShapeShifterCurseFabric.LOGGER.error("DataPack zip contains too large json file: {}", fileName);
                                throw new Exception("DataPack zip contains too large json file");
                            }
                            String jsonStr = new String(bytes, StandardCharsets.UTF_8);
                            JsonObject jsonObject = new Gson().fromJson(jsonStr, JsonObject.class);
                            jsonObjects.add(jsonObject);
                        }
                        catch (Exception e) {
                            ShapeShifterCurseFabric.LOGGER.error("Failed to read json file: {}", fileName, e);
                        }
                    }
                }
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
            return jsonObjects;
        }
        catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to read DataPack zip", e);
        }
        return null;
    }

    public static byte[] downloadFormURL(String urlString) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            URL url = new URL(urlString);
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = url.openStream();
            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to download file from {}", urlString, e);
            return null;
        }
        return outputStream.toByteArray();
    }

    public static int getVersion(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String versionStr = reader.readLine();
            return Integer.parseInt(versionStr);
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to get version from {}", urlString, e);
            return -1;
        }
    }

    public static int getVersionLocal(String LocalPath) {
        Path LocalDataPackVersion = ShapeShifterCurseFabric.MOD_LOCAL_DATA_STORAGE.resolve(LocalPath);
        if (!LocalDataPackVersion.toFile().exists()) {
            return -1;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(LocalDataPackVersion.toFile()));
            String versionStr = reader.readLine();
            return Integer.parseInt(versionStr);
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to get version from {}", LocalPath, e);
            return -1;
        }
    }

    public static void CheckDataPackUpdate(MinecraftServer server) {
        if (NeedUpdateDataPack()) {
            UpdateDataPack(server);
        }
    }

    public static boolean NeedUpdateDataPack() {
        // 如果无法获取版本号 则默认不需要(没法)更新
        int WebDataPackVersion = getVersion(commonConfig.DataPackVersionUrl);
        DataPackVersion = getVersionLocal(DataPackVersionName);
        return WebDataPackVersion > DataPackVersion;
    }

    public static byte[] getNewestDataPack() {
        // **** 此DataPack非标准数据包 为单层 <id>.json 的ssc_form文件 !!!! 不可以放在数据包文件夹 !!!! ****
        // !!!! 如果发现缓存到数据包文件夹 请及时通知修改代码 !!!!
        Path LocalDataPack = ShapeShifterCurseFabric.MOD_LOCAL_DATA_STORAGE.resolve(DataPackName);
        Path LocalDataPackVersion = ShapeShifterCurseFabric.MOD_LOCAL_DATA_STORAGE.resolve(DataPackVersionName);
        int WebDataPackVersion = getVersion(commonConfig.DataPackVersionUrl);
        if (WebDataPackVersion > DataPackVersion) {
            byte[] dataPackZip = downloadFormURL(commonConfig.DataPackUrl);
            if (dataPackZip != null) {
                try {
                    Files.write(LocalDataPack, dataPackZip);
                    Files.writeString(LocalDataPackVersion, String.valueOf(WebDataPackVersion));
                } catch (IOException e) {
                    ShapeShifterCurseFabric.LOGGER.error("Failed to write local DataPack", e);
                }
            }
            return dataPackZip;
        }
        if (LocalDataPack.toFile().exists()) {
            try {
                return Files.readAllBytes(LocalDataPack);
            }
            catch (IOException e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to read local DataPack", e);
            }
        }
        return null;
    }

    public static boolean NeedUpdateResourcePack() {
        // 如果无法获取版本号 则默认不需要(没法)更新
        int WebResourcePackVersion = getVersion(commonConfig.ResourcePackVersionUrl);
        ResourcePackVersion = getVersionLocal(ResourcePackVersionName);
        return WebResourcePackVersion > ResourcePackVersion;
    }

    public static void ApplyNewestResourcePack() {
        if (NeedUpdateResourcePack()) {
            Path ResourcePackVersion = ShapeShifterCurseFabric.MOD_LOCAL_DATA_STORAGE.resolve(ResourcePackVersionName);
            int WebResourcePackVersion = getVersion(commonConfig.ResourcePackVersionUrl);
            if (WebResourcePackVersion != -1) {
                byte[] resourcePackZip = downloadFormURL(commonConfig.ResourcePackUrl);
                if (resourcePackZip != null) {
                    try {
                        Files.write(ResourcePackPath, resourcePackZip);
                        Files.writeString(ResourcePackVersion, String.valueOf(WebResourcePackVersion));
                    } catch (IOException e) {
                        ShapeShifterCurseFabric.LOGGER.error("Failed to write local ResourcePack", e);
                    }
                }
            }
            else {
                ShapeShifterCurseFabric.LOGGER.error("Failed to get resource pack");
                return;
            }
        }
        ApplyResourcePack("file/" + ResourcePackName);
        return;
    }

    public static void ApplyResourcePack(String ResourcePackName) {
        Path GameConfig = Path.of("options.txt");
        if (!GameConfig.toFile().exists()) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to find options.txt, It Should Not Happen!");
            return;
        }
        try {
            List<String> configLines = Files.readAllLines(GameConfig);
            for (int i = 0; i < configLines.size(); i++) {
                if (!configLines.get(i).startsWith("resourcePacks")) {
                    continue;
                }
                String ResourcePackList = configLines.get(i).replaceFirst("resourcePacks:", "");
                List<String> ResourcePackListArray = new Gson().fromJson(ResourcePackList, new TypeToken<List<String>>(){}.getType());
                if (ResourcePackListArray.contains(ResourcePackName)) {
                    return;
                }
                ResourcePackListArray.add(ResourcePackName);
                configLines.set(i, "resourcePacks:" + new Gson().toJson(ResourcePackListArray));
                Files.write(GameConfig, configLines);
                ShapeShifterCurseFabric.LOGGER.info("Resource Pack Applied");
                return;
            }
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to modify options.txt", e);
            return;
        }
    }

    // **** 此DataPack非标准数据包 为单层 <id>.json 的ssc_form文件 ****
    public static void UpdateDataPack(MinecraftServer server) {
        List<JsonObject> jsonObjects = ReadDataPackZip(getNewestDataPack());
        List<Identifier> patronForms = new ArrayList<>();
        if (jsonObjects != null) {
            for (JsonObject jsonObject : jsonObjects) {
                PlayerFormDynamic pfd = null;
                try {
                    pfd = PlayerFormDynamic.of(jsonObject);
                }
                catch (IllegalArgumentException e) {
                    ShapeShifterCurseFabric.LOGGER.error("Failed to parse PlayerFormDynamic from json No FormID", e);
                    continue;
                }
                if (!pfd.FormID.getNamespace().equals(RegPlayerForms.PatronNameSpace)) {
                    ShapeShifterCurseFabric.LOGGER.warn("DataPack contains non-patron PlayerFormDynamic: {}", pfd.FormID);
                    continue;
                }
                Identifier formID = RegPlayerForms.registerDynamicPlayerForm(pfd).FormID;
                if (!patronForms.contains(formID)) {
                    patronForms.add(formID);
                }
            }
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ModPacketsS2CServer.updatePatronForms(player, patronForms);
            }
        }
        else {
            ShapeShifterCurseFabric.LOGGER.error("Failed to Update Patron Forms");
        }
    }

    // 仅服务端
    public static void UpdatePatronData(MinecraftServer server) {
        byte[] PatronDataByte = downloadFormURL(commonConfig.PatronDataUrl);
        if (PatronDataByte != null) {
            try {
                Files.write(ShapeShifterCurseFabric.MOD_LOCAL_DATA_STORAGE.resolve(PatronDataName), PatronDataByte);
            } catch (IOException e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to write local PatronData", e);
            }
        }
        else {
            ShapeShifterCurseFabric.LOGGER.error("Failed to get patron data");
            try {
                PatronDataByte = Files.readAllBytes(ShapeShifterCurseFabric.MOD_LOCAL_DATA_STORAGE.resolve(PatronDataName));
            } catch (IOException e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to read local PatronData", e);
            }
        }
        if (PatronDataByte != null) {
            try {
                JsonObject PatronData = new Gson().fromJson(new String(PatronDataByte), JsonObject.class);
                JsonArray PatronLevelList = PatronData.getAsJsonArray("patron_level");
                for (JsonElement PatronLevel : PatronLevelList) {
                    if (PatronLevel.isJsonObject()) {
                        JsonObject PatronLevelObject = PatronLevel.getAsJsonObject();
                        UUID PlayerUUID = UUID.fromString(PatronLevelObject.get("uuid").getAsString());
                        int Level = PatronLevelObject.get("level").getAsInt();
                        PatronLevels.put(PlayerUUID, Level);
                    }
                }
                ModPacketsS2CServer.updatePatronLevel(server);
            }
            catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to parse PatronData", e);
                return;
            }
        }
    }

    // 仅客户端
    public static void ApplyPatronLevel(HashMap<UUID, Integer> NewPatronLevels) {
        PatronLevels.clear();
        for (Map.Entry<UUID, Integer> entry : NewPatronLevels.entrySet()) {
            if (entry.getValue() > 0) {
                PatronLevels.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
