package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.ClimbingPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.function.Predicate;

public class ClimbingEXPower extends ClimbingPower {
    public final Predicate<Entity> startClimbCondition;
    public final Predicate<Entity> continueClimbCondition;
    public final Predicate<Entity> holdingCondition;
    public final boolean allowHolding;

    public boolean lastIsClimbing = false;

    public ClimbingEXPower(PowerType<?> type, LivingEntity entity, Predicate<Entity> startClimbCondition, Predicate<Entity> continueClimbCondition, Predicate<Entity> holdingCondition, boolean allowHolding) {
        super(type, entity, holdingCondition, allowHolding);
        this.startClimbCondition = startClimbCondition;
        this.continueClimbCondition = continueClimbCondition;
        this.holdingCondition = holdingCondition;
        this.allowHolding = allowHolding;
        this.setTicking();
    }

    public boolean canHold() {
        if (!this.allowHolding) {
            return false;
        }
        if (this.holdingCondition == null) {
            return entity.isSneaking();
        }
        return this.holdingCondition.test(this.entity);
    }

    @Override
    public void tick() {
        if (this.isActive()) {
            entity.fallDistance = 0;
        }
    }

    @Override
    public boolean isActive() {
        if (!super.isActive()) {
            return false;
        }
        boolean active;
        if (lastIsClimbing) {
            active = continueClimbCondition == null || continueClimbCondition.test(this.entity);
        } else {
            active = startClimbCondition == null || startClimbCondition.test(this.entity);
        }
        lastIsClimbing = active;
        return active;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("climbing_ex"),
                new SerializableData()
                        .add("start_climb_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("continue_climb_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("holding_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("allow_holding", SerializableDataTypes.BOOLEAN, true),
                data -> (type, entity) -> new ClimbingEXPower(type, entity, data.get("start_climb_condition"), data.get("continue_climb_condition"), data.get("holding_condition"), data.get("allow_holding"))
        ).allowCondition();
    }
}
