package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Unique
    private static float prevBodyYaw;

    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIFFLnet/minecraft/entity/LivingEntity;)V", at = @At("HEAD"))
    private static void drawEntityHead(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        prevBodyYaw = entity.prevBodyYaw;
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        entity.prevBodyYaw = 180.0F + f * 20.0F;
        return;
    }

    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIFFLnet/minecraft/entity/LivingEntity;)V", at = @At("RETURN"))
    private static void drawEntityTail(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        entity.prevBodyYaw = prevBodyYaw;
        return;
    }
}
