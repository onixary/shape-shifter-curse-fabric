package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
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
    private void forcePose(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(player).getCurrentForm();
        boolean isFeral = RegFormConfig.getConfig(curForm).getBodyType() == PlayerFormBodyType.FERAL;
        if(isFeral){
            if (this.wouldPoseNotCollide(EntityPose.SWIMMING)) {
                EntityPose entityPose;
                if (this.isFallFlying()) {
                    entityPose = EntityPose.FALL_FLYING;
                } else if (this.isSleeping()) {
                    entityPose = EntityPose.STANDING;
                } else if (this.isSwimming()) {
                    entityPose = EntityPose.SWIMMING;
                } else if (this.isUsingRiptide()) {
                    entityPose = EntityPose.SPIN_ATTACK;
                } else if (this.isSneaking()) {
                    entityPose = EntityPose.CROUCHING;
                } else {
                    entityPose = EntityPose.STANDING;
                }

                EntityPose entityPose2;
                if (!this.isSpectator() && !this.hasVehicle() && !this.wouldPoseNotCollide(entityPose)) {
                    if (this.wouldPoseNotCollide(EntityPose.CROUCHING)) {
                        entityPose2 = EntityPose.CROUCHING;
                    } else {
                        entityPose2 = EntityPose.STANDING;
                    }
                } else {
                    entityPose2 = entityPose;
                }

                this.setPose(entityPose2);
            }
            ci.cancel();

            /*if(isSwimming()){
                this.setPose(EntityPose.STANDING);
            }
            if(isSleeping()){
                this.setPose(EntityPose.STANDING);
            }*/
            //else if(this.isFallFlying()){
            //    this.setPose(EntityPose.STANDING);
            //}
            //ci.cancel();
        }
    }
}
