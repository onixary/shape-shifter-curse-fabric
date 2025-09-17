package net.onixary.shapeShifterCurseFabric.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataStorage {
    private static final Gson GSON = new Gson();
    private static final String DATA_FILE_NAME = "player_data.json";
    private static final String MOD_DATA_DIR = ShapeShifterCurseFabric.MOD_ID;
    private static final Map<Path, JsonObject> cachedData = new ConcurrentHashMap<>();

    public static void initialize(MinecraftServer server) {
        // 这会触发数据迁移和缓存加载
        getCachedData(server);
        ShapeShifterCurseFabric.LOGGER.info("PlayerDataStorage initialized for server.");
    }

    private static Path getNewSavePath(MinecraftServer server) {
        return server.getSavePath(WorldSavePath.ROOT).resolve("data").resolve(MOD_DATA_DIR).resolve(DATA_FILE_NAME);
    }

    private static Path getOldSavePath() {
        return Path.of("config", DATA_FILE_NAME);
    }

    private static void migrateData(MinecraftServer server) {
        Path oldPath = getOldSavePath();
        Path newPath = getNewSavePath(server);
        if (Files.exists(oldPath) && !Files.exists(newPath)) {
            try {
                Files.createDirectories(newPath.getParent());
                Files.copy(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                ShapeShifterCurseFabric.LOGGER.info("Migrated global player data from '{}' to world-specific '{}'", oldPath, newPath);
            } catch (IOException e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to migrate player data.", e);
            }
        }
    }

    public static void savePlayerData(MinecraftServer server, String key, int value) {
        JsonObject data = getCachedData(server);
        data.addProperty(key, value);
        updateCache(server, data);
    }

    public static void savePlayerData(MinecraftServer server, String key, boolean value) {
        JsonObject data = getCachedData(server);
        data.addProperty(key, value);
        updateCache(server, data);
    }

    public static void savePlayerData(MinecraftServer server, String key, String value) {
        JsonObject data = getCachedData(server);
        data.addProperty(key, value);
        updateCache(server, data);
    }

    public static void savePlayerData(MinecraftServer server, String key, float value) {
        JsonObject data = getCachedData(server);
        data.addProperty(key, value);
        updateCache(server, data);
    }

    public static void savePlayerData(MinecraftServer server, String key, JsonObject value) {
        JsonObject data = getCachedData(server);
        data.add(key, value);
        updateCache(server, data);
    }

    public static void savePlayerData(MinecraftServer server, String key, JsonArray value) {
        JsonObject data = getCachedData(server);
        data.add(key, value);
        updateCache(server, data);
    }

    public static void savePlayerData(MinecraftServer server, String key, Enum<?> value) {
        JsonObject data = getCachedData(server);
        data.addProperty(key, value.name());
        updateCache(server, data);
    }

    public static void savePlayerForm(MinecraftServer server, String key, PlayerFormBase state) {
        savePlayerData(server, key, state.FormID.toString());
    }

    public static PlayerFormBase loadPlayerForm(MinecraftServer server, String key){
        JsonObject data = getCachedData(server);
        return data.has(key)? RegPlayerForms.getPlayerForm(data.get(key).getAsString()) : RegPlayerForms.ORIGINAL_SHIFTER;
    }

    public static int loadPlayerDataAsInt(MinecraftServer server, String key) {
        JsonObject data = getCachedData(server);
        return data.has(key) ? data.get(key).getAsInt() : 0;
    }

    public static boolean loadPlayerDataAsBoolean(MinecraftServer server, String key) {
        JsonObject data = getCachedData(server);
        return data.has(key) && data.get(key).getAsBoolean();
    }

    public static String loadPlayerDataAsString(MinecraftServer server, String key) {
        JsonObject data = getCachedData(server);
        return data.has(key) ? data.get(key).getAsString() : "";
    }

    public static JsonObject loadPlayerDataAsJsonObject(MinecraftServer server, String key) {
        JsonObject data = getCachedData(server);
        return data.has(key) ? data.get(key).getAsJsonObject() : new JsonObject();
    }

    public static JsonArray loadPlayerDataAsJsonArray(MinecraftServer server, String key) {
        JsonObject data = getCachedData(server);
        return data.has(key) ? data.get(key).getAsJsonArray() : new JsonArray();
    }

    public static <T extends Enum<T>> T loadPlayerDataAsEnum(MinecraftServer server, String key, Class<T> enumClass) {
        JsonObject data = getCachedData(server);
        return data.has(key) ? Enum.valueOf(enumClass, data.get(key).getAsString()) : null;
    }

    private static JsonObject getCachedData(MinecraftServer server) {
        Path savePath = getNewSavePath(server);
        return cachedData.computeIfAbsent(savePath, path -> loadData(server));
    }

    private static JsonObject loadData(MinecraftServer server) {
        migrateData(server);
        Path dataFile = getNewSavePath(server);
        if (Files.exists(dataFile)) {
            try (FileReader reader = new FileReader(dataFile.toFile())) {
                JsonObject data = GSON.fromJson(reader, JsonObject.class);
                return data != null ? data : new JsonObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new JsonObject();
    }

    private static void updateCache(MinecraftServer server, JsonObject data) {
        Path savePath = getNewSavePath(server);
        cachedData.put(savePath, data);
        saveDataToFile(server, data);
    }

    private static void saveDataToFile(MinecraftServer server, JsonObject data) {
        Path dataFile = getNewSavePath(server);
        try {
            Files.createDirectories(dataFile.getParent());
            try (FileWriter writer = new FileWriter(dataFile.toFile())) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
