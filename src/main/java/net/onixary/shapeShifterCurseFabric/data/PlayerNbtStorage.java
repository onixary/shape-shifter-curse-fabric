package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.PlayerInstinctComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayerNbtStorage {
    private static final Path SAVE_DIR = Paths.get("config", "shape_shifter_curse_fabric");

    private static Path getWorldSaveDir(ServerWorld world) {
        return SAVE_DIR.resolve(world.getServer().getSaveProperties().getLevelName());
    }

    public static void saveAttachment(ServerWorld world, String playerId, PlayerEffectAttachment attachment) {
        try {
            Path worldSaveDir = getWorldSaveDir(world);
            Files.createDirectories(worldSaveDir);
            Path savePath = worldSaveDir.resolve(playerId + "_attachment.dat");
            NbtCompound nbt = attachment.writeNbt();
            NbtIo.write(nbt, savePath.toFile());
        } catch (IOException e) {
            // 错误处理
            ShapeShifterCurseFabric.LOGGER.error("Failed to save attachment for player: " + playerId, e);
        }
    }

    public static PlayerEffectAttachment loadAttachment(ServerWorld world, String playerId) {
        try {
            Path savePath = getWorldSaveDir(world).resolve(playerId + "_attachment.json");
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

    public static void savePlayerFormComponent(ServerWorld world, String playerId, PlayerFormComponent formComponent) {
        try {
            Path saveDir = getWorldSaveDir(world);
            Files.createDirectories(saveDir);
            Path savePath = saveDir.resolve(playerId + "_form.dat");
            NbtCompound nbt = new NbtCompound();
            formComponent.writeToNbt(nbt);
            NbtIo.write(nbt, savePath.toFile());
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to save player form for player: " + playerId, e);
        }
    }

    public static PlayerFormComponent loadPlayerFormComponent(ServerWorld world, String playerId) {
        try {
            Path savePath = getWorldSaveDir(world).resolve(playerId + "_form.dat");
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

    public static PlayerInstinctComponent loadPlayerInstinctComponent(ServerWorld world, String playerId) {
        try {
            Path savePath = getWorldSaveDir(world).resolve(playerId + "_instinct.dat");
            if (!Files.exists(savePath)) return null;
            NbtCompound nbt = NbtIo.read(savePath.toFile());
            if (nbt == null) return null;
            PlayerInstinctComponent instinctComponent = new PlayerInstinctComponent();
            instinctComponent.readFromNbt(nbt);
            return instinctComponent;
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to load player instinct for player: " + playerId, e);
            return null;
        }
    }

    public static void savePlayerInstinctComponent(ServerWorld world, String playerId, PlayerInstinctComponent instinctComponent) {
        try {
            Path saveDir = getWorldSaveDir(world);
            Files.createDirectories(saveDir);
            Path savePath = saveDir.resolve(playerId + "_instinct.dat");
            NbtCompound nbt = new NbtCompound();
            instinctComponent.writeToNbt(nbt);
            NbtIo.write(nbt, savePath.toFile());
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to save player instinct for player: " + playerId, e);
        }
    }
}
