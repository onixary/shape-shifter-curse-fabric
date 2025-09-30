package net.onixary.shapeShifterCurseFabric.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PatronUtils {
    private static String DataPackUrl = "localhost:8888/DataPack.zip";  // 后续由Common Config 提供
    private static String ResourcePackUrl = "localhost:8888/ResourcePack.zip";  // 后续由Common Config 提供

    private static int DataPackVersion = -1;
    private static int ResourcePackVersion = -1;

    private static List<JsonObject> ReadDataPackZip(byte[] dataPackZip) {
        // 单层 <ID.json>
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
                            byte[] bytes = new byte[32767];  // 最大支持32K String
                            int Size = zipInputStream.read(bytes);
                            if (zipInputStream.read(new byte[1]) != -1) {
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
                    zipInputStream.closeEntry();
                    zipEntry = zipInputStream.getNextEntry();
                }
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

    public static boolean NeedUpdateDataPack() {
        // 如果无法获取版本号 则默认不需要(没法)更新
        int WebDataPackVersion = -1;
        // TODO 版本获取
        return WebDataPackVersion > DataPackVersion;
    }

    public static byte[] getNewestDataPack() {
        // TODO 本地缓存
        // **** 此DataPack非标准数据包 为单层 <id>.json 的ssc_form文件 !!!! 不可以放在数据包文件夹 !!!! ****
        // !!!! 如果发现缓存到数据包文件夹 请及时通知修改代码 !!!!
        return downloadFormURL(DataPackUrl);
    }

    public static boolean NeedUpdateResourcePack() {
        // 如果无法获取版本号 则默认不需要(没法)更新
        int WebResourcePackVersion = -1;
        // TODO 版本获取
        return WebResourcePackVersion > ResourcePackVersion;
    }

    public static void ApplyNewestResourcePack() {
        if (!NeedUpdateResourcePack()) {
            // TODO 本地下载到资源包文件夹
        }
        // TODO 应用资源包
        return;
    }

    // TODO 挂载逻辑 添加每24h更新 + 服务器启动时更新
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
}
