package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class ModifyStepHeightPower extends Power {

    private final float stepHeightScale;
    private final ConditionFactory<LivingEntity>.Instance condition;

    public ModifyStepHeightPower(PowerType<?> type, LivingEntity entity, float stepHeightScale, ConditionFactory<LivingEntity>.Instance condition) {
        super(type, entity);
        this.stepHeightScale = stepHeightScale;
        this.condition = condition;
        this.setTicking(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (entity instanceof ServerPlayerEntity) {
            boolean isEffective = (condition == null || condition.test(entity));
            ScaleData scaleDataStepHeight = ScaleTypes.STEP_HEIGHT.getScaleData(entity);

            if (isEffective) {
                // 条件满足时应用缩放
                scaleDataStepHeight.setScale(stepHeightScale);
                scaleDataStepHeight.setPersistence(true);
            } else{
                // 条件不满足时恢复默认
                scaleDataStepHeight.setScale(1.0f);
                scaleDataStepHeight.setPersistence(true);
            }
        }
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("modify_step_height"),
                new SerializableData()
                        .add("step_height_scale", SerializableDataTypes.FLOAT)
                        .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data -> (powerType, livingEntity) -> new ModifyStepHeightPower(
                        powerType,
                        livingEntity,
                        data.getFloat("step_height_scale"),
                        data.get("condition")
                )
        ).allowCondition();
    }
}

