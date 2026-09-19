package net.onixary.shapeShifterCurseFabric.util.util;

// 用于导出数据

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.Hash;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimRegistry;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ApiStatus.Experimental
    // 不保证能用 也不进行维护 Port无需Port这个函数 纯工具函数 什么时候维护取决于我什么时候要用
    public static void dumpAnimData() {
        JsonObject json = new JsonObject();
        for (IForm form : RegPlayerForms.playerForms.values()) {
            JsonObject formJson = new JsonObject();
            HashMap<Identifier, HashMap<Identifier, Float>> speedMap = new HashMap<>();
            for (Map.Entry<RegistryKey<AnimRegistry.AnimState>, AnimRegistry.AnimState> stateEntry : AnimRegistry.animStateRegistry.getEntrySet()) {
                Identifier id = stateEntry.getKey().getValue();
                AbstractAnimStateController controller = form.getAnimStateController(ClientUtils.getPlayer(), new AnimSystem.AnimSystemData(ClientUtils.getPlayer()), id);
                if (controller != null) {
                    if (!controller.isRegistered(ClientUtils.getPlayer(), new AnimSystem.AnimSystemData(ClientUtils.getPlayer()))) {
                        controller.registerAnim(ClientUtils.getPlayer(), new AnimSystem.AnimSystemData(ClientUtils.getPlayer()));
                    }
                    List<AnimationHolder> animations = controller.getAllAnimations();
                    for (AnimationHolder animation : animations) {
                        HashMap<Identifier, Float> speeds = speedMap.computeIfAbsent(animation.animationID, k -> new HashMap<>());
                        float speed = animation.getSpeed();
                        speeds.put(id, speed);
                    }
                }
            }
            for (Map.Entry<Identifier, HashMap<Identifier, Float>> entry : speedMap.entrySet()) {
                JsonObject speeds = new JsonObject();
                for (Map.Entry<Identifier, Float> speedEntry : entry.getValue().entrySet()) {
                    speeds.addProperty(speedEntry.getKey().toString(), speedEntry.getValue());
                }
                formJson.add(entry.getKey().toString(), speeds);
            }
            json.add(form.getFormID().toString(), formJson);
        }
        dumpJson(json, FabricLoader.getInstance().getConfigDir().resolve("ssc_dump/anim.json"));
    }

    public static <T> List<T> buildList(T... items) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }
}
