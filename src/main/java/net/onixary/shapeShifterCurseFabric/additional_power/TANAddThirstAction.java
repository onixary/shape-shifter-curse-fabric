package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.integration.toughasnails.ToughAsNailsPowerUtils;
import net.onixary.shapeShifterCurseFabric.integration.toughasnails.ToughAsNailsThirstIntegration;

public class TANAddThirstAction {
    public static ActionFactory<Entity> createFactory() {
        return new ActionFactory<>(
                ShapeShifterCurseFabric.identifier("tan_add_thirst"),
                new SerializableData()
                        .add("amount", SerializableDataTypes.INT, 0),
                (data, entity) -> {
                    if (!(entity instanceof PlayerEntity player) || player.getWorld().isClient()) {
                        return;
                    }
                    if (!ToughAsNailsPowerUtils.isToughAsNailsLoaded()) {
                        return;
                    }
                    ToughAsNailsThirstIntegration.addThirst(player, data.getInt("amount"));
                }
        );
    }
}
