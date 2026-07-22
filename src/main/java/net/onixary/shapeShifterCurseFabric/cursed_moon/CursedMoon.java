package net.onixary.shapeShifterCurseFabric.cursed_moon;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.event.SSCEvent;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.ITransformReason;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.utils.TransformManager;

import java.util.Arrays;
import java.util.Optional;

public class CursedMoon {
    // Server Side
    public static long day = -1;  // 用于实现进入新的日期时自动同步诅咒之月 要不是CommonConfig客户端和服务器端可能不同 连同步都不需要

    public static boolean isCursedMoonByPhase(int moonPhase) {
        int[] curseMoonPhase = ShapeShifterCurseFabric.commonConfig.curseMoonPhase;
        return Arrays.stream(curseMoonPhase).anyMatch(phase -> phase == moonPhase);
    }

    public static boolean isCursedMoonDay(World world) {
        if (world.isClient) {
            return CursedMoonClient.isCursedMoon;
        }
        int moonPhase = world.getMoonPhase();
        return isCursedMoonByPhase(moonPhase);
    }

    public static boolean isNight(World world) {
        long timeDayMoon = world.getTimeOfDay() % 24000;
        return timeDayMoon > 12000L && timeDayMoon < 23000L;
    }

    public static boolean isInCursedMoon(World world) {
        return isCursedMoonDay(world) && isNight(world);
    }

    public static void applyStartCursedMoonEffect(World world, PlayerEntity player) {
        // java16+ 真神奇的写法
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }
        if (PlayerFormComponent.COMPONENT.get(player).isCursedMoonApplied) {
            return;
        }
        boolean isOverworld = player.getWorld().getRegistryKey() == World.OVERWORLD;
        if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player)) {
            if (isOverworld) {
                player.sendMessage(Text.translatable("info.shape-shifter-curse.on_cursed_moon_before_enable").formatted(Formatting.LIGHT_PURPLE));
            }
        } else {
            if(isOverworld){
                player.sendMessage(Text.translatable("info.shape-shifter-curse.on_cursed_moon").formatted(Formatting.LIGHT_PURPLE));
            }
            else{
                player.sendMessage(Text.translatable("info.shape-shifter-curse.on_cursed_moon_nether").formatted(Formatting.LIGHT_PURPLE));
            }
            ShapeShifterCurseFabric.ON_TRIGGER_CURSED_MOON.trigger(serverPlayer);
        }
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        component.isCursedMoonApplied = true;
        component.lastTransformByCure = false;
        component.BeforeCursedMoonAppliedForm = null;
        component.AfterCursedMoonAppliedForm = null;
        if (!RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player) && ShapeShifterCurseFabric.commonConfig.enableCursedMoonTransform) {
            IForm nowForm = component.nowForm;
            IForm targetForm = component.nowForm._getNextForm(player, ITransformReason.CursedMoon);
            if (!nowForm.isEquals(targetForm)) {
                component.BeforeCursedMoonAppliedForm = nowForm;
                component.AfterCursedMoonAppliedForm = targetForm;
                TransformManager.startTransform(player, targetForm, null);
                if (FormUtils.CursedMoonFinalForm.hasFlag(nowForm)) {
                    ShapeShifterCurseFabric.ON_TRIGGER_CURSED_MOON_FORM_2.trigger(serverPlayer);
                }
            }
        }
        component.sync();
        SSCEvent.CURSED_MOON_BEGIN.invoker().onEvent(player);
    }

    public static void applyEndCursedMoonEffect(World world, PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }
        if (!PlayerFormComponent.COMPONENT.get(player).isCursedMoonApplied) {
            return;
        }
        boolean isOverworld = player.getWorld().getRegistryKey() == World.OVERWORLD;
        PlayerFormComponent component = PlayerFormComponent.COMPONENT.get(player);
        if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player)) {
            if (isOverworld) {
                player.sendMessage(Text.translatable("info.shape-shifter-curse.end_cursed_moon_before_enable").formatted(Formatting.LIGHT_PURPLE));
            }
        } else {
            // 要不是可以用别的手段降级(比如我拓展的幻形石) 我直接查当前形态与AfterCursedMoonAppliedForm的等级差就行
            if (component.lastTransformByCure) {
                player.sendMessage(Text.translatable("info.shape-shifter-curse.end_cursed_moon_by_cure").formatted(Formatting.LIGHT_PURPLE));
                ShapeShifterCurseFabric.ON_END_CURSED_MOON_CURED.trigger(serverPlayer);
                if (component.AfterCursedMoonAppliedForm != null && component.AfterCursedMoonAppliedForm.getFormTier() == 1) {
                    ShapeShifterCurseFabric.ON_END_CURSED_MOON_CURED_FORM_2.trigger(serverPlayer);
                }
            } else if(RegPlayerForms.ORIGINAL_SHIFTER.isPlayerForm(player)){
                player.sendMessage(Text.translatable("info.shape-shifter-curse.end_cursed_moon_special").formatted(Formatting.LIGHT_PURPLE));
            } else if (component.BeforeCursedMoonAppliedForm != null && component.AfterCursedMoonAppliedForm != null && component.AfterCursedMoonAppliedForm.isPlayerForm(player)) {
                player.sendMessage(Text.translatable("info.shape-shifter-curse.end_cursed_moon").formatted(Formatting.LIGHT_PURPLE));
                ShapeShifterCurseFabric.ON_END_CURSED_MOON.trigger(serverPlayer);
            }
        }
        component.isCursedMoonApplied = false;
        component.lastTransformByCure = false;
        IForm targetForm = component.nowForm._getPrevForm(player, ITransformReason.CursedMoon);
        TransformManager.startTransform(player, targetForm, null);
        component.BeforeCursedMoonAppliedForm = null;
        component.AfterCursedMoonAppliedForm = null;
        component.sync();
        SSCEvent.CURSED_MOON_END.invoker().onEvent(player);
    }

    public static void serverTick(MinecraftServer minecraftServer) {
        World world = minecraftServer.getWorld(World.OVERWORLD);
        if (world.isClient) return;
        long timeOfDay = world.getTimeOfDay();
        long nowDay = timeOfDay / 24000;
        long dayTime = timeOfDay % 24000;
        if (nowDay != day) {
            day = nowDay;
            for (PlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    ModPacketsS2CServer.sendCursedMoonData(serverPlayer, isCursedMoonDay(world));
                }
            }
        }
        if (isInCursedMoon(world)) {
            for (PlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                if(player.isSleeping() && !ShapeShifterCurseFabric.commonConfig.allowSleepInCursedMoon){
                    player.wakeUp();
                }
                applyStartCursedMoonEffect(world, player);
            }
        } else {
            for (PlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                applyEndCursedMoonEffect(world, player);
            }
        }
    }

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
        int currentPhase = world.getMoonPhase();
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

        ShapeShifterCurseFabric.LOGGER.info("CursedMoon manually triggered! Skipped " + daysToSkip + " days to reach moon phase " + targetPhase);

        // 向所有玩家发送消息
        for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            if (!RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player)) {
                player.sendMessage(Text.translatable("info.shape-shifter-curse.cursed_moon_forced").formatted(Formatting.DARK_PURPLE));
            }
        }

        // 立即向所有在线玩家同步状态
        boolean currentIsNight = isNight(world);
        for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            ModPacketsS2CServer.sendCursedMoonData(player, true);
        }
    }
}
