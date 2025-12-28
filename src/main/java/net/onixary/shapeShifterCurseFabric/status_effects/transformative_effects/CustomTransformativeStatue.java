package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.CTPUtils;

public class CustomTransformativeStatue extends BaseTransformativeStatusEffect {
    public CustomTransformativeStatue() {
        super(null, StatusEffectCategory.NEUTRAL, 0xFFFFFF, false);
    }


    public PlayerFormBase getToForm(PlayerEntity player) {
        return CTPUtils.getTransformativePotionForm(player);
    }

    // 抽象方法：效果应用时的回调
    public void ActiveEffect(ServerPlayerEntity player){
        PlayerFormBase targetForm = this.getToForm(player);
        if (RegPlayerForms.ORIGINAL_BEFORE_ENABLE.FormID.equals(targetForm.FormID)) {
            return;
        }
        TransformManager.handleDirectTransform(player, targetForm, false);
        CTPUtils.resetTransformativePotionForm(player);
    };
}
