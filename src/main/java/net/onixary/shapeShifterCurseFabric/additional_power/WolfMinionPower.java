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
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import java.util.function.Consumer;

/** Owner-side bonuses, shared by passive and active wolf summons. */
public class WolfMinionPower extends Power {
    private final float deathHeal;
    private final Consumer<Entity> spawnAction;

    public WolfMinionPower(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
        super(type, entity);
        deathHeal = data.getFloat("death_heal");
        spawnAction = data.get("minion_action");
    }

    public static void onSpawn(LivingEntity owner, Entity minion) {
        for (var power : PowerHolderComponent.getPowers(owner, WolfMinionPower.class)) {
            if (power.spawnAction != null) power.spawnAction.accept(minion);
        }
    }

    public static void onDeath(LivingEntity owner) {
        if (owner == null || !owner.isAlive() || owner.getWorld().isClient) return;
        for (var power : PowerHolderComponent.getPowers(owner, WolfMinionPower.class)) owner.heal(power.deathHeal);
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(ShapeShifterCurseFabric.identifier("wolf_minion_bonus"),
                new SerializableData().add("death_heal", SerializableDataTypes.FLOAT, 0f)
                        .add("minion_action", ApoliDataTypes.ENTITY_ACTION, null),
                data -> (type, entity) -> new WolfMinionPower(type, entity, data)).allowCondition();
    }
}
