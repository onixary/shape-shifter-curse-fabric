package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormPhase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

import java.util.Iterator;

import static net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient.applyInstinctThresholdEffect;
import static net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager.getForm;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.loadInstinctComp;
import static net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager.handleProgressiveTransform;

public class InstinctTicker {
    public static float currentInstinctValue = 0.0f;
    public static float currentInstinctRate;
    public static boolean showInstinctBar = false;
    public static boolean isInstinctIncreasing = false;
    public static boolean isInstinctDecreasing = false;
    public static boolean isInstinctLock = false;
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

    public static void tick(PlayerEntity player) {
        PlayerInstinctComponent comp = player.getComponent(RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP);

        if(CursedMoon.isCursedMoon() && CursedMoon.isNight()){
            isUnderCursedMoon = true;
        }
        else{
            isUnderCursedMoon = false;
        }

        // 处理立即效果
        processImmediateEffects(comp);

        // 计算当前速率
        currentInstinctRate = (isPausing || isUnderCursedMoon) ? 0.0f : calculateCurrentRate(player, comp);

        // 应用持续增长
        comp.instinctValue = MathHelper.clamp(
                comp.instinctValue + currentInstinctRate,
                0f,
                StaticParams.INSTINCT_MAX
        );
        RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.sync(player);

        if(comp.instinctValue >= 80.0f && comp.instinctValue < 99.99f && player.getWorld().isClient) {
            applyInstinctThresholdEffect();
        }
        //ShapeShifterCurseFabric.LOGGER.info("currentInstinctFromComp: " + comp.instinctValue);
        // 判断当前状态
        judgeInstinctState(player, currentInstinctRate);

        currentInstinctValue = comp.instinctValue;
        // 检查触发条件
        checkThreshold(player, comp);
    }

    private static float judgeInstinctGrowRate(PlayerEntity player){
        PlayerFormComponent formComp = player.getComponent(RegPlayerFormComponent.PLAYER_FORM);
        PlayerFormPhase currentPhase = RegFormConfig.CONFIGS.get(formComp.getCurrentForm()).getPhase();
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

    private static void judgeInstinctState(PlayerEntity player, float instinctRate){
        // 判断当前状态，供进度条使用
        PlayerForms form = getForm(player);
        PlayerFormPhase currentPhase = RegFormConfig.CONFIGS.get(form).getPhase();
        showInstinctBar = !(currentPhase == PlayerFormPhase.PHASE_CLEAR || currentPhase == PlayerFormPhase.PHASE_3);

        float baseRate = judgeInstinctGrowRate(player);
        if(instinctRate > baseRate){
            isInstinctIncreasing = true;
            isInstinctDecreasing = false;
        }
        else if(instinctRate < baseRate){
            isInstinctIncreasing = false;
            isInstinctDecreasing = true;
        }
        else{
            isInstinctIncreasing = false;
            isInstinctDecreasing = false;
        }

        if(getForm(player).getIndex() < 2){
            if(isUnderCursedMoon){
                isInstinctLock = true;
            }
            else{
                isInstinctLock = false;
            }
        }
        else{
            isInstinctLock = true;
        }
    }

    private static void processImmediateEffects(PlayerInstinctComponent comp) {
        while (!comp.immediateEffects.isEmpty()) {
            InstinctEffectType effect = comp.immediateEffects.poll();
            comp.instinctValue = MathHelper.clamp(
                    comp.instinctValue + effect.getValue(),
                    0f,
                    StaticParams.INSTINCT_MAX
            );

        }
    }

    public static float calculateCurrentRate(PlayerEntity player, PlayerInstinctComponent comp) {
        float rate = judgeInstinctGrowRate(player);
        Iterator<InstinctEffectType> iterator = comp.sustainedEffects.iterator();
        while (iterator.hasNext()) {
            InstinctEffectType effect = iterator.next();
            rate += effect.getRateModifier();
            iterator.remove();
        }
        return rate;
    }

    private static void checkThreshold(PlayerEntity player, PlayerInstinctComponent comp) {
        if (comp.instinctValue >= StaticParams.INSTINCT_MAX) {
            // 这里放置满instinct时要触发的逻辑
            if(getForm(player).getIndex() < 2){
                handleProgressiveTransform(player, false);
            }
            comp.instinctValue = 0f;
        }
    }
}
