package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.BurnPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctEffectType;

import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.applySustainedEffect;

public class AddSustainedInstinctPower extends Power {

    private final InstinctEffectType instinctEffectType;

    public AddSustainedInstinctPower(PowerType<?> type, LivingEntity entity, String instinctEffectType) {
        super(type, entity);
        InstinctEffectType effectType = null;
        try {
            effectType = InstinctEffectType.valueOf(instinctEffectType);
        } catch (IllegalArgumentException e) {
            // Handle the error, for example, log it or set a default value
            ShapeShifterCurseFabric.LOGGER.error("Invalid instinct effect type: " + instinctEffectType + ", it should be matching the enum InstinctEffectType");
        }
        this.instinctEffectType = effectType;
        this.setTicking();
    }

    public void tick() {
        if(entity instanceof ServerPlayerEntity && instinctEffectType != null && instinctEffectType.isSustained()) {
            //ShapeShifterCurseFabric.LOGGER.info("Applying sustained effect bt power: " + instinctEffectType);
            applySustainedEffect((ServerPlayerEntity)entity, instinctEffectType);
        }
    }

    public static PowerFactory getFactory() {
        return new PowerFactory<>(
            Apoli.identifier("add_sustained_instinct"),
            new SerializableData()
                .add("instinct_effect_type", SerializableDataTypes.STRING),
            data -> (powerType, livingEntity) -> new AddSustainedInstinctPower(
                powerType,
                livingEntity,
                data.getString("instinct_effect_type")
            )
        ).allowCondition();
    }

}