package net.onixary.shapeShifterCurseFabric.player_form_render;

import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public interface OriginAbilityEvent {
    public enum AbilityType {
        PRIMARY, SECONDARY
    }
    Event<OriginAbilityEvent> PRIMARY_ABILITY_EVENT = EventFactory.createArrayBacked(OriginAbilityEvent.class, originAbilityEvents -> (entity, origin) -> {
        for (OriginAbilityEvent listener : originAbilityEvents) {
            ActionResult result = listener.onAbilityUsed(entity,origin);
            if (result != ActionResult.PASS) {return result;}
        }
        return ActionResult.PASS;
    });
    Event<OriginAbilityEvent> SECONDARY_ABILITY_EVENT = EventFactory.createArrayBacked(OriginAbilityEvent.class, originAbilityEvents -> (entity, origin) -> {
        for (OriginAbilityEvent listener : originAbilityEvents) {
            ActionResult result = listener.onAbilityUsed(entity,origin);
            if (result != ActionResult.PASS) {return result;}
        }
        return ActionResult.PASS;
    });
    ActionResult onAbilityUsed(PlayerEntity entity, Origin origin);
}
