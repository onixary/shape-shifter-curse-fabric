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
    private final boolean adjustRunIndividually;
    private final float runSpeedMultiplier;

    public ModifyFootstepSoundSpeedPower(PowerType<?> type, LivingEntity entity, float speedMultiplier,
                                         boolean adjustRunIndividually, float runSpeedMultiplier) {
        super(type, entity);
        this.speedMultiplier = speedMultiplier;
        this.adjustRunIndividually = adjustRunIndividually;
        this.runSpeedMultiplier = runSpeedMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getSpeedMultiplierFor(LivingEntity entity) {
        return adjustRunIndividually && entity.isSprinting() ? runSpeedMultiplier : speedMultiplier;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("modify_footstep_sound_speed"),
                new SerializableData()
                        .add("speed_multiplier", SerializableDataTypes.FLOAT, 1.0f)
                        .add("adjust_run_individually", SerializableDataTypes.BOOLEAN, false)
                        .add("run_speed_multiplier", SerializableDataTypes.FLOAT, 1.0f),
                data -> (powerType, livingEntity) -> new ModifyFootstepSoundSpeedPower(
                        powerType,
                        livingEntity,
                        data.getFloat("speed_multiplier"),
                        data.getBoolean("adjust_run_individually"),
                        data.getFloat("run_speed_multiplier")
                )
        ).allowCondition();
    }
}
