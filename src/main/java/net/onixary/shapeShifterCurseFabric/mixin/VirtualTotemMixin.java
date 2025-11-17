package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.onixary.shapeShifterCurseFabric.additional_power.VirtualTotemPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = LivingEntity.class, priority = 10)
public class VirtualTotemMixin {
    @Inject(method = "tryUseTotem", at = @At("RETURN"), cancellable = true)
    public void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }
        if (!cir.getReturnValue()) {
            if ((Object)this instanceof LivingEntity livingEntity) {
                List<VirtualTotemPower> virtualTotemPowerList = PowerHolderComponent.getPowers(livingEntity, VirtualTotemPower.class);
                if (virtualTotemPowerList.isEmpty()) {
                    return;
                }
                for (VirtualTotemPower virtualTotemPower : virtualTotemPowerList) {
                    if (virtualTotemPower.canUse()) {
                        virtualTotemPower.use();
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}
