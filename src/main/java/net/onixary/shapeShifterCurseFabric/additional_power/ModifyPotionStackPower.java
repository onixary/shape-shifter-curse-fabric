package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;

public class ModifyPotionStackPower extends Power {

    private final int count;

    public ModifyPotionStackPower(PowerType<?> type, LivingEntity entity, int count)  {
        super(type, entity);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("modify_potion_stack"),
                new SerializableData().add("count", SerializableDataTypes.INT, 1),
                data -> (type, entity) -> new ModifyPotionStackPower(type, entity, data.getInt("count"))
        ).allowCondition();
    }
}