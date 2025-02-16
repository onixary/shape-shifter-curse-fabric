package net.onixary.shapeShifterCurseFabric.player_form.transform;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker;

import static net.onixary.shapeShifterCurseFabric.post_effect.TransformFX.beginTransformEffect;
import static net.onixary.shapeShifterCurseFabric.post_effect.TransformFX.endTransformEffect;

public class TransformManager {
    private TransformManager() {
    }

    private static int beginTransformEffectTicks = 0;
    private static int endTransformEffectTicks = 0;
    private static boolean isEffectActive = false;
    private static boolean isEndEffectActive = false;
    private static PlayerEntity curPlayer = null;
    private static PlayerForms curToForm = null;

    public static void handleProgressiveTransform(PlayerEntity player, boolean isByCursedMoon){
        PlayerForms currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        int currentFormIndex = currentForm.getIndex();
        String currentFormGroup = currentForm.getGroup();
        PlayerForms toForm = null;
        switch (currentFormIndex) {
            case 0:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[1];
                break;
            case 1:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[2];
                break;
            case 2:
                if(isByCursedMoon){
                    toForm = PlayerForms.getFormsByGroup(currentFormGroup)[0];
                }
                else{
                    ShapeShifterCurseFabric.LOGGER.info("Triggered transformation when at max phase, this should not happen!");
                }
            default:
                break;
        }
        if (toForm == null) {
            ShapeShifterCurseFabric.LOGGER.info("No next form found, this should not happen!");
            return;
        }
        // todo: 效果相关逻辑

        curPlayer = player;
        curToForm = toForm;
        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + curPlayer + " To Form: " + curToForm);
        handleTransformEffect();
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
    }

    public static void update() {
        if(isEffectActive){
            beginTransformEffectTicks--;
            if(beginTransformEffectTicks <= 0){
                isEffectActive = false;
                isEndEffectActive = true;
                if (curPlayer != null) {
                    FormAbilityManager.applyForm(curPlayer, curToForm);
                    RegPlayerFormComponent.PLAYER_FORM.sync(curPlayer);
                } else {
                    ShapeShifterCurseFabric.LOGGER.error("curPlayer is null when trying to apply form!");
                }
                endTransformEffect();
            }
        }
        else if(isEndEffectActive){
            endTransformEffectTicks--;
            if(endTransformEffectTicks <= 0){
                // todo: 结束时的相关逻辑放在这里
                InstinctTicker.isPausing = false;
                isEndEffectActive = false;
                beginTransformEffectTicks = 0;
                endTransformEffectTicks = 0;
            }
        }
    }

    public static void handleDirectTransform(PlayerEntity player, PlayerForms toForm){
        // todo: 效果相关逻辑
        curPlayer = player;
        curToForm = toForm;
        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + curPlayer + " To Form: " + curToForm);
        handleTransformEffect();
        // FormAbilityManager.applyForm(player, toForm);
    }

    private static void handleTransformEffect(){
        beginTransformEffect();
        beginTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_IN;
        endTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_OUT;
        isEffectActive = true;
        InstinctTicker.isPausing = true;
    }
}
