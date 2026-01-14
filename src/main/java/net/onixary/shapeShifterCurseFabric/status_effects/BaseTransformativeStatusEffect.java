package net.onixary.shapeShifterCurseFabric.status_effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;

// 自定义药水效果基类（含类型和回调）
public abstract class BaseTransformativeStatusEffect extends StatusEffect {
    public boolean IS_INSTANT = false;
    private final PlayerFormBase toForm;

    public BaseTransformativeStatusEffect(PlayerFormBase toForm, StatusEffectCategory category, int color, boolean isInstant) {
        super(category, color);
        IS_INSTANT = isInstant;
        this.toForm = toForm;
    }

    public PlayerFormBase getToForm(PlayerEntity player) {
        return toForm;
    }

    // 抽象方法：效果应用时的回调
    public void ActiveEffect(ServerPlayerEntity player){

    };
}
