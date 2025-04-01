package net.onixary.shapeShifterCurseFabric.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

// 在玩家第一次在有模组的情况下加入游戏时触发，用于给与玩家书本
public class OnFirstJoinWithMod extends AbstractCriterion<OnFirstJoinWithMod.Condition> {
    public static final Identifier ID = new Identifier(ShapeShifterCurseFabric.MOD_ID, "on_first_join_with_mod");

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, condition -> {
            return true;
        });
    }

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Condition();
    }

    public static class Condition extends AbstractCriterionConditions {

        public Condition() {
            super(ID, LootContextPredicate.EMPTY);
        }
    }
}
