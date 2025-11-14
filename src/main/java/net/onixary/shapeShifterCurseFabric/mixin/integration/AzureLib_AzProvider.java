package net.onixary.shapeShifterCurseFabric.mixin.integration;

import mod.azure.azurelib.rewrite.animation.AzAnimator;
import mod.azure.azurelib.rewrite.render.AzProvider;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = AzProvider.class, remap = false)
public class AzureLib_AzProvider<T> {
    @Inject(method = "provideAnimator", at = @At("HEAD"))
    public void provideAnimator(T animatable, CallbackInfoReturnable<AzAnimator<T>> cir) {
        // 真正的Bug位置为 ItemStackMixin_AzItemAnimatorCache.getAnimatorOrNull 但是我不会给Mixin里面注入
        if (animatable instanceof ItemStack itemStack && !itemStack.getOrCreateNbt().contains("az_id")) {
            itemStack.getOrCreateNbt().putUuid("az_id", UUID.randomUUID());
        }
    }
}
