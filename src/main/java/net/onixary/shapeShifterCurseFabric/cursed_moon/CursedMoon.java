package net.onixary.shapeShifterCurseFabric.cursed_moon;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.CursedMoonData;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import org.spongepowered.asm.mixin.Unique;



// Logic from Magic Moon mod
// https://github.com/ChongYuCN/Magic-Moon
public class CursedMoon {
    public static long day_time = 0;
    public static int day = 0;

    // Cursed Moon rises every 3 days
    public static boolean isCursedMoon() {

        CursedMoonData currentData = ShapeShifterCurseFabric.cursedMoonData.getInstance();
        if(currentData.isActive){
            return day == currentData.startDay
                    || ((day - currentData.startDay) % currentData.perDay == 0 && day >= currentData.startDay);
        }
        else{
            return false;
        }
    }

    public static boolean isNight(){
        long timeDayMoon = CursedMoon.day_time - (CursedMoon.day -1)*24000L;
        //6:0,7:1,8:2,9:3,10:4,11:5,12:6,13:7,14:8,
        // 15:9000L, 16:10000L, 17:11000L, 18:12000L, 19:13000L, 20:14000L, 21:15000L
        //22:16,  23:17, 24:18, 1:19, 2:20, 3:21, 4:22, 5:23, 6: 0
        return timeDayMoon > 12000L && timeDayMoon < 23000L;
    }

    @Unique
    public static boolean checkIfOutdoor(ServerWorld world, BlockPos.Mutable pos) {
        return pos.getY() > 63 && pos.getY() >= world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1;
    }

    public static void jumpToNextCursedMoon(ServerWorld world){
        // 让下一晚变成Cursed Moon
        int newStartDay = (int)(day / StaticParams.CURSED_MOON_INTERVAL_DAY) * StaticParams.CURSED_MOON_INTERVAL_DAY +
                (day % StaticParams.CURSED_MOON_INTERVAL_DAY);
        ShapeShifterCurseFabric.cursedMoonData.getInstance().startDay = newStartDay;
        ShapeShifterCurseFabric.cursedMoonData.getInstance().save(world);
    }


}
