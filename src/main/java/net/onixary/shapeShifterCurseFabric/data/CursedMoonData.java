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

    private static final Path SAVE_DIR = Paths.get("config", "shape_shifter_curse_fabric");

    public CursedMoonData() {
    }

    private static Path getWorldSaveDir(ServerWorld world) {

        Path worldSavePath = world.getServer().getSavePath(WorldSavePath.ROOT);
        String worldName = worldSavePath.getName(worldSavePath.getNameCount() - 2).toString();
        ShapeShifterCurseFabric.LOGGER.info("World save name: " + worldName);
        return SAVE_DIR.resolve(worldName);
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
            Path worldSaveDir = getWorldSaveDir(world);
            NbtCompound nbt = serialize();
            NbtIo.write(nbt, worldSaveDir.resolve("cursed_moon_data.dat").toFile());
        } catch (Exception e) {
            // 错误处理
            ShapeShifterCurseFabric.LOGGER.error("Failed to save Cursed Moon Data", e);
        }
    }

    public void load(ServerWorld world){
        try {
            Path savePath = getWorldSaveDir(world).resolve("cursed_moon_data.dat");
            if (!savePath.toFile().exists()){
                ShapeShifterCurseFabric.LOGGER.warn("Cursed Moon Data file location not found, creating new data");
                instance.save(world);
            }

            NbtCompound nbt = NbtIo.read(savePath.toFile());
            if (nbt == null) {
                ShapeShifterCurseFabric.LOGGER.warn("Failed to read Cursed Moon Data Nbt, creating new data");
                instance.save(world);
            }

            instance.deserialize(nbt);
        } catch (Exception e) {
            // 错误处理
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
