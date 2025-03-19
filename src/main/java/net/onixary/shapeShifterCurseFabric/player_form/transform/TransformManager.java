package net.onixary.shapeShifterCurseFabric.player_form.transform;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.FormRandomSelector;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker;
import net.onixary.shapeShifterCurseFabric.screen_effect.TransformOverlay;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.PlayerEffectAttachment;

import static net.onixary.shapeShifterCurseFabric.player_form.effect.PlayerTransformEffectManager.*;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctTicker.clearInstinct;
import static net.onixary.shapeShifterCurseFabric.screen_effect.TransformFX.beginTransformEffect;
import static net.onixary.shapeShifterCurseFabric.screen_effect.TransformFX.endTransformEffect;

public class TransformManager {
    private TransformManager() {
    }

    private static int beginTransformEffectTicks = 0;
    private static int endTransformEffectTicks = 0;
    private static boolean isEffectActive = false;
    private static boolean isEndEffectActive = false;
    private static PlayerEntity curPlayer = null;
    private static PlayerForms curToForm = null;
    private static boolean _isByCursedMoon = false;
    private static boolean _isRegressedFromFinal = false;
    private static boolean _isByCure = false;

    private static float nauesaStrength = 0.0f;
    private static float blackStrength = 0.0f;
    private static boolean isTransforming = false;

    public static boolean isTransforming(){
        return isTransforming;
    }

    public static void handleProgressiveTransform(PlayerEntity player, boolean isByCursedMoon){
        _isByCursedMoon = isByCursedMoon;
        _isRegressedFromFinal = false;
        _isByCure = false;
        PlayerForms currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        int currentFormIndex = currentForm.getIndex();
        String currentFormGroup = currentForm.getGroup();
        PlayerForms toForm = null;
        switch (currentFormIndex) {
            case -2:
                // 未激活mod内容，不做任何事
                break;
            case -1:
                // 如果没有buff则随机选择一个形态，如果有buff则buff形态+1
                toForm = getRandomOrBuffForm(player);
                break;
            case 0:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[1];
                break;
            case 1:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[2];
                break;
            case 2:
                if(isByCursedMoon){
                    toForm = PlayerForms.getFormsByGroup(currentFormGroup)[0];
                    _isRegressedFromFinal = true;
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
        curPlayer = player;
        curToForm = toForm;
        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + curPlayer + " To Form: " + curToForm);
        applyStartTransformEffect((ServerPlayerEntity) player, StaticParams.TRANSFORM_FX_DURATION_IN);
        handleTransformEffect();
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
    }

    public static void handleMoonEndTransform(PlayerEntity player){
        PlayerForms currentForm = player.getComponent(RegPlayerFormComponent.PLAYER_FORM).getCurrentForm();
        int currentFormIndex = currentForm.getIndex();
        String currentFormGroup = currentForm.getGroup();
        PlayerForms toForm = null;
        switch (currentFormIndex) {
            case -2:
                // 不应该触发
                ShapeShifterCurseFabric.LOGGER.error("Moon end transformation triggered when mod is not enabled, this should not happen!");
                break;
            case -1:
                // 不应该触发
                ShapeShifterCurseFabric.LOGGER.error("Moon end transformation triggered when has original form, this should not happen!");
                break;
            case 0:
                if(player.getComponent(RegPlayerFormComponent.PLAYER_FORM).isRegressedFromFinal()){
                    toForm = PlayerForms.getFormsByGroup(currentFormGroup)[2];
                }
                else{
                    toForm = PlayerForms.ORIGINAL_SHIFTER;
                }
                break;
            case 1:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[0];
                break;
            case 2:
                toForm = PlayerForms.getFormsByGroup(currentFormGroup)[1];
            default:
                break;
        }
        if (toForm == null) {
            ShapeShifterCurseFabric.LOGGER.info("No next form found, this should not happen!");
            return;
        }
        curPlayer = player;
        curToForm = toForm;
        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + curPlayer + " To Form: " + curToForm);
        applyStartTransformEffect((ServerPlayerEntity) player, StaticParams.TRANSFORM_FX_DURATION_IN);
        handleTransformEffect();
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
    }

    static PlayerForms getRandomOrBuffForm(PlayerEntity player){
        PlayerEffectAttachment currentTransformEffect = EffectManager.getCurrentEffectAttachment(player);
        if(currentTransformEffect != null){
            return currentTransformEffect.currentToForm;
        }
        else{
            return FormRandomSelector.getRandomFormWithIndexZero();
        }
    }

