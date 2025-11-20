package net.onixary.shapeShifterCurseFabric.status_effects.transformative_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import org.jetbrains.annotations.Nullable;

// 这个Instance仅出现在服务器端 客户端为StatusEffectInstance
public class TransformativeStatusInstance extends StatusEffectInstance {

    public TransformativeStatusInstance(BaseTransformativeStatusEffect effect, int duration) {
        super(effect, duration, 0, false, false, true);  // 默认不显示粒子效果
    }

    @Override
    public boolean update(LivingEntity entity, Runnable overwriteCallback) {
        // 仅限因为持续时间结束而消失时触发成就
        if (entity instanceof ServerPlayerEntity player && this.getDuration() <= 1) {
            ShapeShifterCurseFabric.ON_TRANSFORM_EFFECT_FADE.trigger(player);
        }
        return super.update(entity, overwriteCallback);
    }

    public void ActiveEffect(ServerPlayerEntity player) {
        BaseTransformativeStatusEffect effect = this.getTransformativeEffectType();
        if (effect != null) {
            effect.ActiveEffect(player);
        }
    }

    public static @Nullable TransformativeStatusInstance formStatusEffectInstance(StatusEffectInstance instance) {
        StatusEffect effect = instance.getEffectType();
        if (effect instanceof BaseTransformativeStatusEffect baseTransformativeStatusEffect) {
            return new TransformativeStatusInstance(baseTransformativeStatusEffect, instance.getDuration());
        }
        return null;
    }

    public @Nullable BaseTransformativeStatusEffect getTransformativeEffectType() {
        if (super.getEffectType() instanceof BaseTransformativeStatusEffect baseTransformativeStatusEffect) {
            return baseTransformativeStatusEffect;
        }
        return null;
    }
}
