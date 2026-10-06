package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import java.util.function.Consumer;

/** Runs only from the accepted shield-block branch of damage(), never from a shield query. */
public class ActionOnShieldBlockPower extends Power {
    private final Consumer<Pair<Entity, Entity>> action;
    public ActionOnShieldBlockPower(PowerType<?> type, LivingEntity entity, Consumer<Pair<Entity, Entity>> action) {
        super(type, entity);
        this.action = action;
    }
    public static void onBlock(LivingEntity defender, DamageSource source) {
        if (defender.getWorld().isClient || !defender.isBlocking() || !source.isIn(DamageTypeTags.IS_PROJECTILE)
                || source.getAttacker() == null || source.getAttacker() == defender) return;
        for (var power : PowerHolderComponent.getPowers(defender, ActionOnShieldBlockPower.class)) {
            power.action.accept(new Pair<>(defender, source.getAttacker()));
        }
    }
    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(ShapeShifterCurseFabric.identifier("action_on_shield_block"),
                new SerializableData().add("bientity_action", ApoliDataTypes.BIENTITY_ACTION),
                data -> (type, entity) -> new ActionOnShieldBlockPower(type, entity, data.get("bientity_action"))).allowCondition();
    }
}
