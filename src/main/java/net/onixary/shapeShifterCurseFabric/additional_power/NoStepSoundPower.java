package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;

public class NoStepSoundPower extends Power {

    public NoStepSoundPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("no_step_sound"),
                new SerializableData(),
                data -> (type, entity) -> new FoxFriendlyPower(type, entity)
        ).allowCondition();
    }
}
