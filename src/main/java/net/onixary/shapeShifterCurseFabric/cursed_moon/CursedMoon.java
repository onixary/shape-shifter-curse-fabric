package net.onixary.shapeShifterCurseFabric.cursed_moon;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.CursedMoonData;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;

import java.util.Arrays;
import java.util.Optional;

import static net.onixary.shapeShifterCurseFabric.data.StaticParams.CURSED_MOON_PROBABILITY_MAX;


// Logic from Magic Moon mod
// https://github.com/ChongYuCN/Magic-Moon
public class CursedMoon {
    public static long day_time = 0;
    public static int day = 0;
    public static boolean midday_message_sent = false;

    // 客户端同步的状态变量
    public static boolean clientIsCursedMoon = false;
    public static boolean clientIsNight = false;

    public static long getDayTime(World world) {
        return world.getTimeOfDay();
    }

    public static int getDay(World world) {
        return (int)(getDayTime(world) / 24000L) + 1;
    }

    public static boolean isNight(World world) {
        long timeDayMoon = getDayTime(world) - (getDay(world) - 1) * 24000L;
        return timeDayMoon > 12000L && timeDayMoon < 23000L;
    }

    public static boolean isDaytime(World world) {
        long timeDayMoon = getDayTime(world) - (getDay(world) - 1) * 24000L;
        return timeDayMoon >= 0L && timeDayMoon <= 12000L;
    }

    // 新的概率系统判断逻辑
    /*public static boolean isCursedMoon() {
        // 如果在客户端，使用同步的状态
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return clientIsCursedMoon;
        }

        // 服务端逻辑：基于概率系统判断
        CursedMoonData currentData = ShapeShifterCurseFabric.cursedMoonData.getInstance();
        if (currentData.isActive) {
            // 使用新的概率系统：检查今晚是否被标记为诅咒之月
            return currentData.isTonightCursedMoon;
        } else {
            return false;
        }
    }*/

    public static boolean isCursedMoon(World world) {
        // 如果在客户端，使用同步的状态
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return clientIsCursedMoon;
        }

