package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class WitherIntervalPower extends Power {
    private final double multiplier;
    public WitherIntervalPower(PowerType<?> type, LivingEntity entity, double multiplier) {
        super(type, entity);
        this.multiplier = multiplier;
    }

    public static boolean shouldApply(LivingEntity entity, int duration, int amplifier, boolean vanilla) {
        var powers = PowerHolderComponent.getPowers(entity, WitherIntervalPower.class);
        if (powers.isEmpty()) return vanilla;
        double multiplier = powers.stream().mapToDouble(p -> p.multiplier).max().orElse(1);
        int interval = Math.max(1, (int) Math.floor(Math.scalb(40.0 * multiplier, -Math.max(0, amplifier))));
        return duration % interval == 0;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(ShapeShifterCurseFabric.identifier("wither_interval"),
                new SerializableData().add("multiplier", SerializableDataTypes.DOUBLE, 1.0),
                data -> (type, entity) -> new WitherIntervalPower(type, entity, data.getDouble("multiplier"))).allowCondition();
    }
}
