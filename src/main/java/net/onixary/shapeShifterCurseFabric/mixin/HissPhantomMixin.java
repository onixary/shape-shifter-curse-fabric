package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.additional_power.HissPhantomPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$SwoopMovementGoal")
public class HissPhantomMixin {
    @Unique
    private PhantomEntity phantomEntity;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setPhantomEntity(PhantomEntity phantomEntity, CallbackInfo ci) {
        this.phantomEntity = phantomEntity;
    }

    @Inject(method = "shouldContinue", at = @At("RETURN"), cancellable = true)
    private void shouldContinue(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = phantomEntity.getTarget();
        if (cir.getReturnValueZ() && livingEntity != null) {
            HissPhantomPower power = PowerHolderComponent.getPowers(livingEntity, HissPhantomPower.class).stream().findFirst().orElse(null);
            if (power != null && power.isActive()) {
                power.invokeAction(livingEntity, phantomEntity);
                cir.setReturnValue(false);
                return;
            }
            List<PlayerEntity> entities = phantomEntity.getWorld().getNonSpectatingEntities(PlayerEntity.class, livingEntity.getBoundingBox().expand(8.0f));
            if (!entities.isEmpty()) {
                for (PlayerEntity playerEntity : entities) {
                    HissPhantomPower otherPower = PowerHolderComponent.getPowers(playerEntity, HissPhantomPower.class).stream().findFirst().orElse(null);
                    if (otherPower != null && otherPower.isActive()) {
                        otherPower.invokeAction(playerEntity, phantomEntity);
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
    }
}
