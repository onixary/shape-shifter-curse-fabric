package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.registry.Registry;

public class AdditionalPowers {
    public static void register() {
        register(AddSustainedInstinctPower.getFactory());
        register(AddImmediateInstinctPower.getFactory());
        register(CrawlingPower.getFactory());
        register(ScalePower.getFactory());
        register(LevitatePower.getFactory());
        register(AttractByEntityPower.getFactory());
        register(LootingPower.createFactory());
        register(ProjectileDodgePower.createFactory());
        register(WaterFlexibilityPower.createFactory());
        register(AlwaysSweepingPower.createFactory());
        register(FallingProtectionPower.createFactory());
        register(EnhancedFallingAttackPower.createFactory());
        register(TripleJumpPower.createFactory());
        register(PowderSnowWalkerPower.createFactory());
        register(FoxFriendlyPower.createFactory());
        register(BurnDamageModifierPower.createFactory());
        register(CriticalDamageModifierPower.createFactory());
        register(SnowballBlockTransformPower.createFactory());
        register(BatBlockAttachPower.createFactory());
        register(ActionOnJumpPower.createFactory());
        register(NoRenderArmPower.createFactory());
        register(CanEatAmethystShardPower.createFactory());
        register(NoStepSoundPower.createFactory());
        register(PillagerFriendlyPower.createFactory());
        register(PreventBerryEffectPower.createFactory());
        register(WitchFriendlyPower.createFactory());
        register(ScareVillagerPower.createFactory());
        register(ModifyPotionStackPower.createFactory());
    }

    public static PowerFactory<?> register(PowerFactory<?> powerFactory) {
        return Registry.register(ApoliRegistries.POWER_FACTORY, powerFactory.getSerializerId(), powerFactory);
    }
}
