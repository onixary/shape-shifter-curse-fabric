package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class FallingProtectionPower extends Power {

    private final float fallDistance;

    public FallingProtectionPower(PowerType<?> type, LivingEntity entity, float fallDistance) {
        super(type, entity);
        this.fallDistance = fallDistance;
    }

    public float getFallDistance() {
        return fallDistance;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("falling_protection"),
                new SerializableData()
                        .add("fall_distance", SerializableDataTypes.FLOAT),
                data -> (type, entity) -> new FallingProtectionPower(type, entity, data.getFloat("fall_distance"))
        ).allowCondition();
    }
}
