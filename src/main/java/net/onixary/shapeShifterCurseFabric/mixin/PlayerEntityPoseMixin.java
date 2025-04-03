package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.PosePower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityPoseMixin extends LivingEntity implements Nameable, CommandOutput {

    protected PlayerEntityPoseMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void eggolib$forcePose(CallbackInfo ci) {

        PowerHolderComponent.getPowers(this, PosePower.class)
                .stream()
                .max(Comparator.comparing(PosePower::getPriority))
                .ifPresent(p -> {
                    this.setPose(p.getPose());
                    ci.cancel();
                });

    }
}
