package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctEffectType;

import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.applyImmediateEffect;
import static net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctManager.applySustainedEffect;

public class AddInstinctAction{

    public static void action(SerializableData.Instance data, Entity entity) {

        if (!(entity instanceof ServerPlayerEntity playerEntity)) {
            return;
        }

        InstinctEffectType effectType = null;
        try {
            effectType = InstinctEffectType.valueOf(data.getString("instinct_effect_type"));
        } catch (IllegalArgumentException e) {
            // Handle the error, for example, log it or set a default value
            ShapeShifterCurseFabric.LOGGER.error("Invalid instinct effect type: " + data.getString("instinct_effect_type") + ", it should be matching the enum InstinctEffectType");
        }

        if(effectType != null && !effectType.isSustained()) {
            ShapeShifterCurseFabric.LOGGER.info("Add immediate instinct action: " + effectType);
            applyImmediateEffect((ServerPlayerEntity)entity, effectType);
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(
                Apoli.identifier("add_instinct"),
                new SerializableData()
                        .add("instinct_effect_type", SerializableDataTypes.STRING),
                AddInstinctAction::action
        );
    }
}