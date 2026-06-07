package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.old.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.old.PlayerFormPhase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.RegPlayerInstinctComponent;

public class InstinctValueCondition {

    public static boolean condition(SerializableData.Instance data, Entity entity) {

        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }

        PlayerFormBase currentForm = FormAbilityManager.getForm(player);

        // ORIGINAL_BEFORE_ENABLE 和 ORIGINAL_SHIFTER 形态始终返回 false
        if (currentForm == RegPlayerForms.ORIGINAL_BEFORE_ENABLE || currentForm == RegPlayerForms.ORIGINAL_SHIFTER) {
            return false;
        }

        // PHASE_2, PHASE_3, PHASE_SP 形态始终返回 false
        PlayerFormPhase phase = currentForm.getPhase();
        if (phase == PlayerFormPhase.PHASE_2 || phase == PlayerFormPhase.PHASE_3 || phase == PlayerFormPhase.PHASE_SP) {
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
