package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ModifyPotionStackPower extends Power {

    private final int count;
    private final boolean onlyWaterPotion;

    public ModifyPotionStackPower(PowerType<?> type, LivingEntity entity, int count, boolean onlyWaterPotion) {
        super(type, entity);
        this.count = count;
        this.onlyWaterPotion = onlyWaterPotion;
    }

    public boolean isOnlyWaterPotion() {
        return onlyWaterPotion;
    }

    public int getCount() {
        return count;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("modify_potion_stack"),
                new SerializableData()
                        .add("count", SerializableDataTypes.INT, 1)
                        .add("only_water_potion", SerializableDataTypes.BOOLEAN, false),
                data -> (type, entity) -> new ModifyPotionStackPower(type, entity, data.getInt("count"), data.getBoolean("only_water_potion"))
        ).allowCondition();
    }
}