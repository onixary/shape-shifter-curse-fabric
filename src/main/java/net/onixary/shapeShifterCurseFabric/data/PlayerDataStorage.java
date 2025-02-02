package net.onixary.shapeShifterCurseFabric.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerDataStorage {
    private static final Gson GSON = new Gson();
    private static final String DATA_FILE_NAME = "config/player_data.json";
    private static JsonObject cachedData = null;

    public static void initialize() {
        cachedData = loadData();
    }

    public static void savePlayerData(String key, int value) {
        JsonObject data = getCachedData();
        data.addProperty(key, value);
        updateCache(data);
    }

    public static void savePlayerData(String key, boolean value) {
        JsonObject data = getCachedData();
        data.addProperty(key, value);
        updateCache(data);
    }

    public static void savePlayerData(String key, String value) {
        JsonObject data = getCachedData();
        data.addProperty(key, value);
        updateCache(data);
    }

    public static void savePlayerData(String key, float value) {
        JsonObject data = getCachedData();
        data.addProperty(key, value);
        updateCache(data);
    }

    public static void savePlayerData(String key, JsonObject value) {
        JsonObject data = getCachedData();
        data.add(key, value);
        updateCache(data);
    }

    public static void savePlayerData(String key, JsonArray value) {
        JsonObject data = getCachedData();
        data.add(key, value);
        updateCache(data);
    }

    public static void savePlayerData(String key, Enum<?> value) {
        JsonObject data = getCachedData();
        data.addProperty(key, value.name());
        updateCache(data);
    }

    public static void savePlayerForm(String key, PlayerForms state) {
        savePlayerData(key, state);
    }

    public static PlayerForms loadPlayerForm(String key){
        JsonObject data = getCachedData();
        return data.has(key)? PlayerForms.valueOf(data.get(key).getAsString()) : PlayerForms.ORIGINAL_SHIFTER;
    }

    public static int loadPlayerDataAsInt(String key) {
        JsonObject data = getCachedData();
        return data.has(key) ? data.get(key).getAsInt() : 0;
    }

    public static boolean loadPlayerDataAsBoolean(String key) {
        JsonObject data = getCachedData();
        return data.has(key) && data.get(key).getAsBoolean();
    }

    public static String loadPlayerDataAsString(String key) {
        JsonObject data = getCachedData();
        return data.has(key) ? data.get(key).getAsString() : "";
    }

    public static JsonObject loadPlayerDataAsJsonObject(String key) {
        JsonObject data = getCachedData();
        return data.has(key) ? data.get(key).getAsJsonObject() : new JsonObject();
    }

    public static JsonArray loadPlayerDataAsJsonArray(String key) {
        JsonObject data = getCachedData();
        return data.has(key) ? data.get(key).getAsJsonArray() : new JsonArray();
    }

    public static <T extends Enum<T>> T loadPlayerDataAsEnum(String key, Class<T> enumClass) {
        JsonObject data = getCachedData();
        return data.has(key) ? Enum.valueOf(enumClass, data.get(key).getAsString()) : null;
    }

    private static File getDataFile() {
        return new File(MinecraftClient.getInstance().runDirectory, DATA_FILE_NAME);
    }

    private static JsonObject getCachedData() {
        if (cachedData == null) {
            cachedData = loadData();
        }
        return cachedData;
    }

    private static JsonObject loadData() {
        File dataFile = getDataFile();
        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile)) {
                return GSON.fromJson(reader, JsonObject.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new JsonObject();
    }

    private static void updateCache(JsonObject data) {
        cachedData = data;
        saveDataToFile(data);
    }

    private static void saveDataToFile(JsonObject data) {
        File dataFile = getDataFile();
        try (FileWriter writer = new FileWriter(dataFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
