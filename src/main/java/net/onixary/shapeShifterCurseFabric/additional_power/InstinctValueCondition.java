package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.RegPlayerInstinctComponent;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;

public class InstinctValueCondition {

    public static boolean condition(SerializableData.Instance data, Entity entity) {

        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }

        IForm currentForm = FormUtils.getPlayerForm(player);

        if (FormUtils.NoInstinct.hasFlag(currentForm) || FormUtils.LockInstinct.hasFlag(currentForm)) {
            return false;
        }

        Comparison comparison = (Comparison) data.get("comparison");
        if (comparison == null) {
            return false;
        }

        float instinctValue = RegPlayerInstinctComponent.PLAYER_INSTINCT_COMP.get(player).instinctValue;
        float compareTo = data.getFloat("compare_to");

        return comparison.compare(instinctValue, compareTo);
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(
                ShapeShifterCurseFabric.identifier("instinct_value"),
                new SerializableData()
                        .add("comparison", ApoliDataTypes.COMPARISON)
                        .add("compare_to", SerializableDataTypes.FLOAT, 0.0f),
                InstinctValueCondition::condition
        );
    }
}
