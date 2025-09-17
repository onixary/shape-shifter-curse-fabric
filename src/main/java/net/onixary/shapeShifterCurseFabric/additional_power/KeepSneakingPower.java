package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class KeepSneakingPower extends Power {

    private final ConditionFactory<LivingEntity>.Instance condition;

    public KeepSneakingPower(PowerType<?> type, LivingEntity entity, ConditionFactory<LivingEntity>.Instance condition) {
        super(type, entity);
        this.condition = condition;
    }

    public boolean shouldForceSneak(PlayerEntity player) {
        // 在水中时不强制潜行
        if (player.isSubmergedInWater() || player.isInsideWaterOrBubbleColumn()) {
            return false;
        }

        return (condition == null || condition.test(entity));
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("keep_sneaking"),
                new SerializableData()
                        .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data -> (powerType, livingEntity) -> new KeepSneakingPower(
                        powerType,
                        livingEntity,
                        data.get("condition")
                )
        ).allowCondition();
    }
}
