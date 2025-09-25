package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class ScalePower extends Power {

    public ScalePower(PowerType<?> type, LivingEntity entity, float scale, float eye_scale) {
        super(type, entity);
        if(entity instanceof ServerPlayerEntity) {
            ScaleData scaleDataWidth = ScaleTypes.WIDTH.getScaleData(entity);
            ScaleData scaleDataHeight = ScaleTypes.HEIGHT.getScaleData(entity);
            scaleDataWidth.setScale(scale);
            scaleDataWidth.setPersistence(true);
            scaleDataHeight.setScale(scale);
            scaleDataHeight.setPersistence(true);
            ScaleData scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(entity);
            ScaleData scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(entity);
            scaleDataEyeHeight.setScale(eye_scale);
            scaleDataEyeHeight.setPersistence(true);
            scaleDataHitboxHeight.setScale(eye_scale);
            scaleDataHitboxHeight.setPersistence(true);
        }
    }

    public static PowerFactory getFactory() {
        return new PowerFactory<>(
            ShapeShifterCurseFabric.identifier("scale"),
            new SerializableData()
                .add("scale", SerializableDataTypes.FLOAT, 1.0f)
                .add("eye_scale", SerializableDataTypes.FLOAT, 1.0f),
            data -> (powerType, livingEntity) -> new ScalePower(
                powerType,
                livingEntity,
                data.getFloat("scale"),
                data.getFloat("eye_scale")
            )
        ).allowCondition();
    }

}