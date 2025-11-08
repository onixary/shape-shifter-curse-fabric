package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;

public class AdditionalEntityActions {
    public static void register() {
        register(AddInstinctAction.getFactory());
        register(SetFallingDistanceAction.createFactory());
        register(TransformAction.createFactory());
        register(ExplosionDamageEntityAction.createFactory());
    }

    public static ActionFactory<Entity> register(ActionFactory<Entity> actionFactory) {
        return Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
