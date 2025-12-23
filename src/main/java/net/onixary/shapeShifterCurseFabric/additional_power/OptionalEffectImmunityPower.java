package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.EffectImmunityPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class OptionalEffectImmunityPower {

    public static @Nullable StatusEffect getStatueEffect(Identifier effectID) {
        Optional<StatusEffect> result = Registries.STATUS_EFFECT.getOrEmpty(effectID);
        return result.orElse(null);
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                ShapeShifterCurseFabric.identifier("optional_effect_immunity"),
                new SerializableData()
                        .add("effect", SerializableDataTypes.IDENTIFIER, null)
                        .add("effects", SerializableDataTypes.IDENTIFIERS, null)
                        .add("inverted", SerializableDataTypes.BOOLEAN, false),
                (data) -> (type, player) -> {
                    EffectImmunityPower power = new EffectImmunityPower(type, player, data.get("inverted"));
                    if (data.isPresent("effect")) {
                        StatusEffect effect = getStatueEffect(data.get("effect"));
                        if (effect != null) {
                            power.addEffect(effect);
                        }
                    }
                    if (data.isPresent("effects")) {
                        List<Identifier> effectIDs = data.get("effects");
                        for (Identifier effectID : effectIDs) {
                            StatusEffect effect = getStatueEffect(effectID);
                            if (effect != null) {
                                power.addEffect(effect);
                            }
                        }
                    }
                    return power;
                }
        ).allowCondition();
    }
}
