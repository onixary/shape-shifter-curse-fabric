package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegFormConfig;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.feralItemCenter;

@Mixin(ElytraEntityModel.class)
public abstract class ElytraEntityModelMixin<T extends LivingEntity> {
    @Shadow
    @Final
    private ModelPart rightWing;

    @Shadow
    @Final
    private ModelPart leftWing;

    @Inject(
            method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
            at = @At("TAIL"),
            cancellable = true
    )
    private void customElytraAngles(T entity, float limbAngle, float limbDistance,
                                    float animationProgress, float headYaw, float headPitch,
                                    CallbackInfo ci) {
        float k = 0.2617994f;
        float l = -0.2617994f;
        float m = 0.0f;
        float n = 0.0f;
        if (((LivingEntity)entity).isFallFlying()) {
            float o = 1.0f;
            Vec3d vec3d = ((Entity)entity).getVelocity();
            if (vec3d.y < 0.0) {
                Vec3d vec3d2 = vec3d.normalize();
                o = 1.0f - (float)Math.pow(-vec3d2.y, 1.5);
            }
            k = o * 0.34906584f + (1.0f - o) * k;
            l = o * -1.5707964f + (1.0f - o) * l;
        } else if (((Entity)entity).isInSneakingPose()) {
            k = 0.6981317f;
            l = -0.7853982f;
            m = 3.0f;
            n = 0.08726646f;
        }
        this.leftWing.pivotY = m;
        if (entity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity;
            abstractClientPlayerEntity.elytraPitch += (k - abstractClientPlayerEntity.elytraPitch) * 0.1f;
            abstractClientPlayerEntity.elytraYaw += (n - abstractClientPlayerEntity.elytraYaw) * 0.1f;
            abstractClientPlayerEntity.elytraRoll += (l - abstractClientPlayerEntity.elytraRoll) * 0.1f;

            PlayerForms curForm = RegPlayerFormComponent.PLAYER_FORM.get(entity).getCurrentForm();
            boolean isFeral = RegFormConfig.getConfig(curForm).getBodyType() == PlayerFormBodyType.FERAL;
            if(isFeral){
                this.leftWing.pitch = k + (float)Math.toRadians(70.0);
                this.leftWing.roll = l+ (float)Math.toRadians(70.0);
                this.leftWing.yaw = n+ (float)Math.toRadians(45.0);
            }
            else{
                this.leftWing.pitch = abstractClientPlayerEntity.elytraPitch;
                this.leftWing.yaw = abstractClientPlayerEntity.elytraYaw;
                this.leftWing.roll = abstractClientPlayerEntity.elytraRoll;
            }
        } else {
            this.leftWing.pitch = k;
            this.leftWing.roll = l;
            this.leftWing.yaw = n;
        }
        this.rightWing.yaw = -this.leftWing.yaw;
        this.rightWing.pivotY = this.leftWing.pivotY;
        this.rightWing.pitch = this.leftWing.pitch;
        this.rightWing.roll = -this.leftWing.roll;

        // 取消原版的角度设置
        ci.cancel();
    }
}
