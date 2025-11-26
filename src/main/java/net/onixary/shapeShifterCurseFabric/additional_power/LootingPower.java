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
    private final int maxLevel;

    public LootingPower(PowerType<?> type, LivingEntity entity, int level, int maxLevel)  {
        super(type, entity);
        this.level = level;
        this.maxLevel = maxLevel;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getLevel(int preProcessLevel) {
        return Math.min(preProcessLevel + level, maxLevel);
    }

    // 工厂方法
    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("simple_looting"),
                new SerializableData()
                        .add("level", SerializableDataTypes.INT, 1)
                        .add("max_level", SerializableDataTypes.INT, Integer.MAX_VALUE),
                data -> (powerType, entity) -> new LootingPower(
                        powerType,
                        entity,
                        data.getInt("level"),
                        data.getInt("max_level")
                )
        ).allowCondition();
    }
}
