package net.onixary.shapeShifterCurseFabric.player_form.transform;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

public class TransformManager {
    private TransformManager() {
    }

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
        FormAbilityManager.applyForm(player, toForm);

        RegPlayerFormComponent.PLAYER_FORM.sync(player);
    }

    public static void handleDirectTransform(PlayerEntity player, PlayerForms toForm){
        // todo: 效果相关逻辑
        FormAbilityManager.applyForm(player, toForm);
    }
}
