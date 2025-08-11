package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class SetFallingDistanceAction {
    public static ActionFactory<Entity> createFactory() {
        return new ActionFactory<>(
                Apoli.identifier("set_falling_distance"),
                new SerializableData()
                        .add("distance", SerializableDataTypes.FLOAT, 0.0f), // 定义一个名为 "distance" 的浮点数参数，默认值为 0.0
                (data, entity) -> {
                    // 将实体的 fallDistance 字段设置为从数据中获取的值
                    entity.fallDistance = data.getFloat("distance");
                    ShapeShifterCurseFabric.LOGGER.info("Set falling distance for entity {} to {}", entity.getName().getString(), data.getFloat("distance"));
                }
        );
    }
}
