package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.ArrayList;
import java.util.List;

public class ApplyEffectPower extends Power {

    private final List<StatusEffectInstance> effects;
    private final List<StatusEffectInstance> storeEffects;
    private boolean isApplied = false;

    public ApplyEffectPower(PowerType<?> type, LivingEntity entity, List<StatusEffectInstance> effects) {
        super(type, entity);
        this.effects = effects;
        this.storeEffects = new ArrayList<>();
        this.setTicking(true);
    }

    @Override
    public void tick() {
        if (this.isActive() && !this.isApplied) {
            this.ApplyEffects();
            this.isApplied = true;
        } else if (!this.isActive() && this.isApplied) {
            this.RemoveEffects();
            this.isApplied = false;
        }
    }

    private void ApplyEffects() {
        for (StatusEffectInstance effect : this.effects) {
            if (this.entity.hasStatusEffect(effect.getEffectType())) {
                this.storeEffects.add(this.entity.getStatusEffect(effect.getEffectType()));
                this.entity.removeStatusEffect(effect.getEffectType());
            }
            this.entity.addStatusEffect(new StatusEffectInstance(effect));
        }
    }

    private void RemoveEffects() {
        for (StatusEffectInstance effect : this.effects) {
            this.entity.removeStatusEffect(effect.getEffectType());
        }
        for (StatusEffectInstance effect : this.storeEffects) {
            this.entity.addStatusEffect(effect);
        }
        this.storeEffects.clear();
    }

    public void onRemoved() {
        if (this.isApplied) {
            this.RemoveEffects();
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("apply_effect"),
                new SerializableData()
                        .add("status_effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null),  // 时效必须为无限
                data -> (powerType, entity) -> new ApplyEffectPower(powerType, entity, data.get("status_effects"))
        ).allowCondition();
    }
}
