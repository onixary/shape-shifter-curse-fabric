package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.utils.InstinctUtils;


public class AddInstinctAction{

    public static void action(SerializableData.Instance data, Entity entity) {

        if (!(entity instanceof ServerPlayerEntity playerEntity)) {
            return;
        }

        if (!data.isPresent("instinct_effect_id")) {
            ShapeShifterCurseFabric.LOGGER.error("Instinct effect ID is missing");
            return;
        }

        Identifier EffectID = data.getId("instinct_effect_id");
        float EffectValue = data.getFloat("value");
        int EffectDuration = data.getInt("duration");

        InstinctUtils.addInstinctEffect(playerEntity, EffectID, EffectValue, EffectDuration, EffectDuration <= 1);
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("add_instinct"),
                new SerializableData()
                        .add("instinct_effect_id", SerializableDataTypes.IDENTIFIER)
                        .add("value", SerializableDataTypes.FLOAT, 0.0f)
                        .add("duration", SerializableDataTypes.INT, 1),
                AddInstinctAction::action
        );
    }
}