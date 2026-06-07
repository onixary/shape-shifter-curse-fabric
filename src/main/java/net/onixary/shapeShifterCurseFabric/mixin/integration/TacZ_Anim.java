package net.onixary.shapeShifterCurseFabric.mixin.integration;

import com.tacz.guns.api.event.common.GunDrawEvent;
import com.tacz.guns.api.event.common.GunMeleeEvent;
import com.tacz.guns.api.event.common.GunReloadEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.compat.playeranimator.animation.AnimationManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_form.old.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AnimationManager.class, remap = false)
public class TacZ_Anim {
    @Inject(method = "onFire", at = @At("HEAD"), cancellable = true)
    private void onFire(GunShootEvent event, CallbackInfo ci) {
        if (event.getShooter() instanceof AbstractClientPlayerEntity player) {
            PlayerFormBase form = FormTextureUtils.getPlayerForm_Render(player);
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "onReload", at = @At("HEAD"), cancellable = true)
    private void onReload(GunReloadEvent event, CallbackInfo ci) {
        if (event.getEntity() instanceof AbstractClientPlayerEntity player) {
            PlayerFormBase form = FormTextureUtils.getPlayerForm_Render(player);
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "onMelee", at = @At("HEAD"), cancellable = true)
    private void onMelee(GunMeleeEvent event, CallbackInfo ci) {
        if (event.getShooter() instanceof AbstractClientPlayerEntity player) {
            PlayerFormBase form = FormTextureUtils.getPlayerForm_Render(player);
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "onDraw", at = @At("HEAD"), cancellable = true)
    private void onDraw(GunDrawEvent event, CallbackInfo ci) {
        if (event.getEntity() instanceof AbstractClientPlayerEntity player) {
            PlayerFormBase form = FormTextureUtils.getPlayerForm_Render(player);
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
}