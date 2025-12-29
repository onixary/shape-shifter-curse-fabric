package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;

public class AdditionalEntityActions {
    public static void register() {
        registerAction(AddInstinctAction.getFactory());
        registerAction(SetFallingDistanceAction.createFactory());
        TransformAction.registerAction(AdditionalEntityActions::registerAction, AdditionalEntityActions::registerBIAction);
        registerAction(ExplosionDamageEntityAction.createFactory());
        registerAction(SummonMinionWolfNearbyAction.createFactory());
        registerBIAction(SummonMinionWolfNearbyAction.createBIFactory());
        PlayPowerAnimationAction.register(AdditionalEntityActions::registerAction, AdditionalEntityActions::registerBIAction);
        ManaUtilsApoli.registerAction(AdditionalEntityActions::registerAction, AdditionalEntityActions::registerBIAction);
        FireArrowAction.registerAction(AdditionalEntityActions::registerAction, AdditionalEntityActions::registerBIAction);
    }

    public static ActionFactory<Entity> registerAction(ActionFactory<Entity> actionFactory) {
        return Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }

    public static ActionFactory<Pair<Entity, Entity>> registerBIAction(ActionFactory<Pair<Entity, Entity>> actionFactory) {
        return Registry.register(ApoliRegistries.BIENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
