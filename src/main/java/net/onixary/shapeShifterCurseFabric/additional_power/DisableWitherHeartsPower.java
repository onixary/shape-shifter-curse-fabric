package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

/** Suppresses the withered heart texture on the client while active. */
public class DisableWitherHeartsPower extends Power {
    public DisableWitherHeartsPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("disable_wither_hearts"),
                new SerializableData(),
                data -> DisableWitherHeartsPower::new
        ).allowCondition();
    }
}
