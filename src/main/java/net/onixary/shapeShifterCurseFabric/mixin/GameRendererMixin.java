package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.onixary.shapeShifterCurseFabric.additional_power.DisableHurtCameraPower;
import net.onixary.shapeShifterCurseFabric.screen_effect.TransformOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    // This point is after vanilla's death tilt and before the hurt camera rotations.
    @Inject(method = "tiltViewWhenHurt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;getDamageTiltYaw()F"), cancellable = true)
    private void shape_shifter_curse$disableHurtCamera(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(MinecraftClient.getInstance().getCameraEntity(), DisableHurtCameraPower.class)) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 0))
    private void shape_shifter_curse$renderOverlayAboveHud(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        TransformOverlay.INSTANCE.render();
    }
}
