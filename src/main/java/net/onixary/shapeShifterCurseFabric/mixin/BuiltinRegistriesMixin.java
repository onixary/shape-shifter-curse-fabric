package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuiltinRegistries.class)
public class BuiltinRegistriesMixin {
    @Inject(method = "createWrapperLookup", at = @At("HEAD"))
    private static void ModifyStructure(CallbackInfoReturnable<RegistryWrapper.WrapperLookup> cir) {

    }
}
