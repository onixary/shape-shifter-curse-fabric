package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.LinkedList;
import java.util.List;

public class TANModifyThirstExhaustionPower extends Power {
    private final List<Modifier> modifiers = new LinkedList<>();

    public TANModifyThirstExhaustionPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public void addModifier(Modifier modifier) {
        this.modifiers.add(modifier);
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("tan_modify_thirst_exhaustion"),
                new SerializableData()
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null),
                data -> (powerType, entity) -> {
                    TANModifyThirstExhaustionPower power = new TANModifyThirstExhaustionPower(powerType, entity);
                    data.ifPresent("modifier", power::addModifier);
                    data.<List<Modifier>>ifPresent("modifiers", modifiers -> modifiers.forEach(power::addModifier));
                    return power;
                }
        ).allowCondition();
    }
}
