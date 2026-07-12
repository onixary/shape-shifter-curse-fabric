package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.onixary.shapeShifterCurseFabric.util.EntityAttributeRegister;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(DefaultAttributeRegistry.class)
public class DefaultAttributeRegistryMixin {
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private static void get(EntityType<? extends LivingEntity> type, CallbackInfoReturnable<DefaultAttributeContainer> cir) {
        if (EntityAttributeRegister.ShouldUseThisSystem()) {
            Optional<DefaultAttributeContainer> optional = EntityAttributeRegister.getAttributes(type);
            optional.ifPresent(cir::setReturnValue);
        }
    }

}
