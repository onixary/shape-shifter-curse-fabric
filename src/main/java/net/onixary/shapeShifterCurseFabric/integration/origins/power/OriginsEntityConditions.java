package net.onixary.shapeShifterCurseFabric.integration.origins.power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.onixary.shapeShifterCurseFabric.integration.origins.Origins;
import net.onixary.shapeShifterCurseFabric.integration.origins.component.OriginComponent;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayer;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayers;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class OriginsEntityConditions {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new ConditionFactory<>(Origins.identifier("origin"), new SerializableData()
            .add("origin", SerializableDataTypes.IDENTIFIER)
            .add("layer", SerializableDataTypes.IDENTIFIER, null),
            (data, entity) -> {
                if(entity instanceof PlayerEntity) {OriginComponent component = ModComponents.ORIGIN.get(entity);
                    Identifier originId = data.getId("origin");
                    if(data.isPresent("layer")) {
                        Identifier layerId = data.getId("layer");
                        OriginLayer layer = OriginLayers.getLayer(layerId);
                        if(layer == null) {
                            return false;
                        } else {
                            Origin origin = component.getOrigin(layer);
                            if(origin != null) {
                                return origin.getIdentifier().equals(originId);
                            }
                            return false;
                        }
                    } else {
                        return component.getOrigins().values().stream().anyMatch(o -> o.getIdentifier().equals(originId));
                    }
                } else {
                    return false;
                }
            }));
    }

    private static void register(ConditionFactory<Entity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
