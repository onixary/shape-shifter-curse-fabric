package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;

public class HoldBreathPower extends Power {

    public HoldBreathPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(
                Apoli.identifier("hold_breath"),
                new SerializableData(),
                data -> (powerType, livingEntity) -> new CrawlingPower(
                        powerType,
                        livingEntity
                )
        ).allowCondition();
    }
}
