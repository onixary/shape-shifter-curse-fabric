package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class WaterFlexibilityPower extends Power {

    private final float resistance;

    public WaterFlexibilityPower(PowerType<?> type, LivingEntity entity, float resistance) {
        super(type, entity);
        this.resistance = Math.max(0.0f, Math.min(1.0f, resistance));
    }

    public float getResistance() {
        return resistance;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("water_flexibility"),
                new SerializableData()
                        .add("water_flex", SerializableDataTypes.FLOAT, 0.5f),
                data -> (type, entity) -> new WaterFlexibilityPower(type, entity, data.getFloat("water_flex"))
        ).allowCondition();
    }
}
