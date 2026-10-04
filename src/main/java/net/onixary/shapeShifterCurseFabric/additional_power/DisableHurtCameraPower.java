package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

/** A condition-aware marker consumed by the client camera renderer. */
public class DisableHurtCameraPower extends Power {
    public DisableHurtCameraPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("disable_hurt_camera"),
                new SerializableData(),
                data -> DisableHurtCameraPower::new
        ).allowCondition();
    }
}
