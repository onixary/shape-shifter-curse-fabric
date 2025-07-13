package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.onixary.shapeShifterCurseFabric.data.StaticParams.CURSED_MOON_BASE_PROBABILITY;
import static net.onixary.shapeShifterCurseFabric.data.StaticParams.CURSED_MOON_PROBABILITY_INCREASE;

public class CursedMoonData {
    private static final CursedMoonData instance = new CursedMoonData();

    // 新的概率系统变量
    public float basePerSecondProbability = CURSED_MOON_BASE_PROBABILITY;  // 每秒基准概率
    public float currentPerSecondProbability = CURSED_MOON_BASE_PROBABILITY;  // 每秒当前概率
    public float probabilityIncrease = CURSED_MOON_PROBABILITY_INCREASE;  // 概率倍数（失败后递增）
    public boolean isTonightCursedMoon = false;  // 今晚是否为诅咒之月
    public int lastCheckedDay = 0;  // 上次检查的天数
    public float lastTriggerTime;

    // 保留原有变量用于兼容性
    public int startDay = 0;
    public int perDay = 3;
    public boolean isActive = false;

    private static final Path LEGACY_SAVE_DIR = Paths.get("config", "shape_shifter_curse_fabric");

    public CursedMoonData() {
    }

    private static Path getSaveDir(ServerWorld world) {
        return world.getServer().getSavePath(WorldSavePath.ROOT).resolve(ShapeShifterCurseFabric.MOD_ID);
    }

    private static Path getLegacyWorldSaveDir(ServerWorld world) {
        Path worldSavePath = world.getServer().getSavePath(WorldSavePath.ROOT);
        String worldName = worldSavePath.getFileName().toString();
        return LEGACY_SAVE_DIR.resolve(worldName);
    }

    private NbtCompound serialize(){
        NbtCompound moonData = new NbtCompound();

        // 新的概率系统数据
        moonData.putFloat("basePerSecondProbability", basePerSecondProbability);
        moonData.putFloat("currentPerSecondProbability", currentPerSecondProbability);
        moonData.putFloat("probabilityIncrease", probabilityIncrease);
        moonData.putBoolean("isTonightCursedMoon", isTonightCursedMoon);
        moonData.putInt("lastCheckedDay", lastCheckedDay);
        moonData.putFloat("lastTriggerTime", lastTriggerTime);

        // 保留原有数据
        moonData.putInt("startDay", startDay);
        moonData.putInt("perDay", perDay);
        moonData.putBoolean("isActive", isActive);

        return moonData;
    }

