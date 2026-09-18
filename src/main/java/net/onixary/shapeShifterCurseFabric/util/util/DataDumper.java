package net.onixary.shapeShifterCurseFabric.util.util;

// 用于导出数据

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// 导出数据仅能在开发环境中导出
public class DataDumper {
    public static void dumpJson(JsonObject json, Path path) {
        if (!ShapeShifterCurseFabric.IsDevelopmentEnvironment()) {
            return;
        }
        ShapeShifterCurseFabric.LOGGER.info("Dumping data to " + path);
        ShapeShifterCurseFabric.LOGGER.info(json.toString());
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String content = gson.toJson(json);
            Files.writeString(
                    path,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to dump data to " + path, e);
        }
    }

    public static void dumpBinary(byte[] data, Path path) {
        if (!ShapeShifterCurseFabric.IsDevelopmentEnvironment()) {
            return;
        }
        ShapeShifterCurseFabric.LOGGER.info("Dumping data to " + path);
        ShapeShifterCurseFabric.LOGGER.info("Data length: " + data.length);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to dump data to " + path, e);
        }
    }
}
