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

public class ModifyFallDamagePower extends Power {

    private final List<Modifier> Modifiers_FallDistance = new LinkedList<>();
    private final List<Modifier> Modifiers_DamageMultiplier = new LinkedList<>();

    public ModifyFallDamagePower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public void addModifier_FallDistance(Modifier modifier) {
        this.Modifiers_FallDistance.add(modifier);
    }

    public List<Modifier> getModifiers_FallDistance() {
        return Modifiers_FallDistance;
    }

    public void addModifier_DamageMultiplier(Modifier modifier) {
        this.Modifiers_DamageMultiplier.add(modifier);
    }

    public List<Modifier> getModifiers_DamageMultiplier() {
        return Modifiers_DamageMultiplier;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(ShapeShifterCurseFabric.identifier("modfiy_fall_damage"),
                new SerializableData()
                        .add("modifier_fall_distance", Modifier.DATA_TYPE, null)
                        .add("modifiers_fall_distance", Modifier.LIST_TYPE, null)
                        .add("modifier_damage_multiplier", Modifier.DATA_TYPE, null)
                        .add("modifiers_damage_multiplier", Modifier.LIST_TYPE, null),
                data ->
                        (type, player) -> {
                            ModifyFallDamagePower power = new ModifyFallDamagePower(type, player);
                            data.ifPresent("modifier_fall_distance", power::addModifier_FallDistance);
                            data.<List<Modifier>>ifPresent("modifiers_fall_distance", mods -> mods.forEach(power::addModifier_FallDistance));
                            data.ifPresent("modifier_damage_multiplier", power::addModifier_DamageMultiplier);
                            data.<List<Modifier>>ifPresent("modifiers_damage_multiplier", mods -> mods.forEach(power::addModifier_DamageMultiplier));
                            return power;
                        })
                .allowCondition();
    }
}
