package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import net.onixary.shapeShifterCurseFabric.util.ClientUtils;

public class AdditionalEntityConditions {
    public static void register() {
        register(DiggingBareHandCondition.getFactory());
        register(ChanceCondition.getFactory());
        register(JumpEventCondition.getFactory());
        register(MustCrawlingCondition.getFactory());
        TrinketsCondition.registerCondition(AdditionalEntityConditions::register);
        ManaUtilsApoli.registerCondition(AdditionalEntityConditions::register);
        register(new ConditionFactory<Entity>(
                ShapeShifterCurseFabric.identifier("can_render_gui"),
                new SerializableData(),
                (data, e) -> {
                    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                        return ClientUtils.CanDisplayGUI();
                    }
                    return true;
                }
        ));
    }

    private static void register(ConditionFactory<Entity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);

    }
}
