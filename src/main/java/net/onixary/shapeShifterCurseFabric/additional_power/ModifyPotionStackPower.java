package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;

public class ModifyPotionStackPower extends Power {

    public ModifyPotionStackPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("modify_potion_stack"),
                new SerializableData(),
                data -> (type, entity) -> new ModifyPotionStackPower(type, entity)
        ).allowCondition();
    }
}