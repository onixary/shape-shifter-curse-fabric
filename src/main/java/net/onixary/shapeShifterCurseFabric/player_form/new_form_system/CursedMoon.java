package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.ITransformReason;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.utils.TransformManager;

import java.util.Arrays;

public class CursedMoon {
    // Server Side
    public static long day = -1;  // 用于实现进入新的日期时自动同步诅咒之月 要不是CommonConfig客户端和服务器端可能不同 连同步都不需要
    // Client Side
    public static boolean isCursedMoon = false;  // 由同步包更新
    public static boolean middayMessageSent = false;  // 接收到同步包时自动置为 false

    public static boolean isCursedMoonByPhase(int moonPhase) {
        int[] curseMoonPhase = ShapeShifterCurseFabric.commonConfig.curseMoonPhase;
        return Arrays.stream(curseMoonPhase).anyMatch(phase -> phase == moonPhase);
    }

    public static boolean isCursedMoonDay(World world) {
        if (world.isClient) {
            return isCursedMoon;
        }
        int moonPhase = world.getMoonPhase();
        return isCursedMoonByPhase(moonPhase);
    }

    public static void clientTick(World world) {
        if (!isCursedMoonDay(world)) { return; }
        long dayTime = world.getTimeOfDay() % 24000;
        if (dayTime >= 6000L && dayTime < 12500L && !middayMessageSent) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                if (player.getWorld().getRegistryKey() != World.OVERWORLD) {
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.before_cursed_moon_nether").formatted(Formatting.LIGHT_PURPLE));
                } else {
                    player.sendMessage(Text.translatable("info.shape-shifter-curse.before_cursed_moon").formatted(Formatting.LIGHT_PURPLE));
                }
            }
            middayMessageSent = true;
        }
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
        if (!RegPlayerForms.ORIGINAL_BEFORE_ENABLE.isPlayerForm(player)) {
            IForm nowForm = component.nowForm;
            IForm targetForm = component.nowForm._getNextForm(player, ITransformReason.CursedMoon);
            if (!nowForm.isEquals(targetForm)) {
                component.BeforeCursedMoonAppliedForm = nowForm;
                component.AfterCursedMoonAppliedForm = targetForm;
                TransformManager.startTransform(player, targetForm, null);
            }
        }
        component.sync();
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
    }

    public static void serverTick(World world) {
        if (world.isClient) return;
        long timeOfDay = world.getTimeOfDay();
        long nowDay = timeOfDay / 24000;
        long dayTime = timeOfDay % 24000;
        if (nowDay != day) {
            day = nowDay;
            // TODO 同步数据给全部在线玩家
        }
        if (dayTime >= 12500L && dayTime < 23000L && isCursedMoonDay(world)) {
            for (PlayerEntity player : world.getPlayers()) {
                applyStartCursedMoonEffect(world, player);
            }
        } else {
            for (PlayerEntity player : world.getPlayers()) {
                applyEndCursedMoonEffect(world, player);
            }
        }
    }
}
