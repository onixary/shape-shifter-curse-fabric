package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ModifyInstantDamagePower extends Power {
    private final float MulScale;

    public ModifyInstantDamagePower(PowerType<?> type, LivingEntity entity, float MulScale) {
        super(type, entity);
        this.MulScale = MulScale;
    }

    public float ApplyMulScale(float orig_value) {
        return orig_value * MulScale;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("modify_instant_damage_scale"),
                new SerializableData()
                        .add("scale", SerializableDataTypes.FLOAT, 1.0f),
                data -> (powerType, entity) -> new ModifyInstantDamagePower(powerType, entity, data.get("scale"))
        ).allowCondition();
    }
}