    public static void update() {
        if(isEffectActive){
            // handle overlay effect
            nauesaStrength = 1.0f - (beginTransformEffectTicks / (float)StaticParams.TRANSFORM_FX_DURATION_IN);
            if(nauesaStrength > 0.8f){
                blackStrength = (nauesaStrength - 0.8f) / 0.2f;
            }
            else{
                blackStrength = 0.0f;
            }
            TransformOverlay.INSTANCE.setNauesaStrength(nauesaStrength);
            TransformOverlay.INSTANCE.setBlackStrength(blackStrength);
            beginTransformEffectTicks--;

            if(beginTransformEffectTicks <= 0){
                isEffectActive = false;
                isEndEffectActive = true;
                if (curPlayer != null) {
                    FormAbilityManager.applyForm(curPlayer, curToForm);
                    RegPlayerFormComponent.PLAYER_FORM.get(curPlayer).setByCursedMoon(_isByCursedMoon);
                    RegPlayerFormComponent.PLAYER_FORM.get(curPlayer).setRegressedFromFinal(_isRegressedFromFinal);
                    RegPlayerFormComponent.PLAYER_FORM.get(curPlayer).setByCure(_isByCure);
                    RegPlayerFormComponent.PLAYER_FORM.sync(curPlayer);
                    if(!_isByCursedMoon){
                        clearInstinct(curPlayer);
                    }

                } else {
                    ShapeShifterCurseFabric.LOGGER.error("curPlayer is null when trying to apply form!");
                }
                applyEndTransformEffect((ServerPlayerEntity) curPlayer, StaticParams.TRANSFORM_FX_DURATION_OUT);
                endTransformEffect();
            }
        }
        else if(isEndEffectActive){
            // handle overlay fade effect
            nauesaStrength = endTransformEffectTicks / (float)StaticParams.TRANSFORM_FX_DURATION_OUT;
            if(nauesaStrength > 0.6f){
                blackStrength = 1.0f;
            }
            else{
                blackStrength = nauesaStrength / 0.6f;
            }
            TransformOverlay.INSTANCE.setNauesaStrength(nauesaStrength);
            TransformOverlay.INSTANCE.setBlackStrength(blackStrength);

            endTransformEffectTicks--;
            if(endTransformEffectTicks <= 0){
                // todo: 结束时的相关逻辑放在这里
                applyFinaleTransformEffect((ServerPlayerEntity) curPlayer, 5);
                InstinctTicker.isPausing = false;
                TransformOverlay.INSTANCE.setEnableOverlay(false);
                isTransforming = false;
                isEndEffectActive = false;
                beginTransformEffectTicks = 0;
                endTransformEffectTicks = 0;
            }
        }
    }

    public static void handleDirectTransform(PlayerEntity player, PlayerForms toForm, boolean isByCure){
        curPlayer = player;
        curToForm = toForm;
        _isByCure = isByCure;
        ShapeShifterCurseFabric.LOGGER.info("Cur Player: " + curPlayer + " To Form: " + curToForm);
        handleTransformEffect();
        applyStartTransformEffect((ServerPlayerEntity) player, StaticParams.TRANSFORM_FX_DURATION_IN);
        // FormAbilityManager.applyForm(player, toForm);
    }

    private static void handleTransformEffect(){
        isTransforming = true;
        beginTransformEffect();
        beginTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_IN;
        endTransformEffectTicks = StaticParams.TRANSFORM_FX_DURATION_OUT;
        isEffectActive = true;
        InstinctTicker.isPausing = true;
        TransformOverlay.INSTANCE.setEnableOverlay(true);
    }

    public static void setFormDirectly(PlayerEntity player, PlayerForms toForm){
        curPlayer = player;
        curToForm = toForm;
        FormAbilityManager.applyForm(curPlayer, curToForm);
        clearInstinct(curPlayer);
        RegPlayerFormComponent.PLAYER_FORM.sync(curPlayer);
    }

    public static void clearFormFlag(PlayerEntity player){
        // when cursed moon ends, clear all flags
        _isByCursedMoon = false;
        _isRegressedFromFinal = false;
        _isByCure = false;
        RegPlayerFormComponent.PLAYER_FORM.get(player).setByCursedMoon(false);
        RegPlayerFormComponent.PLAYER_FORM.get(player).setRegressedFromFinal(false);
        RegPlayerFormComponent.PLAYER_FORM.get(player).setByCure(false);
        RegPlayerFormComponent.PLAYER_FORM.sync(player);
    }
}
