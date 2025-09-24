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

    public ScalePower(PowerType<?> type, LivingEntity entity, float scale, boolean isFeral) {
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
            if(isFeral) {
                scaleDataEyeHeight.setScale(0.6f);
                scaleDataEyeHeight.setPersistence(true);
                scaleDataHitboxHeight.setScale(0.6f);
                scaleDataHitboxHeight.setPersistence(true);
            }
            else{
                scaleDataEyeHeight.setScale(1);
                scaleDataEyeHeight.setPersistence(true);
                scaleDataHitboxHeight.setScale(1);
                scaleDataHitboxHeight.setPersistence(true);
            }
        }
    }

    public static PowerFactory getFactory() {
        return new PowerFactory<>(
            ShapeShifterCurseFabric.identifier("scale"),
            new SerializableData()
                .add("scale", SerializableDataTypes.FLOAT)
                    .add("is_feral", SerializableDataTypes.BOOLEAN, false),
            data -> (powerType, livingEntity) -> new ScalePower(
                powerType,
                livingEntity,
                data.getFloat("scale"),
                data.getBoolean("is_feral")
            )
        ).allowCondition();
    }

}