package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class ModifyFootstepSoundSpeedPower extends Power {

    private final float speedMultiplier;

    public ModifyFootstepSoundSpeedPower(PowerType<?> type, LivingEntity entity, float speedMultiplier) {
        super(type, entity);
        this.speedMultiplier = speedMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("modify_footstep_sound_speed"),
                new SerializableData()
                        .add("speed_multiplier", SerializableDataTypes.FLOAT, 1.0f),
                data -> (powerType, livingEntity) -> new ModifyFootstepSoundSpeedPower(
                        powerType,
                        livingEntity,
                        data.getFloat("speed_multiplier")
                )
        ).allowCondition();
    }
}
