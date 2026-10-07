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

public class EnhancedFallingAttackPower extends Power {

    private final ActionFactory<Entity>.Instance targetActionOnHit;
    private final ActionFactory<Entity>.Instance selfActionOnHit;
    private final float maxMultiplier;

    public EnhancedFallingAttackPower(PowerType<?> type, LivingEntity entity, ActionFactory<Entity>.Instance targetActionOnHit, ActionFactory<Entity>.Instance selfActionOnHit) {
        this(type, entity, targetActionOnHit, selfActionOnHit, 2.0f);
    }

    public EnhancedFallingAttackPower(PowerType<?> type, LivingEntity entity, ActionFactory<Entity>.Instance targetActionOnHit, ActionFactory<Entity>.Instance selfActionOnHit, float maxMultiplier) {
        super(type, entity);
        this.targetActionOnHit = targetActionOnHit;
        this.selfActionOnHit = selfActionOnHit;
        this.maxMultiplier = Float.isFinite(maxMultiplier) ? Math.max(1.0f, maxMultiplier) : 2.0f;
    }

    public float getFallMultiplier(float fallDistance) {
        float progress = net.minecraft.util.math.MathHelper.clamp(fallDistance - 1.0f, 0.0f, 1.0f);
        return 1.0f + (maxMultiplier - 1.0f) * progress;
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
                ShapeShifterCurseFabric.identifier("enhanced_falling_attack"),
                new SerializableData()
                        .add("max_multiplier", SerializableDataTypes.FLOAT, 2.0f)
                        .add("target_action_on_critical_hit", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("self_action_on_critical_hit", ApoliDataTypes.ENTITY_ACTION, null),
                data -> (type, entity) -> new EnhancedFallingAttackPower(
                        type,
                        entity,
                        data.get("target_action_on_critical_hit"),
                        data.get("self_action_on_critical_hit"),
                        data.getFloat("max_multiplier")
                )
        ).allowCondition();
    }
}
