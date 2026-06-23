package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class TANPreventDirtyWaterThirstEffectPower extends Power {
    public TANPreventDirtyWaterThirstEffectPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("tan_prevent_dirty_water_thirst_effect"),
                new SerializableData(),
                data -> TANPreventDirtyWaterThirstEffectPower::new
        ).allowCondition();
    }
}
