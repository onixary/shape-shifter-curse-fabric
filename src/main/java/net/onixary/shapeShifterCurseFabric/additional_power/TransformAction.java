package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.utils.TransformManager;
import net.onixary.shapeShifterCurseFabric.status_effects.CTPUtils;
import net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusPotionEffect;

import java.util.function.Consumer;

public class TransformAction {
    public static void TransformToFormAction(SerializableData.Instance data, Entity entity) {
        if (entity instanceof PlayerEntity pe) {
            Identifier formId = data.get("form_id");
            boolean instant = data.get("instant");
            if (formId == null) {
                ShapeShifterCurseFabric.LOGGER.warn("Missing form_id for TransformAction");
                return;
            }
            if (!RegPlayerForms.playerForms.containsKey(formId)) {
                ShapeShifterCurseFabric.LOGGER.warn("Invalid form_id for TransformAction: {}", formId);
                return;
            }
            IForm pfb = RegPlayerForms.getPlayerForm(formId);
            if (instant) {
                TransformManager.immediatelyTransform(pe, pfb);
            }
            else {
                TransformManager.startTransform(pe, pfb, null);
            }
        }
    }

    public static void GiveCustomTransformEffect(SerializableData.Instance data, Entity entity) {
        if (entity instanceof PlayerEntity player) {
            Identifier formId = data.get("form_id");
            if (formId != null) {
                CTPUtils.setTransformativePotionForm(player, formId);
                RegTStatusPotionEffect.TO_CUSTOM_STATUE_POTION.applyInstantEffect(player, player, player, 0, 1.0d);
            }
        }
    }

    public static void registerAction(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("transform_to_form"),
                new SerializableData()
                        .add("form_id", SerializableDataTypes.IDENTIFIER, null)
                        .add("instant", SerializableDataTypes.BOOLEAN, false),
                TransformAction::TransformToFormAction
        ));
        ActionRegister.accept(new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("give_custom_transform_effect"),
                new SerializableData()
                        .add("form_id", SerializableDataTypes.IDENTIFIER, null),
                TransformAction::GiveCustomTransformEffect
        ));
    }
}
