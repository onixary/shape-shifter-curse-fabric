package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.function.Consumer;
import java.util.function.Predicate;

/** Successful primary melee/critical/pounce hits, not a second collision detector. */
public class ActionOnCombatHitPower extends Power {
    private final String event;
    private final float minDamage;
    private final float bonusDamage;
    private final Predicate<Pair<Entity, Entity>> condition;
    private final Consumer<Pair<Entity, Entity>> action;

    public ActionOnCombatHitPower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        event = data.getString("event");
        minDamage = data.getFloat("min_damage");
        bonusDamage = data.getFloat("bonus_damage");
        condition = data.get("bientity_condition");
        action = data.get("bientity_action");
    }

    public static float health(Entity entity) {
        return entity instanceof LivingEntity living ? living.getHealth() + living.getAbsorptionAmount() : 0;
    }

    public static float meleeBonus(LivingEntity actor, Entity target) {
        float bonus = 0;
        Pair<Entity, Entity> pair = new Pair<>(actor, target);
        for (ActionOnCombatHitPower power : PowerHolderComponent.getPowers(actor, ActionOnCombatHitPower.class)) {
            if (power.event.equals("melee") && (power.condition == null || power.condition.test(pair))) bonus += power.bonusDamage;
        }
        return bonus;
    }

    public static void fire(LivingEntity actor, Entity target, String event, float damage) {
        if (actor.getWorld().isClient) return;
        Pair<Entity, Entity> pair = new Pair<>(actor, target);
        for (ActionOnCombatHitPower power : PowerHolderComponent.getPowers(actor, ActionOnCombatHitPower.class)) {
            if (power.event.equals(event) && damage > power.minDamage
                    && (power.condition == null || power.condition.test(pair))) power.action.accept(pair);
        }
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(ShapeShifterCurseFabric.identifier("action_on_combat_hit"),
                new SerializableData().add("event", SerializableDataTypes.STRING, "melee")
                        .add("min_damage", SerializableDataTypes.FLOAT, 0f)
                        .add("bonus_damage", SerializableDataTypes.FLOAT, 0f)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION),
                data -> (type, entity) -> new ActionOnCombatHitPower(type, entity, data)).allowCondition();
    }
}
