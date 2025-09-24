package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class LootingPower extends Power {

    private final int level;

    public LootingPower(PowerType<?> type, LivingEntity entity, int level) {
        super(type, entity);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    // 工厂方法
    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("simple_looting"),
                new SerializableData()
                        .add("level", SerializableDataTypes.INT, 1),
                data -> (powerType, entity) -> new LootingPower(
                        powerType,
                        entity,
                        data.getInt("level")
                )
        ).allowCondition();
    }
}
