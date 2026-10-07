package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.additional_power.FrostDivePower;
import net.onixary.shapeShifterCurseFabric.status_effects.RegOtherStatusEffects;
import net.onixary.shapeShifterCurseFabric.status_effects.other_effects.FrostClawEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public class FrostMovementMixin {
    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
    private Vec3d frostMovement(Vec3d movement) {
        Entity self = (Entity) (Object) this;
        movement = FrostDivePower.constrainMovement(self, movement);
        if (self instanceof LivingEntity living && living.hasStatusEffect(RegOtherStatusEffects.FROST_CLAW)) {
            FrostClawEffect.beforeMove(living, movement);
        }
        return movement;
    }
}
