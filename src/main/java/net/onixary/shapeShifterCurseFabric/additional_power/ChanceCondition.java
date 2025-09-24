package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Random;

public class ChanceCondition {

    public static <T> boolean condition(SerializableData.Instance data, T t) {
        return new Random().nextFloat() < data.getFloat("chance");
    }

    public static <T> ConditionFactory<T> getFactory() {
        return new ConditionFactory<>(
            ShapeShifterCurseFabric.identifier("chance"),
            new SerializableData()
                .add("chance", SerializableDataTypes.FLOAT),
            ChanceCondition::condition
        );
    }

}
