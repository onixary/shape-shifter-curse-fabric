package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.BypassesLandingEffectsPower;
import net.onixary.shapeShifterCurseFabric.additional_power.BypassesSteppingEffectsPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "bypassesSteppingEffects", at = @At("RETURN"), cancellable = true)
    private void bypassesSteppingEffects(CallbackInfoReturnable<Boolean> cir) {
        // 只有玩家需要这个Power 所以为了减少性能开销 所以做了个判断
        if ((Object)this instanceof PlayerEntity player && !cir.getReturnValueZ()) {
            int powerCount = PowerHolderComponent.getPowers(player, BypassesSteppingEffectsPower.class).size();
            if (powerCount > 0) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "bypassesLandingEffects", at = @At("RETURN"), cancellable = true)
    private void bypassesLandingEffects(CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof PlayerEntity player && !cir.getReturnValueZ()) {
            int powerCount = PowerHolderComponent.getPowers(player, BypassesLandingEffectsPower.class).size();
            if (powerCount > 0) {
                cir.setReturnValue(true);
            }
        }
    }
}
