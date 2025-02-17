package net.onixary.shapeShifterCurseFabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import net.onixary.shapeShifterCurseFabric.screen_effect.TransformOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 0))
    private void shape_shifter_curse$renderOverlayAboveHud(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        TransformOverlay.INSTANCE.render();
    }

}
