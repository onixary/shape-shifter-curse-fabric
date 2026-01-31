package net.onixary.shapeShifterCurseFabric.mixin.integration;

import com.tacz.guns.api.event.common.GunDrawEvent;
import com.tacz.guns.api.event.common.GunMeleeEvent;
import com.tacz.guns.api.event.common.GunReloadEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat;
import com.tacz.guns.compat.playeranimator.animation.AnimationManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.PlayerManager;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AnimationManager.class, remap = false)
public class TacZ_Anim {
    @Inject(method = "onFire", at = @At("HEAD"), cancellable = true)
    private static void onFire(GunShootEvent event, CallbackInfo ci) {
        if (event.getShooter() instanceof AbstractClientPlayerEntity player) {
            PlayerFormBase form = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "onReload", at = @At("HEAD"), cancellable = true)
    private static void onFire(GunReloadEvent event, CallbackInfo ci) {
        if (event.getEntity() instanceof AbstractClientPlayerEntity player) {
            PlayerFormBase form = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "onMelee", at = @At("HEAD"), cancellable = true)
    private static void onMelee(GunMeleeEvent event, CallbackInfo ci) {
        if (event.getShooter() instanceof AbstractClientPlayerEntity player) {
            PlayerFormBase form = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "onDraw", at = @At("HEAD"), cancellable = true)
    private static void onDraw(GunDrawEvent event, CallbackInfo ci) {
        if (event.getEntity() instanceof AbstractClientPlayerEntity player) {
            PlayerFormBase form = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
            if (form.getBodyType() == PlayerFormBodyType.FERAL) {
                ci.cancel();
            }
        }
    }
}