package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

public class AdditionalEntityConditions {
    public static void register() {
        register(DiggingBareHandCondition.getFactory());
        register(ChanceCondition.getFactory());
        register(JumpEventCondition.getFactory());
    }

    private static void register(ConditionFactory<Entity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);

    }
}
