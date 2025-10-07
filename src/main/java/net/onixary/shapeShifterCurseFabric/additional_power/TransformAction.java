package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;

public class TransformAction {
    public static void action(SerializableData.Instance data, Entity entity) {
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
            PlayerFormBase pfb = RegPlayerForms.getPlayerForm(formId);
            if (instant) {
                TransformManager.setFormDirectly(pe, pfb);
            }
            else {
                TransformManager.handleDirectTransform(pe, pfb, false);
            }
        }
    }

    public static ActionFactory<Entity> createFactory() {
        return new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("transform_to_form"),
                new SerializableData()
                        .add("form_id", SerializableDataTypes.IDENTIFIER)
                        .add("instant", SerializableDataTypes.BOOLEAN, false),
                TransformAction::action
        );
    }
}
