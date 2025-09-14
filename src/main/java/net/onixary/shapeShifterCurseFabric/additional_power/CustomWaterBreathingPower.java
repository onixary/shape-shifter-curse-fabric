package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;

public class CustomWaterBreathingPower extends Power {
    // 使用原版水下呼吸逻辑的反向来实现陆地上慢速失去氧气
    // 水下呼吸等级越大，陆地上失去氧气的速度越慢
    // 24级为体验相对较好的数值
    private final int landWaterBreathLevel;

    public CustomWaterBreathingPower(PowerType<?> type, LivingEntity entity, int landWaterBreathLevel) {
        super(type, entity);
        this.landWaterBreathLevel = landWaterBreathLevel;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                Apoli.identifier("custom_water_breathing"),
                new SerializableData()
                        .add("land_water_breathing_level", SerializableDataTypes.INT, 24),
                data -> (powerType, entity) -> new CustomWaterBreathingPower(
                        powerType,
                        entity,
                        data.getInt("land_water_breathing_level")
                )
        ).allowCondition();
    }

    public int getLandWaterBreathLevel() {
        return landWaterBreathLevel;
    }
}
