package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Predicate;

public class ActionOnJumpPower extends Power {

    private final ActionFactory<Entity>.Instance entityAction;
    private final Predicate<Entity> entityCondition;

    public ActionOnJumpPower(PowerType<?> type, LivingEntity entity, ActionFactory<Entity>.Instance entityAction, Predicate<Entity> condition) {
        super(type, entity);
        this.entityAction = entityAction;
        this.entityCondition = condition;
    }

    public void executeAction() {
        if (entity instanceof PlayerEntity player) {
            // 检查condition是否满足
            if (entityCondition == null || entityCondition.test(player)) {
                // 执行action
                if (entityAction != null) {
                    entityAction.accept(player);
                }
            }
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("action_on_jump"),
                new SerializableData()
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data -> (type, entity) -> new ActionOnJumpPower(
                        type,
                        entity,
                        data.get("entity_action"),
                        data.get("entity_condition")
                )
        ).allowCondition();
    }
}
