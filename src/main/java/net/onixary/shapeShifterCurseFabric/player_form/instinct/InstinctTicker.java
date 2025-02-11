package net.onixary.shapeShifterCurseFabric.player_form.instinct;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormPhase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.loadInstinctComp;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.saveInstinctComp;
import static net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager.handleProgressiveTransform;

public class InstinctTicker {
    //public static final float BASE_GROWTH_RATE = 0.1f; // 每秒增长6单位（0.1*20tick）
    //public static final float MAX_VALUE = 100f;
    public static void loadInstinct(PlayerEntity player) {
        PlayerInstinctComponent comp = loadInstinctComp(player);
        if(comp != null){
            PlayerInstinctComponent thisComp = RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.get(player);
            thisComp.instinctValue = comp.instinctValue;
            thisComp.immediateEffects = comp.immediateEffects;
            thisComp.sustainedEffects = comp.sustainedEffects;
        }
    }

    public static void tick(PlayerEntity player) {
        PlayerInstinctComponent comp = RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.get(player);

        // 处理立即效果
        processImmediateEffects(comp);

        // 计算当前速率
        float currentRate = calculateCurrentRate(player, comp);

        // 应用持续增长
        comp.instinctValue = MathHelper.clamp(
                comp.instinctValue + currentRate,
                0f,
                StaticParams.INSTINCT_MAX
        );

        // 检查触发条件
        checkThreshold(player, comp);
    }

    private static float judgeInstinctGrowRate(PlayerEntity player){
        PlayerFormComponent formComp = RegPlayerFormComponent.PLAYER_FORM.get(player);
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
        }
        return 0.0f;
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
        for (InstinctEffectType effect : comp.sustainedEffects) {
            rate += effect.getRateModifier();
        }
        return rate / 20f; // 转换为每tick增长量
    }

    private static void checkThreshold(PlayerEntity player, PlayerInstinctComponent comp) {
        if (comp.instinctValue >= StaticParams.INSTINCT_MAX) {
            // 这里放置满instinct时要触发的逻辑
            handleProgressiveTransform(player, false);
            comp.instinctValue = 0f;
        }
    }
}
