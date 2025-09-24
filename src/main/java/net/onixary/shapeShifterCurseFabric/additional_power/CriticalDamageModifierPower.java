package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class CriticalDamageModifierPower extends Power {

    private final ActionFactory<Entity>.Instance action;
    private final float multiplier;

    public CriticalDamageModifierPower(PowerType<?> type, LivingEntity entity, ActionFactory<Entity>.Instance action, float multiplier) {
        super(type, entity);
        this.action = action;
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void executeAction() {
        if (action != null) {
            action.accept(entity);
        }
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("critical_damage_modifier"),
                new SerializableData()
                        .add("action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("multiplier", SerializableDataTypes.FLOAT, 1.5f),
                data -> (type, entity) -> new CriticalDamageModifierPower(
                        type,
                        entity,
                        data.get("action"),
                        data.getFloat("multiplier")
                )
        ).allowCondition();
    }
}
