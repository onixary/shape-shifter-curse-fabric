package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.utils.InstinctUtils;

public class AddImmediateInstinctPower extends Power {

    private final Identifier instinctEffectID;
    private final float value;

    public AddImmediateInstinctPower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        this.value = data.getFloat("value");

        if (!data.isPresent("instinct_effect_id")) {
            ShapeShifterCurseFabric.LOGGER.error("Instinct effect ID is missing");
            this.instinctEffectID = null;
            return;
        }

        this.instinctEffectID = data.getId("instinct_effect_id");;

        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        InstinctUtils.addInstinctEffect(player, this.instinctEffectID, this.value, 1, true);
    }

    public static PowerFactory getFactory() {
        return new PowerFactory<>(
            ShapeShifterCurseFabric.identifier("add_immediate_instinct"),
            new SerializableData()
                .add("instinct_effect_id", SerializableDataTypes.IDENTIFIER)
                .add("value", SerializableDataTypes.FLOAT, 0.0f),
            data -> (powerType, livingEntity) -> new AddImmediateInstinctPower(
                powerType,
                livingEntity,
                data
            )
        ).allowCondition();
    }

}