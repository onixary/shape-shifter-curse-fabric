package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.List;
import java.util.function.Predicate;

public class ActionOnEntityInRangePower extends Power{

    private final Predicate<Entity> entityCondition;
    private final ActionFactory<Entity>.Instance entityAction;
    private final ActionFactory<Entity>.Instance selfAction;
    private final float actionRadius;
    private final int detectionInterval;

    private int tickCounter = 0;

    public ActionOnEntityInRangePower(PowerType<?> type, LivingEntity entity,
                                      Predicate<Entity> entityCondition,
                                      ActionFactory<Entity>.Instance entityAction,
                                      ActionFactory<Entity>.Instance selfAction,
                                      float actionRadius,
                                      int detectionInterval) {
        super(type, entity);
        this.entityCondition = entityCondition;
        this.entityAction = entityAction;
        this.selfAction = selfAction;
        this.actionRadius = actionRadius;
        this.detectionInterval = detectionInterval;
        this.setTicking(true);
    }

    @Override
    public void tick() {
        if (!(entity instanceof PlayerEntity player) || player.isSpectator()) {
            return;
        }

        if (tickCounter++ % detectionInterval == 0) {
            // 1. 检测范围内的所有实体
            Box searchBox = Box.from(player.getPos()).expand(actionRadius);
            List<Entity> entities = player.getWorld().getOtherEntities(
                    player,
                    searchBox,
                    e -> entityCondition == null || entityCondition.test(e)
            );

            // 2. 对所有符合条件的实体应用动作
            for (Entity targetEntity : entities) {
                if (targetEntity.isAlive() && !targetEntity.isSpectator()) {
                    // 执行实体动作（如果存在）
                    if (entityAction != null) {
                        entityAction.accept(targetEntity);
                    }
                }
            }

            // 3. 执行自身动作（如果存在）
            if (selfAction != null) {
                selfAction.accept(player);
            }

            // 4. 同步状态
            PowerHolderComponent.syncPower(entity, this.type);
        }
    }

    // 工厂方法
    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("action_on_entity_in_range"),
                new SerializableData()
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("action_radius", SerializableDataTypes.FLOAT, 8.0f)
                        .add("detection_interval", SerializableDataTypes.INT, 10),
                data -> (powerType, entity) -> new ActionOnEntityInRangePower(
                        powerType,
                        entity,
                        data.get("entity_condition"),
                        data.get("entity_action"),
                        data.get("self_action"),
                        data.getFloat("action_radius"),
                        data.getInt("detection_interval")
                )
        ).allowCondition();
    }
}
