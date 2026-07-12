package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

/**
 * 根据当前形态改变玩家视角晃动方式的客户端 Power。
 * 实际晃动逻辑由 CameraBobbingMixin 负责处理。
 *
 * 数据驱动配置示例:
 * {
 *   "type": "shape-shifter-curse:form_camera_bobbing",
 *   "bobbing_type": "snake"
 * }
 */
public class FormCameraBobbingPower extends Power {

    public final String bobbingType;

    public FormCameraBobbingPower(PowerType<?> type, LivingEntity entity, String bobbingType) {
        super(type, entity);
        this.bobbingType = bobbingType;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("form_camera_bobbing"),
                new SerializableData()
                        .add("bobbing_type", SerializableDataTypes.STRING),
                data -> (type, entity) -> new FormCameraBobbingPower(
                        type, entity, data.getString("bobbing_type")
                )
        ).allowCondition();
    }
}
