package net.onixary.shapeShifterCurseFabric.mixin.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.AdditionalPowers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(targets = "net.minecraft.entity.passive.OcelotEntity$FleeGoal")
public class OcelotEntityMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/FleeEntityGoal;<init>(Lnet/minecraft/entity/mob/PathAwareEntity;Ljava/lang/Class;FDDLjava/util/function/Predicate;)V"), index = 5)
    private static Predicate<LivingEntity> modifyCatFleeGoalPredicate(Predicate<LivingEntity> predicate) {
        return predicate.and(
                livingEntity -> {
                    if (livingEntity instanceof PlayerEntity player) {
                        return !AdditionalPowers.CAT_FRIENDLY.isActive(player);
                    }
                    return true;
                }
        );
    }
}
