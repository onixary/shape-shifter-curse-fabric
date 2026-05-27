package net.onixary.shapeShifterCurseFabric.advancement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;

import java.util.Objects;

public class OnTransformForm extends AbstractCriterion<OnTransformForm.Condition> {
    public static final Identifier ID = ShapeShifterCurseFabric.identifier("on_transform_form");

    public void trigger(ServerPlayerEntity player) {
        trigger(player, condition -> condition.CanTrigger(player));
    }

    public void trigger(ServerPlayerEntity player, Identifier formID) {
        trigger(player, condition -> condition.CanTrigger(formID));
    }

    @Override
    public OnTransformForm.Condition conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        JsonArray formIDArray = obj.getAsJsonArray("form");
        Identifier[] formID = new Identifier[formIDArray.size()];
        for (int i = 0; i < formIDArray.size(); i++) {
            formID[i] = Identifier.tryParse(formIDArray.get(i).getAsString());
        }
        return new OnTransformForm.Condition(ID, playerPredicate, formID);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static class Condition extends AbstractCriterionConditions {
        public Identifier[] formIDArray;

        public Condition(Identifier id, LootContextPredicate entity, Identifier... formID) {
            super(id, entity);
            this.formIDArray = formID;
        }

        public boolean CanTrigger(Identifier formID) {
            for (Identifier c_formID : formIDArray) {
                if (Objects.equals(c_formID, formID)) {
                    return true;
                }
            }
            return false;
        }

        public boolean CanTrigger(PlayerEntity player) {
            PlayerFormBase form = FormAbilityManager.getForm(player);
            if (form == null) {
                return false;
            }
            return CanTrigger(form.FormID);
        }
    }
}
