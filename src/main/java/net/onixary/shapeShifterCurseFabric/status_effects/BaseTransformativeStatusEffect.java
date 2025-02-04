package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.PlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.effects.ToBatStatus0;

// 自定义药水效果基类（含类型和回调）
public class BaseTransformativeStatusEffect extends StatusEffect {
    public boolean IS_INSTANT = false;
    private final PlayerForms toForm;

    public BaseTransformativeStatusEffect(PlayerForms toForm, StatusEffectCategory category, int color, boolean isInstant) {
        super(category, color);
        IS_INSTANT = isInstant;
        this.toForm = toForm;
    }

    public PlayerForms getToForm() {
        return toForm;
    }

    // 抽象方法：效果结束时的回调
    public void onEffectApplied(PlayerEntity player){

    };


    public void onEffectCanceled(PlayerEntity player){

    };
}