        // 服务端逻辑：基于月相系统判断
        CursedMoonData currentData = ShapeShifterCurseFabric.cursedMoonData.getInstance();
        if (currentData.isActive) {
            return isCursedMoonByPhase(world);
        } else {
            return false;
        }
    }

    public static boolean isCursedMoonByPhase(int moonPhase) {
        int[] curseMoonPhase = ShapeShifterCurseFabric.commonConfig.curseMoonPhase;
        return Arrays.stream(curseMoonPhase).anyMatch(phase -> phase == moonPhase);
    }

    public static boolean isCursedMoonByPhase(World world) {
        int moonPhase = world.getMoonPhase();
        // 月相为1、4、7时触发诅咒之月
        // Cursed Moon is triggered when moon phase is 1, 4, or 7
        // return moonPhase == 1 || moonPhase == 4 || moonPhase == 7;
        return isCursedMoonByPhase(moonPhase);
    }

    public static boolean isNight(){
        // 如果在客户端，使用同步的状态
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return clientIsNight;
        }

        // 服务端逻辑保持不变
        long timeDayMoon = CursedMoon.day_time - (CursedMoon.day -1)*24000L;
        return timeDayMoon > 12000L && timeDayMoon < 23000L;
    }

    public static boolean isDaytime() {
        long timeDayMoon = CursedMoon.day_time - (CursedMoon.day - 1) * 24000L;
        // 白天时间：6:00-18:00 (0-12000刻)
        return timeDayMoon >= 0L && timeDayMoon <= 12000L;
    }

    // 新的概率检查方法 - 每秒在白天调用
    public static void checkCursedMoonProbability(ServerWorld world) {
        CursedMoonData data = ShapeShifterCurseFabric.cursedMoonData.getInstance();

        // 只有在系统激活且为白天时才进行检查
        if (!data.isActive || !isDaytime()) {
            return;
        }

        // 如果今晚已经被标记为诅咒之月，则不再进行判定
        if (data.isTonightCursedMoon) {
            return;
        }

        // 调试日志（可选，避免过多日志）
        if (world.getTime() % 200 == 0) { // 每10秒输出一次调试信息
            ShapeShifterCurseFabric.LOGGER.info("CursedMoon probability check: currentPerSecondProbability=" +
                                              data.currentPerSecondProbability + ", multiplier=" + data.probabilityIncrease);
        }
        // 增强概率控制，保证诅咒之月事件间隔时间不小于2天
        if((data.currentPerSecondProbability - data.basePerSecondProbability) / data.probabilityIncrease < 1200){
            // 增加概率
            data.currentPerSecondProbability += data.probabilityIncrease;
            // 限制最大概率
            if (data.currentPerSecondProbability > CURSED_MOON_PROBABILITY_MAX) {
                data.currentPerSecondProbability = CURSED_MOON_PROBABILITY_MAX;
            }

            //ShapeShifterCurseFabric.LOGGER.info("CursedMoon probability too low to trigger. " +
            //                                  "Current second: " + (data.currentPerSecondProbability - data.basePerSecondProbability) / data.probabilityIncrease);
            return;
        }

        // 每秒进行概率判定
        float random = world.getRandom().nextFloat();
        if (random < data.currentPerSecondProbability) {
            // 判定成功，今晚将是诅咒之月
            data.isTonightCursedMoon = true;
            data.resetProbability(world);
            data.save(world);
            ShapeShifterCurseFabric.LOGGER.info("CursedMoon triggered for tonight! Day: " + day +
                                              ", random: " + random + ", probability: " + data.currentPerSecondProbability);

            // 向所有玩家发送消息
            for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                if (!FormAbilityManager.getForm(player).equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)) {
                    if(player.getWorld().getRegistryKey() == World.OVERWORLD){
                        player.sendMessage(Text.translatable("info.shape-shifter-curse.before_cursed_moon").formatted(Formatting.DARK_PURPLE));
                    }
                    else{
                        player.sendMessage(Text.translatable("info.shape-shifter-curse.before_cursed_moon_nether").formatted(Formatting.DARK_PURPLE));
                    }
                }
            }

            // 向所有在线玩家同步状态
            //boolean currentIsNight = isNight();
            for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                ModPacketsS2CServer.sendCursedMoonData(player, day_time, day, true, false);
            }
        }
        else{
            // 判定失败，增加概率
            data.currentPerSecondProbability += data.probabilityIncrease;

            // 限制最大概率
            if (data.currentPerSecondProbability > CURSED_MOON_PROBABILITY_MAX) {
                data.currentPerSecondProbability = CURSED_MOON_PROBABILITY_MAX;
            }

            data.save(world);

            // 调试日志
            //ShapeShifterCurseFabric.LOGGER.info("CursedMoon probability check failed. Random: " + random +
            //                                  ", currentPerSecondProbability: " + data.currentPerSecondProbability);
        }
    }

    // 废弃原有的 jumpToNextCursedMoon 方法，替换为手动触发方法
    /*public static void forceTriggerCursedMoon(ServerWorld world) {
        CursedMoonData data = ShapeShifterCurseFabric.cursedMoonData.getInstance();

        if (!data.isActive) {
            ShapeShifterCurseFabric.LOGGER.warn("Cannot trigger CursedMoon: system is not active");
            return;
        }

        // 强制触发今晚的诅咒之月
        data.isTonightCursedMoon = true;
        data.resetProbability(world);
        data.save(world);

        ShapeShifterCurseFabric.LOGGER.info("CursedMoon manually triggered for tonight! Day: " + day);

        // 向所有玩家发送消息
        for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            if (FormAbilityManager.getForm(player) != PlayerForms.ORIGINAL_BEFORE_ENABLE) {
                player.sendMessage(Text.translatable("info.shape-shifter-curse.cursed_moon_forced").formatted(Formatting.DARK_PURPLE));
            }
        }

        // 立即向所有在线玩家同步状态
        boolean currentIsNight = isNight();
        for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            ModPacketsS2C.sendCursedMoonData(player, day_time, day, true, currentIsNight);
        }
    }*/

    public static Optional<Integer> getNextCurseMoonPhase(int NowPhase) {
        int MoonPhaseCount = 8;
        for (int DaySkip = 0; DaySkip < MoonPhaseCount; DaySkip++) {
            int CurrentPhase = (NowPhase + DaySkip) % MoonPhaseCount;
            if (isCursedMoonByPhase(CurrentPhase)) {
                return Optional.of(CurrentPhase);
            }
        }
        return Optional.empty();
    }

    public static void forceTriggerCursedMoon(ServerWorld world) {
        CursedMoonData data = ShapeShifterCurseFabric.cursedMoonData.getInstance();

        if (!data.isActive) {
            ShapeShifterCurseFabric.LOGGER.warn("Cannot trigger CursedMoon: system is not active");
            return;
        }

//        // 获取当前月相
        int currentPhase = world.getMoonPhase();
//        int targetPhase = -1;
//
//        // 找到下一个诅咒月相
//        if (currentPhase < 1) {
//            targetPhase = 1;
//        } else if (currentPhase < 4) {
//            targetPhase = 4;
//        } else if (currentPhase < 7) {
//            targetPhase = 7;
//        } else {
//            targetPhase = 1; // 回到下一个周期
//        }
        Optional<Integer> nextCursedPhase = getNextCurseMoonPhase(currentPhase);
        if (nextCursedPhase.isEmpty()) {
            ShapeShifterCurseFabric.LOGGER.warn("Cannot trigger CursedMoon: no next cursed phase found");
            return;
        }
        int targetPhase = nextCursedPhase.get();

        // 计算需要跳过的天数来达到目标月相
        int daysToSkip = (targetPhase - currentPhase + 8) % 8;
        if (daysToSkip == 0) daysToSkip = 8; // 如果已经是诅咒月相，跳到下一个

        // 调整世界时间到目标月相
        long currentTime = world.getTimeOfDay();
        long newTime = currentTime + (daysToSkip * 24000L);
        world.setTimeOfDay(newTime);

        data.save(world);

        ShapeShifterCurseFabric.LOGGER.info("CursedMoon manually triggered! Skipped " + daysToSkip + " days to reach moon phase " + targetPhase);

        // 向所有玩家发送消息
        for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            if (!FormAbilityManager.getForm(player).equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)) {
                player.sendMessage(Text.translatable("info.shape-shifter-curse.cursed_moon_forced").formatted(Formatting.DARK_PURPLE));
            }
        }

        // 立即向所有在线玩家同步状态
        boolean currentIsNight = isNight(world);
        for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            ModPacketsS2CServer.sendCursedMoonData(player, newTime, getDay(world), true, currentIsNight);
        }
    }

    /*public static void skipTonightCursedMoon(ServerWorld world) {
        CursedMoonData data = ShapeShifterCurseFabric.cursedMoonData.getInstance();

        if (data.isTonightCursedMoon) {
            data.isTonightCursedMoon = false;
            data.save(world);
            ShapeShifterCurseFabric.LOGGER.info("Tonight's CursedMoon has been skipped. Day: " + day);

            // 立即向所有在线玩家同步状态
            boolean currentIsNight = isNight();
            for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                ModPacketsS2C.sendCursedMoonData(player, day_time, day, false, currentIsNight);
                player.sendMessage(Text.translatable("info.shape-shifter-curse.cursed_moon_skipped").formatted(Formatting.GRAY));
            }
        }
    }*/


    public static void applyMoonEffect(ServerPlayerEntity player){
        // 处于Cursed Moon时的逻辑
        // 文本提示
        PlayerFormComponent formComp = RegPlayerFormComponent.PLAYER_FORM.get(player);
        if(!formComp.isMoonEffectApplied()){
            boolean isOverworld = player.getWorld().getRegistryKey() == World.OVERWORLD;
            if(FormAbilityManager.getForm(player).equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)){
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
            formComp.setMoonEffectApplied(true);
            RegPlayerFormComponent.PLAYER_FORM.sync(player);
        }
    }

    public static void applyEndMoonEffect(ServerPlayerEntity player){
        // 结束Cursed Moon时的逻辑
        PlayerFormComponent formComp = RegPlayerFormComponent.PLAYER_FORM.get(player);
        if(!formComp.isEndMoonEffectApplied()){
            boolean wasByCursedMoon = RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon();
            ShapeShifterCurseFabric.LOGGER.info("is player form by cursed moon? : " + wasByCursedMoon);
            if(FormAbilityManager.getForm(player).equals(RegPlayerForms.ORIGINAL_BEFORE_ENABLE)) {
                player.sendMessage(Text.translatable("info.shape-shifter-curse.end_cursed_moon_before_enable").formatted(Formatting.LIGHT_PURPLE));
            }
            else if(FormAbilityManager.getForm(player).getIndex() == 0 &&
                    !RegPlayerFormComponent.PLAYER_FORM.get(player).isByCursedMoon()){
                // 触发Bug成就（特性！）
                ShapeShifterCurseFabric.ON_END_CURSED_MOON_BUGGED_FORM_2.trigger(player);
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
                else if(FormAbilityManager.getForm(player).equals(RegPlayerForms.ORIGINAL_SHIFTER)){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.end_cursed_moon_special").formatted(Formatting.LIGHT_PURPLE));
                }
                else if(FormAbilityManager.getForm(player).getIndex() == 3){
                    // 永久形态
                }
                else if(currentFormComponent.isByCursedMoon()){
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.end_cursed_moon").formatted(Formatting.LIGHT_PURPLE));
                    // 触发自定义成就
                    ShapeShifterCurseFabric.ON_END_CURSED_MOON.trigger(player);
                }
            }
            ShapeShifterCurseFabric.LOGGER.info("Cursed Moon ends!");
            TransformManager.setIsByCursedMoonEnd(player, true);
            RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(true);
            FormAbilityManager.saveForm(player);
            RegPlayerFormComponent.PLAYER_FORM.sync(player);
            // transform
            if(wasByCursedMoon && !RegPlayerFormComponent.PLAYER_FORM.get(player).isByCure()){
                TransformManager.handleMoonEndTransform(player);
            }
            //clearFormFlag(player);
            //TransformManager.clearMoonEndFlags(player);
            formComp.setEndMoonEffectApplied(true);
            FormAbilityManager.saveForm(player);
            RegPlayerFormComponent.PLAYER_FORM.sync(player);
            FormAbilityManager.saveForm(player);
        }
    }

    public static void resetMoonEffect(ServerPlayerEntity player){
        // reset moon effect so that it can be triggered again
        PlayerFormComponent formComp = RegPlayerFormComponent.PLAYER_FORM.get(player);
        formComp.setEndMoonEffectApplied(false);
        formComp.setMoonEffectApplied(false);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
        FormAbilityManager.saveForm(player);
    }

    /*public static void resetCursedMoonForNewDay(ServerWorld world) {
        CursedMoonData data = ShapeShifterCurseFabric.cursedMoonData.getInstance();
        // 如果今天已经被标记为诅咒之月，在新的一天开始时将其重置
        if (data.isTonightCursedMoon) {
            data.isTonightCursedMoon = false;
            data.save(world);
            ShapeShifterCurseFabric.LOGGER.info("Resetting Cursed Moon status for new day: " + day);
        }
        clientIsCursedMoon = false;
        clientIsNight = false;
    }*/

    public static void resetCursedMoonForNewDay(ServerWorld world) {
        // 基于月相的系统不需要特殊的重置逻辑
        // 只需要重置客户端状态
        clientIsCursedMoon = isCursedMoonByPhase(world);
        clientIsNight = isNight(world);

        ShapeShifterCurseFabric.LOGGER.info("Updated Cursed Moon status for new day: " + day + ", Moon Phase: " + world.getMoonPhase() + ", Is Cursed: " + clientIsCursedMoon);
    }


}
