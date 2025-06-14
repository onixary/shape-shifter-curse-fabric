package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.PosePower;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityPoseMixin extends LivingEntity implements Nameable, CommandOutput {

    @Shadow public abstract boolean isSwimming();

    protected PlayerEntityPoseMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void eggolib$forcePose(CallbackInfo ci) {

        PowerHolderComponent.getPowers(this, PosePower.class)
                .stream()
                .max(Comparator.comparing(PosePower::getPriority))
                .ifPresent(p -> {
                    if(isSneaking()){
                        this.setPose(p.getPose());
                        ci.cancel();
                    }
                });


        PlayerEntity player = (PlayerEntity) (Object) this;
        PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
        boolean isFeral = RegFormConfig.getConfig(curForm).getBodyType() == PlayerFormBodyType.FERAL;
        if(isFeral){
            if(isSwimming()){
                this.setPose(EntityPose.STANDING);
            }
            else if(isSleeping()){
                this.setPose(EntityPose.STANDING);
            }
            //else if(this.isFallFlying()){
            //    this.setPose(EntityPose.STANDING);
            //}
            ci.cancel();
        }
    }
}
