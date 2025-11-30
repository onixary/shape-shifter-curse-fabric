package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.ModifyFoodHealPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow
    private int foodTickTimer;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void update(PlayerEntity player, CallbackInfo ci) {
        PowerHolderComponent.getPowers(player, ModifyFoodHealPower.class).forEach(power -> {
            if (power.CanApply(player)) {
                this.foodTickTimer = power.ProcessFoodTick(this.foodTickTimer);
            }
        });
    }
}
