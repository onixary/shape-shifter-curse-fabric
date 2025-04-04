package net.onixary.shapeShifterCurseFabric.cursed_moon;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.CursedMoonData;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import org.spongepowered.asm.mixin.Unique;

import static net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager.clearFormFlag;


// Logic from Magic Moon mod
// https://github.com/ChongYuCN/Magic-Moon
public class CursedMoon {
    public static long day_time = 0;
    public static int day = 0;
    public static boolean moon_effect_applied = false;
    public static boolean end_moon_effect_applied = false;

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

    public static void applyMoonEffect(ServerPlayerEntity player){
        // 处于Cursed Moon时的逻辑
        // 文本提示
        if(!CursedMoon.moon_effect_applied){
            boolean isOverworld = player.getWorld().getRegistryKey() == World.OVERWORLD;
            if(FormAbilityManager.getForm(player) == PlayerForms.ORIGINAL_BEFORE_ENABLE){
                if(isOverworld){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.on_cursed_moon_before_enable").formatted(Formatting.LIGHT_PURPLE));
                }
            }
            else{
                if(isOverworld){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.on_cursed_moon").formatted(Formatting.LIGHT_PURPLE));
                }
                else{
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.on_cursed_moon_nether").formatted(Formatting.LIGHT_PURPLE));
                }

                // 触发自定义成就
                ShapeShifterCurseFabric.ON_TRIGGER_CURSED_MOON.trigger(player);
            }
            // 将byCure标记为false 纯纯的史山
            RegPlayerFormComponent.PLAYER_FORM.get(player).setByCure(false);
            RegPlayerFormComponent.PLAYER_FORM.sync(player);
            ShapeShifterCurseFabric.LOGGER.info("Cursed Moon rises!");
            // transform
            // if form already triggered by cursed moon or triggered by cure, do not trigger again
            if(!RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon() && !RegPlayerFormComponent.PLAYER_FORM.get(player).isByCure()){
                TransformManager.handleProgressiveTransform(player,true);
            }
            CursedMoon.moon_effect_applied =true;
        }
    }

    public static void applyEndMoonEffect(ServerPlayerEntity player){
        // 结束Cursed Moon时的逻辑
        if(!CursedMoon.end_moon_effect_applied){
            if(FormAbilityManager.getForm(player) == PlayerForms.ORIGINAL_BEFORE_ENABLE){
                player.sendMessage(Text.translatable("info.shape-shifter-curse.end_cursed_moon_before_enable").formatted(Formatting.LIGHT_PURPLE));
            }
            else{
                // 判断形态flag
                PlayerFormComponent currentFormComponent = RegPlayerFormComponent.PLAYER_FORM.get(player);
                if(currentFormComponent.isByCure()){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.end_cursed_moon_by_cure").formatted(Formatting.LIGHT_PURPLE));
                    // 触发自定义成就
                    ShapeShifterCurseFabric.ON_END_CURSED_MOON_CURED.trigger(player);
                    if(currentFormComponent.getCurrentForm().getIndex() == -1){
                        // 如果在cure的基础上形态index为-1，则一定为2形态进入cursed moon
                        // 触发自定义成就
                        ShapeShifterCurseFabric.ON_END_CURSED_MOON_CURED_FORM_2.trigger(player);
                    }
                }
                else if(currentFormComponent.isByCursedMoon()){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.end_cursed_moon").formatted(Formatting.LIGHT_PURPLE));
                    // 触发自定义成就
                    ShapeShifterCurseFabric.ON_END_CURSED_MOON.trigger(player);
                }
            }
            ShapeShifterCurseFabric.LOGGER.info("Cursed Moon ends!");
            // transform
            if(RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon() && !RegPlayerFormComponent.PLAYER_FORM.get(player).isByCure()){
                TransformManager.handleMoonEndTransform(player);
            }
            CursedMoon.end_moon_effect_applied =true;
        }
    }

    public static void resetMoonEffect(){
        // when player exit game, reset moon effect so that it can be triggered again
        CursedMoon.moon_effect_applied = false;
        CursedMoon.end_moon_effect_applied = false;
    }
}
