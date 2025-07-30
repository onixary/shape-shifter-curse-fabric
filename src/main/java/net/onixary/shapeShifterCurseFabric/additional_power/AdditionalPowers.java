package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.registry.Registry;

public class AdditionalPowers {
    public static void register() {
        register(AddSustainedInstinctPower.getFactory());
        register(AddImmediateInstinctPower.getFactory());
        register(AddSustainedInstinctInTimePower.getFactory());
        register(CrawlingPower.getFactory());
        register(ScalePower.getFactory());
        register(LevitatePower.getFactory());
        register(AttractByEntityPower.getFactory());
        register(LootingPower.createFactory());
        register(ProjectileDodgePower.createFactory());
        register(WaterFlexibilityPower.createFactory());
    }

    public static PowerFactory<?> register(PowerFactory<?> powerFactory) {
        return Registry.register(ApoliRegistries.POWER_FACTORY, powerFactory.getSerializerId(), powerFactory);
    }
}
