package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.applyEffect;

public class AddInstinctAction{

    public static void action(SerializableData.Instance data, Entity entity) {

        if (!(entity instanceof ServerPlayerEntity playerEntity)) {
            return;
        }

        if (!data.isPresent("instinct_effect_id")) {
            ShapeShifterCurseFabric.LOGGER.error("Instinct effect ID is missing");
            return;
        }

        String EffectID = data.getString("instinct_effect_id");
        float EffectValue = data.getFloat("value");
        int EffectDuration = data.getInt("duration");

        applyEffect(playerEntity, EffectID, EffectValue, EffectDuration);

//        try {
//            effectType = InstinctEffectType.valueOf(data.getString("instinct_effect_type"));
//        } catch (IllegalArgumentException e) {
//            // Handle the error, for example, log it or set a default value
//            ShapeShifterCurseFabric.LOGGER.error("Invalid instinct effect type: " + data.getString("instinct_effect_type") + ", it should be matching the enum InstinctEffectType");
//        }
//
//        if(effectType != null && !effectType.isSustained()) {
//            ShapeShifterCurseFabric.LOGGER.info("Add immediate instinct action: " + effectType);
//            applyImmediateEffect((ServerPlayerEntity)entity, effectType);
//        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("add_instinct"),
                new SerializableData()
                        .add("instinct_effect_id", SerializableDataTypes.STRING)
                        .add("value", SerializableDataTypes.FLOAT, 0.0f)
                        .add("duration", SerializableDataTypes.INT, 1),
                AddInstinctAction::action
        );
    }
}