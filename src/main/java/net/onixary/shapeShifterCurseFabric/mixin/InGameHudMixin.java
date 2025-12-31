package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.onixary.shapeShifterCurseFabric.mana.IManaRender;
import net.onixary.shapeShifterCurseFabric.mana.ManaComponent;
import net.onixary.shapeShifterCurseFabric.mana.ManaRegistriesClient;
import net.onixary.shapeShifterCurseFabric.player_form.instinct.InstinctBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private final InstinctBarRenderer instinctBarRenderer = new InstinctBarRenderer();

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderHud(DrawContext context, float tickDelta, CallbackInfo ci) {
        // 在InGameHud.render()方法返回前，调用你的渲染逻辑
        // 这确保了它在渲染完物品栏之后执行
        IManaRender manaRenderer = null;
        if (ManaRegistriesClient.hasManaRender(ManaComponent.LocalManaTypeID)) {
            manaRenderer = ManaRegistriesClient.getManaRender(ManaComponent.LocalManaTypeID);
        }
        if (manaRenderer == null || !manaRenderer.OverrideInstinctBar()) {
            instinctBarRenderer.render(context, tickDelta);
        }
        if (manaRenderer != null) {
            manaRenderer.render(context, tickDelta);
        }
    }
}
