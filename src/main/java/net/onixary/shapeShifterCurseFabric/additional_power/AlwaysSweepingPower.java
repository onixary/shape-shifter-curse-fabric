package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;

public class AlwaysSweepingPower extends Power {

    public AlwaysSweepingPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("always_sweeping"),
                new SerializableData(),
                data -> (type, entity) ->
                        new AlwaysSweepingPower(type, entity)
        ).allowCondition();
    }
}
