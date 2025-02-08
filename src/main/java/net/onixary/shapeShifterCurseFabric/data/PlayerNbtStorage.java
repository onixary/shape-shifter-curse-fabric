package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayerNbtStorage {
    private static final Path SAVE_DIR = Paths.get("config", "shape_shifter_curse_fabric");

    public static void saveAttachment(String playerId, PlayerEffectAttachment attachment) {
        try {
            Files.createDirectories(SAVE_DIR);
            Path savePath = SAVE_DIR.resolve(playerId + "_attachment.json");
            NbtCompound nbt = attachment.writeNbt();
            NbtIo.write(nbt, savePath.toFile());
        } catch (IOException e) {
            // 错误处理
            ShapeShifterCurseFabric.LOGGER.error("Failed to save attachment for player: " + playerId, e);
        }
    }

    public static PlayerEffectAttachment loadAttachment(String playerId) {
        try {
            Path savePath = SAVE_DIR.resolve(playerId + "_attachment.json");
            if (!Files.exists(savePath)) return null;
            NbtCompound nbt = NbtIo.read(savePath.toFile());
            if (nbt == null) return null;
            return PlayerEffectAttachment.fromNbt(nbt);
        } catch (IOException e) {
            // 错误处理
            ShapeShifterCurseFabric.LOGGER.error("Failed to load attachment for player: " + playerId, e);
            return null;
        }
    }

    public static void savePlayerFormComponent(String playerId, PlayerFormComponent formComponent) {
        try {
            Files.createDirectories(SAVE_DIR);
            Path savePath = SAVE_DIR.resolve(playerId + "_form.json");
            NbtCompound nbt = new NbtCompound();
            formComponent.writeToNbt(nbt);
            NbtIo.write(nbt, savePath.toFile());
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to save player form for player: " + playerId, e);
        }
    }

    public static PlayerFormComponent loadPlayerFormComponent(String playerId) {
        try {
            Path savePath = SAVE_DIR.resolve(playerId + "_form.json");
            if (!Files.exists(savePath)) return null;
            NbtCompound nbt = NbtIo.read(savePath.toFile());
            if (nbt == null) return null;
            PlayerFormComponent formComponent = new PlayerFormComponent();
            formComponent.readFromNbt(nbt);
            return formComponent;
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to load player form for player: " + playerId, e);
            return null;
        }
    }
}
