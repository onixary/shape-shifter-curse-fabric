package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class SlowdownPercentPower extends Power {
    public float Multiplier = 1.0f;

    public SlowdownPercentPower(PowerType<?> type, LivingEntity entity, float multiplier) {
        super(type, entity);
        this.Multiplier = multiplier;
    }


    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("slowdown_percent"),
                new SerializableData()
                        .add("multiplier", SerializableDataTypes.FLOAT, 1.0f),
                data -> (type, entity) -> new SlowdownPercentPower(
                        type,
                        entity,
                        data.getFloat("multiplier")
                )
        ).allowCondition();
    }
}
