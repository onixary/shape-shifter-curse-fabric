package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class BreathingUnderWaterPower extends Power {

    public BreathingUnderWaterPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("breathing_under_water"),
                new SerializableData(),
                data -> (powerType, livingEntity) -> new BreathingUnderWaterPower(
                        powerType,
                        livingEntity
                )
        ).allowCondition();
    }
}
