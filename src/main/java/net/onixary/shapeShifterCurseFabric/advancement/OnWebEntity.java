package net.onixary.shapeShifterCurseFabric.advancement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.Objects;

public class OnWebEntity extends AbstractCriterion<OnWebEntity.Condition> {
    public static final Identifier ID = ShapeShifterCurseFabric.identifier("on_web_entity");

    public void trigger(ServerPlayerEntity player, Identifier entityID) {
        trigger(player, condition -> condition.CanTrigger(entityID));
    }

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        JsonArray entityIDArray = obj.getAsJsonArray("entity");
        Identifier[] entityID = new Identifier[entityIDArray.size()];
        for (int i = 0; i < entityIDArray.size(); i++) {
            entityID[i] = Identifier.tryParse(entityIDArray.get(i).getAsString());
        }
        return new OnWebEntity.Condition(ID, playerPredicate, entityID);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static class Condition extends AbstractCriterionConditions {
        public Identifier[] entityIDArray;

        public Condition(Identifier id, LootContextPredicate entity, Identifier... entityID) {
            super(id, entity);
            this.entityIDArray = entityID;
        }

        public boolean CanTrigger(Identifier ID) {
            for (Identifier c_ID : entityIDArray) {
                if (Objects.equals(c_ID, ID)) {
                    return true;
                }
            }
            return false;
        }
    }
}
