package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.networking.ModPackets;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormPhase;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

import java.util.Iterator;

import static net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager.getForm;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.loadInstinctComp;
import static net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager.handleProgressiveTransform;

public class InstinctTicker {
    public static float currentInstinctValue = 0.0f;
    // public static boolean showInstinctBar = false;
    // public static boolean isInstinctLock = false;
    public static boolean isUnderCursedMoon = false;
    public static boolean isPausing = false;


    public static void loadInstinct(PlayerEntity player) {
        PlayerInstinctComponent comp = loadInstinctComp(player);
        if(comp != null){
            PlayerInstinctComponent thisComp = RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.get(player);
            thisComp.instinctValue = comp.instinctValue;
            thisComp.immediateEffects = comp.immediateEffects;
            thisComp.sustainedEffects = comp.sustainedEffects;
            isPausing = false;
        }
    }

    public static void clearInstinct(PlayerEntity player) {
        PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
        comp.instinctValue = 0.0f;
        comp.immediateEffects.clear();
        comp.sustainedEffects.clear();
        RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.sync(player);
    }

    public static void tick(ServerPlayerEntity player) {
        PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);

        if(CursedMoon.isCursedMoon(player.getWorld()) && CursedMoon.isNight(player.getWorld())){
            isUnderCursedMoon = true;
        }
        else{
            isUnderCursedMoon = false;
        }

        // 处理立即效果
        // Process immediate effects
        if (isPausing || isUnderCursedMoon) {
            // 诅咒之月下不加本能值
            comp.immediateEffects.clear();
        } else {
            processImmediateEffects(comp);
        }

        // 计算当前速率
        // Calculate the current instinct growth rate
        comp.currentInstinctRate = (isPausing || isUnderCursedMoon) ? 0.0f : calculateCurrentRate(player, comp);

        // 应用持续增长
        // Apply the instinct growth rate
        comp.instinctValue = MathHelper.clamp(
                comp.instinctValue + comp.currentInstinctRate,
                0f,
                StaticParams.INSTINCT_MAX
        );

        /*if(comp.instinctValue >= 80.0f && comp.instinctValue < 99.99f && player.getWorld().isClient) {
            applyInstinctThresholdEffect();
        }*/
        // 切换为使用数据包触发
        // If the instinct value is between 80 and 99.99, send a packet to the server
        if (comp.instinctValue >= 80.0f && comp.instinctValue < 99.99f && !player.getWorld().isClient && player instanceof ServerPlayerEntity) {
            PacketByteBuf buf = PacketByteBufs.create();
            ServerPlayNetworking.send((ServerPlayerEntity) player, ModPackets.INSTINCT_THRESHOLD_EFFECT_ID, buf);
        }
        //ShapeShifterCurseFabric.LOGGER.info("currentInstinctFromComp: " + comp.instinctValue);
        // 判断当前状态
        // Judge the current state
        judgeInstinctState(player, comp);

        currentInstinctValue = comp.instinctValue;
        // 检查触发条件
        // Check if the instinct value meets the threshold
        checkThreshold(player, comp);
        player.syncComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);
    }

    private static float judgeInstinctGrowRate(PlayerEntity player){
        PlayerFormComponent formComp = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        PlayerFormPhase currentPhase = formComp.getCurrentForm().getPhase();
        switch (currentPhase){
            case PHASE_CLEAR:
                return 0.0f;
            case PHASE_0:
                return StaticParams.INSTINCT_INCREASE_RATE_0;
            case PHASE_1:
                return StaticParams.INSTINCT_INCREASE_RATE_1;
            case PHASE_2:
                // 立刻涨满
                return 100.0f;
            case PHASE_3:
                // 立刻涨满
                return 100.0f;
            case PHASE_SP:
                // 立刻涨满
                return 100.0f;
        }
        return 0.0f;
    }

    private static void judgeInstinctState(PlayerEntity player, PlayerInstinctComponent comp){
        // 判断当前状态，供进度条使用
        // Judge the current state for the progress bar
        // PlayerFormBase form = getForm(player);
        // PlayerFormPhase currentPhase = form.getPhase();
        // showInstinctBar = !(currentPhase == PlayerFormPhase.PHASE_CLEAR || currentPhase == PlayerFormPhase.PHASE_3);

        float baseRate = judgeInstinctGrowRate(player);
        if(comp.currentInstinctRate > baseRate){
            comp.isInstinctIncreasing = true;
            comp.isInstinctDecreasing = false;
        }
        else if(comp.currentInstinctRate < baseRate){
            comp.isInstinctIncreasing = false;
            comp.isInstinctDecreasing = true;
        }
        else{
            comp.isInstinctIncreasing = false;
            comp.isInstinctDecreasing = false;
        }

        // if(getForm(player).FormIndex < 2){
        //     if(isUnderCursedMoon){
        //         isInstinctLock = true;
        //     }
        //     else{
        //         isInstinctLock = false;
        //     }
        // }
        // else{
        //     isInstinctLock = true;
        // }
    }

    private static void processImmediateEffects(PlayerInstinctComponent comp) {
        while (!comp.immediateEffects.isEmpty()) {
            InstinctEffect effect = comp.immediateEffects.poll();
            comp.instinctValue = MathHelper.clamp(
                    comp.instinctValue + effect.getValue(),
                    0f,
                    StaticParams.INSTINCT_MAX
            );

        }
    }

    public static float calculateCurrentRate(PlayerEntity player, PlayerInstinctComponent comp) {
        float rate = judgeInstinctGrowRate(player);
        Iterator<InstinctEffect> iterator = comp.sustainedEffects.iterator();
        while (iterator.hasNext()) {
            InstinctEffect effect = iterator.next();
            if (effect.IsEffectExist()) {
                rate += effect.getRateModifier();
            }
            else {
                iterator.remove();
            }
        }
        return rate;
    }

    private static void checkThreshold(ServerPlayerEntity player, PlayerInstinctComponent comp) {
        if (comp.instinctValue >= StaticParams.INSTINCT_MAX) {
            // 这里放置满instinct时要触发的逻辑
            // Here is the logic to be triggered when the instinct is full
            if(getForm(player).FormIndex < 2){
                handleProgressiveTransform(player, false);
            }
            comp.instinctValue = 0f;
        }
    }
}
