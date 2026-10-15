package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.magic.MagicGUIRender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void ssc$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (window == MinecraftClient.getInstance().getWindow().getHandle()) {
            double d = ((Boolean) this.client.options.getDiscreteMouseScroll().getValue() ? Math.signum(vertical) : vertical) * (Double) this.client.options.getMouseWheelSensitivity().getValue();
            if (this.client.getOverlay() == null && this.client.currentScreen == null && ShapeShifterCurseFabricClient.scrollActiveSkillKeybind.isPressed()) {
                MagicGUIRender.onMouseScroll(d);
                ci.cancel();
            }
        }
    }
}