    private void deserialize(NbtCompound nbt){
        if(nbt == null) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to get Cursed Moon Data");
            return;
        }
        try{
            // 新的概率系统数据
            basePerSecondProbability = nbt.getFloat("basePerSecondProbability");
            currentPerSecondProbability = nbt.getFloat("currentPerSecondProbability");
            probabilityIncrease = nbt.getFloat("probabilityIncrease");
            isTonightCursedMoon = nbt.getBoolean("isTonightCursedMoon");
            lastCheckedDay = nbt.getInt("lastCheckedDay");
            lastTriggerTime = nbt.getFloat("lastTriggerTime");

            // 保留原有数据
            startDay = nbt.getInt("startDay");
            perDay = nbt.getInt("perDay");
            isActive = nbt.getBoolean("isActive");
        }
        catch (Exception e){
            ShapeShifterCurseFabric.LOGGER.error("Failed to read Cursed Moon Data", e);
        }
    }

    public void save(ServerWorld world){
        try {
            Path worldSaveDir = getSaveDir(world);
            if (!worldSaveDir.toFile().exists()) {
                worldSaveDir.toFile().mkdirs();
            }
            NbtCompound nbt = serialize();
            NbtIo.write(nbt, worldSaveDir.resolve("cursed_moon_data.dat").toFile());
            ShapeShifterCurseFabric.LOGGER.info("Cursed Moon Data saved successfully");
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to save Cursed Moon Data", e);
        }
    }

    public void load(ServerWorld world){
        try {
            Path saveDir = getSaveDir(world);
            Path legacySaveDir = getLegacyWorldSaveDir(world);
            Path dataFile = saveDir.resolve("cursed_moon_data.dat");
            Path legacyDataFile = legacySaveDir.resolve("cursed_moon_data.dat");

            NbtCompound nbt = null;
            if (dataFile.toFile().exists()) {
                nbt = NbtIo.read(dataFile.toFile());
            } else if (legacyDataFile.toFile().exists()) {
                ShapeShifterCurseFabric.LOGGER.info("Migrating Cursed Moon Data for world: " + world.getServer().getSavePath(WorldSavePath.ROOT).getFileName().toString());
                nbt = NbtIo.read(legacyDataFile.toFile());
                if (nbt != null) {
                    deserialize(nbt);
                    save(world); // Save to new location
                    legacyDataFile.toFile().delete(); // Delete old file
                }
            }

            if (nbt != null) {
                deserialize(nbt);
            }
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to load Cursed Moon Data", e);
        }
    }

    public CursedMoonData getInstance(){
        return instance;
    }

    public void enableCursedMoon(ServerWorld world){
        // 当玩家启用Mod内容时，启用Cursed Moon并初始化概率系统
        isActive = true;
    }

    public void disableCursedMoon(ServerWorld world){
        // 当玩家未启用Mod内容时，禁用Cursed Moon
        isActive = false;
        ShapeShifterCurseFabric.LOGGER.info("Cursed Moon disabled");
        // 重置概率系统
        basePerSecondProbability = 0.0f;
        currentPerSecondProbability = 0.0f;
        probabilityIncrease = 0.0f;

        save(world);
    }

    // 重置概率系统（当触发成功时调用）
    public void resetProbability(ServerWorld world) {
        basePerSecondProbability = CURSED_MOON_BASE_PROBABILITY;
        currentPerSecondProbability = basePerSecondProbability;
        lastTriggerTime = world.getTimeOfDay();
        probabilityIncrease = CURSED_MOON_PROBABILITY_INCREASE;
        save(world);
        ShapeShifterCurseFabric.LOGGER.info("CursedMoon probability system reset");
    }

    public void saveTonightStatus(ServerWorld world) {
        // 确保立即保存当前状态
        this.save(world);
        ShapeShifterCurseFabric.LOGGER.info("保存诅咒之月状态: " + this.isTonightCursedMoon);
    }

    // 添加保存所有玩家的月亮效果状态
    public void savePlayerStates(ServerWorld world, ServerPlayerEntity player) {
        PlayerFormComponent formComp = RegPlayerFormComponent.PLAYER_FORM.get(player);
        NbtCompound playerData = new NbtCompound();
        playerData.putBoolean("moonEffectApplied", formComp.isMoonEffectApplied());
        playerData.putBoolean("endMoonEffectApplied", formComp.isEndMoonEffectApplied());

        // 保存到世界特定文件
        try {
            Path playerSaveDir = getPlayerSaveDir(world, player);
            if (!playerSaveDir.toFile().exists()) {
                playerSaveDir.toFile().mkdirs();
            }
            NbtIo.write(playerData, playerSaveDir.resolve(player.getUuid().toString() + ".dat").toFile());
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("保存玩家月亮状态失败", e);
        }
    }

    // 从世界特定文件加载玩家状态
    public void loadPlayerStates(ServerWorld world, ServerPlayerEntity player) {
        try {
            Path playerSaveDir = getPlayerSaveDir(world, player);
            NbtCompound playerData = NbtIo.read(playerSaveDir.resolve(player.getUuid().toString() + ".dat").toFile());
            if (playerData != null) {
                PlayerFormComponent formComp = RegPlayerFormComponent.PLAYER_FORM.get(player);
                formComp.setMoonEffectApplied(playerData.getBoolean("moonEffectApplied"));
                formComp.setEndMoonEffectApplied(playerData.getBoolean("endMoonEffectApplied"));
            }
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("加载玩家月亮状态失败", e);
        }
    }

    private Path getPlayerSaveDir(ServerWorld world, ServerPlayerEntity player) {
        return world.getServer().getSavePath(WorldSavePath.ROOT)
                .resolve(ShapeShifterCurseFabric.MOD_ID)
                .resolve("moon_states");
    }
}
