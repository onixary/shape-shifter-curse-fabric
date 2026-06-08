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
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;

import java.util.Objects;
import java.util.function.Predicate;

public class OnTransformForm extends AbstractCriterion<OnTransformForm.Condition> {
    /*
        "criteria": {
            "criteria_id": {
              "trigger": "shape-shifter-curse:on_transform_form",
              "conditions": {
                "form": [
                  "shape-shifter-curse:bat_3"
                ],
                "form_tier": [0]
              }
            }
        }
     */

    public static final Identifier ID = ShapeShifterCurseFabric.identifier("on_transform_form");

    public void trigger(ServerPlayerEntity player) {
        trigger(player, condition -> condition.CanTrigger(player));
    }

    public void trigger(ServerPlayerEntity player, IForm form) {
        trigger(player, condition -> condition.CanTrigger(form));
    }

    @Override
    public OnTransformForm.Condition conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        Predicate<IForm> formPredicate = (form) -> true;
        if (obj.has("form")) {
            JsonArray formIDArray = obj.getAsJsonArray("form");
            Identifier[] formID = new Identifier[formIDArray.size()];
            for (int i = 0; i < formIDArray.size(); i++) {
                formID[i] = Identifier.tryParse(formIDArray.get(i).getAsString());
            }
            if (formID.length > 0) {
                formPredicate = formPredicate.and(form -> {
                    for (Identifier id : formID) {
                        if (Objects.equals(id, form.getFormID())) {
                            return true;
                        }
                    }
                    return false;
                });
            }
        }
        if (obj.has("form_tier")) {
            JsonArray formTierArray = obj.getAsJsonArray("form_tier");
            int[] formTier = new int[formTierArray.size()];
            for (int i = 0; i < formTierArray.size(); i++) {
                formTier[i] = formTierArray.get(i).getAsInt();
            }
            if (formTier.length > 0) {
                formPredicate = formPredicate.and(form -> {
                    for (int tier : formTier) {
                        if (form.getFormTier() == tier) {
                            return true;
                        }
                    }
                    return false;
                });
            }
        }
        return new OnTransformForm.Condition(ID, playerPredicate, formPredicate);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static class Condition extends AbstractCriterionConditions {
        private final Predicate<IForm> formPredicate;

        public Condition(Identifier id, LootContextPredicate entity, Predicate<IForm> formPredicate) {
            super(id, entity);
            this.formPredicate = formPredicate;
        }


        public boolean CanTrigger(IForm form) {
            return formPredicate.test(form);
        }

        public boolean CanTrigger(PlayerEntity player) {
            IForm form = FormUtils.getPlayerForm(player);
            if (form == null) {
                return false;
            }
            return CanTrigger(form);
        }
    }
}
