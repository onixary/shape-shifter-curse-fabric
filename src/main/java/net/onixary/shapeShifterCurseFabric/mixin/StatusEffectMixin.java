// StatusEffectMixin.java
package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class StatusEffectMixin {

    @Inject(method = "onStatusEffectRemoved", at = @At("HEAD"))
    private void onStatusEffectRemoved(StatusEffectInstance effect, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            // 检查移除的是否是变形效果
            if (effect.getEffectType() instanceof BaseTransformativeStatusEffect) {
                player.getServer().execute(() -> {
                    // 延迟检查，确保效果确实被移除
                    boolean stillHasTransformEffect = player.getStatusEffects().stream()
                            .anyMatch(e -> e.getEffectType() instanceof BaseTransformativeStatusEffect);

                    if (!stillHasTransformEffect) {
                        EffectManager.safeResetAttachment(player);
                    }
                });
            }
        }
    }
}