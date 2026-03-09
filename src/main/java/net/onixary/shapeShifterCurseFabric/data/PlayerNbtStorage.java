package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
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
    private static boolean migrationCompleted = false;

    private static Path getNewWorldSaveDir(ServerWorld world) {
        return world.getServer().getSavePath(WorldSavePath.ROOT).resolve("data").resolve(MOD_DATA_DIR);
    }

    private static Path getOldWorldSaveDir(ServerWorld world) {
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
                Files.copy(oldFile, newFile, StandardCopyOption.REPLACE_EXISTING);
                ShapeShifterCurseFabric.LOGGER.info("Migrated player data '{}' from old location to new location.", fileName);
                ShapeShifterCurseFabric.LOGGER.info("Migration completed: newFile exists = {}, size = {}", Files.exists(newFile), Files.size(newFile));
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

    /**
     * 迁移方法：从文件存储读取数据并迁移到 CCA 组件
     * 此方法会在玩家首次加入时调用，自动将旧数据迁移到 CCA 持久化存储
     *
     * @param world    服务器世界
     * @param playerId 玩家 UUID
     * @return 从文件加载的 PlayerFormComponent，如果文件不存在则返回 null
     */
    public static PlayerFormComponent migrateAndGetFormComponent(ServerWorld world, String playerId) {
        if (migrationCompleted) {
            return null;
        }

        String formFileName = playerId + "_form.dat";
        String instinctFileName = playerId + "_instinct.dat";

        try {
            migrateData(world, formFileName);
            migrateData(world, instinctFileName);

            Path newDir = getNewWorldSaveDir(world);
            Path formFile = newDir.resolve(formFileName);
            Path instinctFile = newDir.resolve(instinctFileName);

            if (Files.exists(formFile)) {
                NbtCompound nbt = NbtIo.read(formFile.toFile());
                if (nbt != null && !nbt.isEmpty()) {
                    ShapeShifterCurseFabric.LOGGER.info("Migrating player form data from file: {}", playerId);
                    PlayerFormComponent component = new PlayerFormComponent();
                    component.readFromNbt(nbt);
                    return component;
                }
            }

            if (Files.exists(instinctFile)) {
                NbtCompound nbt = NbtIo.read(instinctFile.toFile());
                if (nbt != null && !nbt.isEmpty()) {
                    ShapeShifterCurseFabric.LOGGER.info("Migrating player instinct data from file: {}", playerId);
                }
            }
        } catch (IOException e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to migrate player data for: " + playerId, e);
        }

        return null;
    }

    /**
     * 标记迁移完成，调用后不再尝试从文件读取数据
     * 建议在世界加载完成后调用
     */
    public static void completeMigration() {
        migrationCompleted = true;
        ShapeShifterCurseFabric.LOGGER.info("Player data migration completed, future loads will use CCA only");
    }

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
        RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.sync(player);
    }
}
