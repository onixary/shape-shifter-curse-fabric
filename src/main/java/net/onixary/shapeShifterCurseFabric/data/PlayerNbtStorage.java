package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.PlayerInstinctComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.RegPlayerInstinctComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PlayerNbtStorage {
    private static final String MOD_DATA_DIR = ShapeShifterCurseFabric.MOD_ID;
    private static final Path OLD_SAVE_DIR_ROOT = Paths.get("config", "shape_shifter_curse_fabric");

    private static Path getNewWorldSaveDir(ServerWorld world) {
        // 正确的路径应为：saves/<world_name>/data/<mod_id>
        return world.getServer().getSavePath(WorldSavePath.ROOT).resolve("data").resolve(MOD_DATA_DIR);
    }

    private static Path getOldWorldSaveDir(ServerWorld world) {
        // 旧路径为：config/shape_shifter_curse_fabric/<world_name>
        String worldName = world.getServer().getSavePath(WorldSavePath.ROOT).getFileName().toString();
        return OLD_SAVE_DIR_ROOT.resolve(worldName);
    }

    private static void migrateData(ServerWorld world, String fileName) {
        try {
            Path oldDir = getOldWorldSaveDir(world);
            Path newDir = getNewWorldSaveDir(world);
            Path oldFile = oldDir.resolve(fileName);
            Path newFile = newDir.resolve(fileName);

            ShapeShifterCurseFabric.LOGGER.info("Checking for migration: oldFile={}, newFile={}", oldFile, newFile);

            if (Files.exists(oldFile) && !Files.exists(newFile)) {
                Files.createDirectories(newDir);
                // 使用 copy 而不是 move，确保操作的可靠性
                Files.copy(oldFile, newFile, StandardCopyOption.REPLACE_EXISTING);
                ShapeShifterCurseFabric.LOGGER.info("Migrated player data '{}' from old location to new location.", fileName);
                ShapeShifterCurseFabric.LOGGER.info("Migration completed: newFile exists = {}, size = {}", Files.exists(newFile), Files.size(newFile));
                // 迁移成功后删除旧文件
                Files.delete(oldFile);
            } else if (Files.exists(newFile)) {
                ShapeShifterCurseFabric.LOGGER.info("New file already exists, skipping migration: {}", newFile);
            } else {
                ShapeShifterCurseFabric.LOGGER.info("No old file found for migration: {}", oldFile);
            }
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to migrate player data file: " + fileName, e);
        }
    }

    /* 重构后不需要了 仅用于参考旧实现逻辑
    public static void saveAttachment(ServerWorld world, String playerId, PlayerEffectAttachment attachment) {
        try {
            Path worldSaveDir = getNewWorldSaveDir(world);
            Files.createDirectories(worldSaveDir);
            Path savePath = worldSaveDir.resolve(playerId + "_attachment.dat");
            NbtCompound nbt = attachment.toNbt();
            NbtIo.write(nbt, savePath.toFile());
        } catch (IOException e) {
            // 错误处理
            ShapeShifterCurseFabric.LOGGER.error("Failed to save attachment for player: " + playerId, e);
        }
    }

    public static PlayerEffectAttachment loadAttachment(ServerWorld world, String playerId) {
        String fileName = playerId + "_attachment.dat";
        migrateData(world, fileName);
        try {
            Path savePath = getNewWorldSaveDir(world).resolve(fileName);
            if (!Files.exists(savePath)) return null;
            NbtCompound nbt = NbtIo.read(savePath.toFile());
            if (nbt == null) return null;
            return PlayerEffectAttachment.fromNbt(nbt);
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to load attachment for player: " + playerId, e);
            return null;
        }
    }
     */

    public static void savePlayerFormComponent(ServerWorld world, String playerId, PlayerFormComponent component) {
        try {
            Path worldSaveDir = getNewWorldSaveDir(world);
            Files.createDirectories(worldSaveDir);
            Path savePath = worldSaveDir.resolve(playerId + "_form.dat");
            NbtCompound nbt = new NbtCompound();
            component.writeToNbt(nbt);
            NbtIo.write(nbt, savePath.toFile());
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to save PlayerFormComponent for player: " + playerId, e);
        }
    }

    public static PlayerFormComponent loadPlayerFormComponent(ServerWorld world, String playerId) {
        String fileName = playerId + "_form.dat";
        migrateData(world, fileName);
        try {
            Path savePath = getNewWorldSaveDir(world).resolve(fileName);
            ShapeShifterCurseFabric.LOGGER.info("Loading PlayerFormComponent: file exists = {}, path = {}", Files.exists(savePath), savePath);
            if (Files.exists(savePath)) {
                NbtCompound nbt = NbtIo.read(savePath.toFile());
                ShapeShifterCurseFabric.LOGGER.info("NBT loaded: nbt != null = {}, nbt.isEmpty() = {}", nbt != null, nbt != null ? nbt.isEmpty() : "null");
                if (nbt != null && !nbt.isEmpty()) {
                    PlayerFormComponent component = new PlayerFormComponent();
                    component.readFromNbt(nbt);
                    ShapeShifterCurseFabric.LOGGER.info("PlayerFormComponent loaded successfully");
                    return component;
                }
            }
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to load PlayerFormComponent for player: " + playerId, e);
        }
        ShapeShifterCurseFabric.LOGGER.info("PlayerFormComponent load failed, returning null");
        return null;
    }

    public static void savePlayerInstinctComponent(ServerWorld world, String playerId, PlayerInstinctComponent component) {
        try {
            Path worldSaveDir = getNewWorldSaveDir(world);
            Files.createDirectories(worldSaveDir);
            Path savePath = worldSaveDir.resolve(playerId + "_instinct.dat");
            NbtCompound nbt = new NbtCompound();
            component.writeToNbt(nbt);
            NbtIo.write(nbt, savePath.toFile());
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to save PlayerInstinctComponent for player: " + playerId, e);
        }
    }

    public static PlayerInstinctComponent loadPlayerInstinctComponent(ServerWorld world, String playerId) {
        String fileName = playerId + "_instinct.dat";
        migrateData(world, fileName);
        try {
            Path savePath = getNewWorldSaveDir(world).resolve(fileName);
            ShapeShifterCurseFabric.LOGGER.info("Loading PlayerInstinctComponent: file exists = {}, path = {}", Files.exists(savePath), savePath);
            if (Files.exists(savePath)) {
                NbtCompound nbt = NbtIo.read(savePath.toFile());
                ShapeShifterCurseFabric.LOGGER.info("NBT loaded: nbt != null = {}, nbt.isEmpty() = {}", nbt != null, nbt != null ? nbt.isEmpty() : "null");
                if (nbt != null && !nbt.isEmpty()) {
                    PlayerInstinctComponent component = new PlayerInstinctComponent();
                    component.readFromNbt(nbt);
                    ShapeShifterCurseFabric.LOGGER.info("PlayerInstinctComponent loaded successfully");
                    return component;
                }
            }
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to load PlayerInstinctComponent for player: " + playerId, e);
        }
        ShapeShifterCurseFabric.LOGGER.info("PlayerInstinctComponent load failed, returning null");
        return null;
    }

    public static void saveAll(ServerWorld world, ServerPlayerEntity player) {
        FormAbilityManager.saveForm(player);
        PlayerNbtStorage.savePlayerFormComponent(world, player.getUuid().toString(),
                RegPlayerFormComponent.PLAYER_FORM.get(player));
        /* 重构后不需要了 仅用于参考旧实现逻辑
        PlayerEffectAttachment attachment = player.getAttached(EffectManager.EFFECT_ATTACHMENT);
        if (attachment != null) {
            PlayerNbtStorage.saveAttachment(world, player.getUuid().toString(),attachment);
        }
         */
        PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
        savePlayerInstinctComponent(world, player.getUuid().toString(), comp);
    }
}
