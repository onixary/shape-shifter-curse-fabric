package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctEffectType;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.applySustainedEffect;

public class ScalePower extends Power {

    public ScalePower(PowerType<?> type, LivingEntity entity, float scale) {
        super(type, entity);
        if(entity instanceof ServerPlayerEntity) {
            ScaleData scaleDataWidth = ScaleTypes.WIDTH.getScaleData(entity);
            ScaleData scaleDataHeight = ScaleTypes.HEIGHT.getScaleData(entity);
            scaleDataWidth.setScale(scale);
            scaleDataWidth.setPersistence(true);
            scaleDataHeight.setScale(scale);
            scaleDataHeight.setPersistence(true);
        }
    }

    public static PowerFactory getFactory() {
        return new PowerFactory<>(
            Apoli.identifier("scale"),
            new SerializableData()
                .add("scale", SerializableDataTypes.FLOAT),
            data -> (powerType, livingEntity) -> new ScalePower(
                powerType,
                livingEntity,
                data.getFloat("scale")
            )
        ).allowCondition();
    }

}