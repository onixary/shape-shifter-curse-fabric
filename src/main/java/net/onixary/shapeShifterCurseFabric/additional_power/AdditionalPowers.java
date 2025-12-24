package net.onixary.shapeShifterCurseFabric.additional_power;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

public class AdditionalPowers {
    public static final PowerType<?> SCARE_SKELETON = new PowerTypeReference<>(ShapeShifterCurseFabric.identifier("scare_skeleton"));  // 这种应该会比较节省计算资源

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
        register(CustomEdiblePower.createFactory());
        register(NoStepSoundPower.createFactory());
        register(PillagerFriendlyPower.createFactory());
        register(PreventBerryEffectPower.createFactory());
        register(WitchFriendlyPower.createFactory());
        register(ScareVillagerPower.createFactory());
        register(ModifyPotionStackPower.createFactory());
        register(BreathingUnderWaterPower.createFactory());
        register(HoldBreathPower.createFactory());
        register(CustomWaterBreathingPower.createFactory());
        register(ConditionedModifySlipperinessPower.createFactory());
        register(ActionOnSprintingToSneakingPower.createFactory());
        register(ModifyStepHeightPower.createFactory());
        register(KeepSneakingPower.createFactory());
        register(DelayAttributePower.createFactory());
        register(AlwaysSprintSwimmingPower.createFactory());
        register(ActionOnSplashPotionTakeEffect.createFactory());
        register(ConditionScalePower.createFactory());
        register(SneakingJumpClashPower.createFactory());
        register(InWaterSpeedModifierPower.createFactory());
        register(VirtualTotemPower.createFactory());
        register(ModifyInstantHealthPower.createFactory());
        register(ModifyInstantDamagePower.createFactory());
        register(SoulSpeedPower.createFactory());
        register(TWolfFriendlyPower.createFactory());
        register(ModifyFoodHealPower.createFactory());
        register(ModifyEntityLootPower.createFactory());
        register(ModifyBlockDropPower.createFactory());
        register(ActionOnEntityInRangePower.createFactory());
        register(ApplyEffectPower.createFactory());
        register(OptionalEffectImmunityPower.createFactory());
        register(ManaTypePower.createFactory());
        register(ManaAttributePower.createFactory());
        register(ConditionedManaAttributePower.createFactory());
    }

    public static PowerFactory<?> register(PowerFactory<?> powerFactory) {
        return Registry.register(ApoliRegistries.POWER_FACTORY, powerFactory.getSerializerId(), powerFactory);
    }
}
