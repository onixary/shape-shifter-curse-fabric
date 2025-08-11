package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class EnhancedFallingAttackPower extends Power {

    private final ActionFactory<Entity>.Instance targetActionOnHit;
    private final ActionFactory<Entity>.Instance selfActionOnHit;

    public EnhancedFallingAttackPower(PowerType<?> type, LivingEntity entity, ActionFactory<Entity>.Instance targetActionOnHit, ActionFactory<Entity>.Instance selfActionOnHit) {
        super(type, entity);
        this.targetActionOnHit = targetActionOnHit;
        this.selfActionOnHit = selfActionOnHit;
    }

    public void executeTargetAction(Entity target) {
        if (targetActionOnHit != null) {
            targetActionOnHit.accept(target);
        }
    }

    public void executeSelfAction() {
        if (selfActionOnHit != null) {
            selfActionOnHit.accept(entity);
        }
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("enhanced_falling_attack"),
                new SerializableData()
                        .add("target_action_on_critical_hit", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("self_action_on_critical_hit", ApoliDataTypes.ENTITY_ACTION, null),
                data -> (type, entity) -> new EnhancedFallingAttackPower(
                        type,
                        entity,
                        data.get("target_action_on_critical_hit"),
                        data.get("self_action_on_critical_hit")
                )
        ).allowCondition();
    }
}
