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
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.function.Predicate;

public class ActionOnSprintingToSneakingPower extends Power {

    private final ActionFactory<Entity>.Instance entityAction;
    private final Predicate<Entity> entityCondition;

    public ActionOnSprintingToSneakingPower(PowerType<?> type, LivingEntity entity, ActionFactory<Entity>.Instance entityAction, Predicate<Entity> condition) {
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
                    ShapeShifterCurseFabric.LOGGER.info("ActionOnSprintingToSneakingPower executed for player");
                }
            }
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("action_on_sprinting_to_sneaking"),
                new SerializableData()
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data -> (type, entity) -> new ActionOnSprintingToSneakingPower(
                        type,
                        entity,
                        data.get("entity_action"),
                        data.get("entity_condition")
                )
        ).allowCondition();
    }
}
