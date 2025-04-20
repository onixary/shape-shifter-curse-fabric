package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.Prioritized;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PosePower extends Power implements Prioritized<PosePower> {

    public static final SerializableDataType<EntityPose> ENTITY_POSE = SerializableDataType.enumValue(EntityPose.class);
    private final EntityPose pose;
    private final int priority;

    public PosePower(PowerType<?> type, LivingEntity entity, EntityPose pose, int priority) {
        super(type, entity);
        this.pose = pose;
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public EntityPose getPose() {
        return pose;
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(
                Apoli.identifier("pose"),
                new SerializableData()
                        .add("pose", ENTITY_POSE)
                        .add("priority", SerializableDataTypes.INT, 0),
                data -> (powerType, entity) -> new PosePower(
                        powerType,
                        entity,
                        data.get("pose"),
                        data.get("priority")
                )
        ).allowCondition();
    }

}
