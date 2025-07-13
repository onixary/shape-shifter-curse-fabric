package net.onixary.shapeShifterCurseFabric.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CursedMoonData {
    private static final CursedMoonData instance = new CursedMoonData();
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

    public void disableCursedMoon(ServerWorld world){
        // 当玩家未启用Mod内容时，禁用Cursed Moon
        startDay = StaticParams.CURSED_MOON_INTERVAL_DAY;
        perDay = StaticParams.CURSED_MOON_INTERVAL_DAY;
        isActive = false;
        save(world);
    }

    public void enableCursedMoon(ServerWorld world){
        // 当玩家启用Mod内容时，启用Cursed Moon
        isActive = true;
        save(world);
    }




}
