package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;

public class AlwaysSprintSwimmingPower extends Power {

    private final float hungerMultiplier;

    public AlwaysSprintSwimmingPower(PowerType<?> type, LivingEntity entity, float hungerMultiplier) {
        super(type, entity);
        this.hungerMultiplier = hungerMultiplier;
    }



    public float getHungerMultiplier() {
        return hungerMultiplier;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("always_sprint_swimming"),
                new SerializableData()
                        .add("hunger_multiplier", SerializableDataTypes.FLOAT, 1.0f),
                data -> (type, entity) -> new AlwaysSprintSwimmingPower(
                        type,
                        entity,
                        data.get("hunger_multiplier")
                )
        ).allowCondition();
    }
}

