package net.onixary.shapeShifterCurseFabric.mixin.integration;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.ModifyFootstepSoundSpeedPower;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

// Presence Footsteps 自带步频判定(TerrestrialStepSoundGenerator.simulateFootsteps)，
// 不经过原版 calculateNextStepSoundDistance，因此需要单独按 power 倍率缩放其步距阈值
@Mixin(targets = "eu.ha3.presencefootsteps.sound.generator.TerrestrialStepSoundGenerator", remap = false)
public abstract class PresenceFootstepsStepSpeedMixin {
    @Final
    @Shadow(remap = false)
    protected LivingEntity entity;

    @ModifyExpressionValue(
        method = "simulateFootsteps",
        at = @At(value = "INVOKE",
            target = "Leu/ha3/presencefootsteps/sound/generator/Modifier;reevaluateDistance(Leu/ha3/presencefootsteps/sound/State;F)F",
            remap = false),
        remap = false)
    private float shapeShifter$modifyStepDistance(float original) {
        if (entity instanceof PlayerEntity player) {
            var powers = PowerHolderComponent.getPowers(player, ModifyFootstepSoundSpeedPower.class);
            if (!powers.isEmpty()) {
                return original / powers.get(0).getSpeedMultiplier();
            }
        }
        return original;
    }
}
