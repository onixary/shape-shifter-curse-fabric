package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.integration.tan.TANHelper;

public class TANTemperatureModifierPower extends Power {
    public int hotResistance = 0;
    public int coldResistance = 0;
    public int temperatureResistance = 0;
    public int temperatureOffset = 0;

    public TANTemperatureModifierPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    @Override
    public void onGained() {
        if (this.entity instanceof PlayerEntity player) {
            TANHelper.markDataDirty(player);
        }
        super.onGained();
    }

    @Override
    public void onLost() {
        if (this.entity instanceof PlayerEntity player) {
            TANHelper.markDataDirty(player);
        }
        super.onLost();
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("tan_temperature_modifier"),
                new SerializableData()
                        .add("hot_resistance", SerializableDataTypes.INT, 0)
                        .add("cold_resistance", SerializableDataTypes.INT, 0)
                        .add("temperature_resistance", SerializableDataTypes.INT, 0)
                        .add("temperature_offset", SerializableDataTypes.INT, 0),
                data -> (powerType, entity) -> {
                    TANTemperatureModifierPower power = new TANTemperatureModifierPower(powerType, entity);
                    power.hotResistance = data.getInt("hot_resistance");
                    power.coldResistance = data.getInt("cold_resistance");
                    power.temperatureResistance = data.getInt("temperature_resistance");
                    power.temperatureOffset = data.getInt("temperature_offset");
                    return power;
                }
        );
    }
}
