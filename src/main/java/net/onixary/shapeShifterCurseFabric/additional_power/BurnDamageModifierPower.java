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

public class BurnDamageModifierPower extends Power {

    private final ActionFactory<Entity>.Instance action;
    private final float damageModifier;

    public BurnDamageModifierPower(PowerType<?> type, LivingEntity entity,ActionFactory<Entity>.Instance action, float damageModifier) {
        super(type, entity);
        this.action = action;
        this.damageModifier = damageModifier;
    }

    public float getDamageModifier() {
        return damageModifier;
    }

    public void executeAction(Entity target) {
        if (action != null) {
            action.accept(target);
        }
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("burn_damage_modifier"),
                new SerializableData()
                        .add("modifier", SerializableDataTypes.FLOAT, 1.0f)
                        .add("action", ApoliDataTypes.ENTITY_ACTION, null),
                data -> (type, entity) -> new BurnDamageModifierPower(type, entity, data.get("action"), data.getFloat("modifier"))
        ).allowCondition();
    }
}