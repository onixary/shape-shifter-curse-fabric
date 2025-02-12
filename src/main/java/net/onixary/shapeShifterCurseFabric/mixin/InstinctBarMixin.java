package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InstinctBarMixin {
    private static final InstinctBarRenderer instinctBar = new InstinctBarRenderer();

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void renderBars(DrawContext context, float tickDelta, CallbackInfo ci) {
        instinctBar.render(context, tickDelta);
    }

}

