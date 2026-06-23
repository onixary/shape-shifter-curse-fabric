package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class TANFormTemperatureModifierPower extends Power {
    private static final int LEVEL_COUNT = 5;

    private final int[] offsets = new int[LEVEL_COUNT];

    public TANFormTemperatureModifierPower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        this.offsets[0] = data.getInt("icy_offset");
        this.offsets[1] = data.getInt("cold_offset");
        this.offsets[2] = data.getInt("neutral_offset");
        this.offsets[3] = data.getInt("warm_offset");
        this.offsets[4] = data.getInt("hot_offset");
    }

    public int modifyTemperatureOrdinal(int currentOrdinal) {
        int clampedOrdinal = Math.max(0, Math.min(LEVEL_COUNT - 1, currentOrdinal));
        return Math.max(0, Math.min(LEVEL_COUNT - 1, clampedOrdinal + this.offsets[clampedOrdinal]));
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("tan_form_temperature_modifier"),
                new SerializableData()
                        .add("icy_offset", SerializableDataTypes.INT, 0)
                        .add("cold_offset", SerializableDataTypes.INT, 0)
                        .add("neutral_offset", SerializableDataTypes.INT, 0)
                        .add("warm_offset", SerializableDataTypes.INT, 0)
                        .add("hot_offset", SerializableDataTypes.INT, 0),
                data -> (powerType, entity) -> new TANFormTemperatureModifierPower(powerType, entity, data)
        ).allowCondition();
    }
}
