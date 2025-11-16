package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerTickMixin {

    @Unique
    private static final int CHECK_INTERVAL = 20; // 每20tick检查一次
    @Unique
    private int tickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            tickCounter++;
            if (tickCounter >= CHECK_INTERVAL) {
                tickCounter = 0;
                EffectManager.verifyAndCleanAttachment(player);
            }
        }
    }
}