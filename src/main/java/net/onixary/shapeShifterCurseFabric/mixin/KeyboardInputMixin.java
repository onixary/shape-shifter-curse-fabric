package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.KeepSneakingPower;
import net.onixary.shapeShifterCurseFabric.client.ClientPlayerStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At("TAIL"))
    private void forceSneakInput(boolean slowDown, float f, CallbackInfo ci) {
        /*MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {
            PlayerEntity player = client.player;
            //ShapeShifterCurseFabric.LOGGER.info("Force Sneaking Init");

            // 检查是否有激活的强制潜行 Power
            PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
            KeepSneakingPower power = PowerHolderComponent.getPowers(player, KeepSneakingPower.class)
                    .stream()
                    .filter(KeepSneakingPower::isActive)
                    .findFirst()
                    .orElse(null);

                if (power != null && power.shouldForceSneak(player)) {
                    // 强制设置潜行输入为 true
                    //ShapeShifterCurseFabric.LOGGER.info("Force Sneaking Activated");
                    this.sneaking = true;
                }

        }*/

        if (ClientPlayerStateManager.shouldForceSneak) {
            this.sneaking = true;
        }
    }
}
