package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.AlwaysSweepingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.BreathingUnderWaterPower;
import net.onixary.shapeShifterCurseFabric.integration.origins.power.OriginsPowerTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class BreathingUnderWaterMixin {

    @Mixin(LivingEntity.class)
    public static abstract class GetNextAirUnderWater extends Entity {

        public GetNextAirUnderWater(EntityType<?> type, World world) {
            super(type, world);
        }

        @Inject(at = @At("HEAD"), method = "getNextAirUnderwater", cancellable = true)
        private void getNextAirUnderwater(int air, CallbackInfoReturnable<Integer> info) {
            PowerHolderComponent component = PowerHolderComponent.KEY.get(this);

            for (BreathingUnderWaterPower power : component.getPowers(BreathingUnderWaterPower.class)) {
                if (power.isActive()) {
                    int i = 100;
                    int returnValue = this.random.nextInt(i + 1) > 0 ? air : air - 1;
                    info.setReturnValue(returnValue);
                }
            }
        }
    }
    /*@Mixin(PlayerEntity.class)
    public static abstract class UpdateAir extends LivingEntity {

        @Shadow public abstract PlayerAbilities getAbilities();

        protected UpdateAir(EntityType<? extends LivingEntity> entityType, World world) {
            super(entityType, world);
        }
        @Unique
        private int getNextAirUnderwaterSlow(int air, int waterBreathLevel) {
            int i = waterBreathLevel;
            return i > 0 && this.random.nextInt(i + 1) > 0 ? air : air - 1;
        }

        @Inject(at = @At("TAIL"), method = "tick")
        private void tick(CallbackInfo info) {
            if(OriginsPowerTypes.BREATHING_UNDER_WATER.isActive(this)) {
                if(this.isSubmergedIn(FluidTags.WATER) && !this.hasStatusEffect(StatusEffects.WATER_BREATHING) && !this.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    int landGain = this.getNextAirOnLand(0);
                    this.setAir(this.getNextAirUnderwaterSlow(this.getAir(), 100) - landGain);

                } else if(this.getAir() < this.getMaxAir()){
                    this.setAir(this.getNextAirOnLand(this.getAir()));
                }

                if (this.isSubmergedIn(FluidTags.WATER) && !this.getWorld().getBlockState(BlockPos.ofFloored(this.getX(), this.getEyeY(), this.getZ())).isOf(Blocks.BUBBLE_COLUMN)) {
                    boolean notBreathUnderWater = !this.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing(this) && (!(this).getAbilities().invulnerable);
                    if (notBreathUnderWater) {
                        this.setAir(this.getNextAirUnderwaterSlow(this.getAir(), 100));
                        if (this.getAir() == -20) {
                            this.setAir(0);
                            Vec3d vec3d = this.getVelocity();

                            for(int i = 0; i < 8; ++i) {
                                double f = this.random.nextDouble() - this.random.nextDouble();
                                double g = this.random.nextDouble() - this.random.nextDouble();
                                double h = this.random.nextDouble() - this.random.nextDouble();
                                this.getWorld().addParticle(ParticleTypes.BUBBLE, this.getX() + f, this.getY() + g, this.getZ() + h, vec3d.x, vec3d.y, vec3d.z);
                            }

                            this.damage(this.getDamageSources().drown(), 2.0F);
                        }
                    }

                    if (!this.getWorld().isClient && this.hasVehicle() && this.getVehicle() != null && this.getVehicle().shouldDismountUnderwater()) {
                        this.stopRiding();
                    }
                } else if (this.getAir() < this.getMaxAir()) {
                    this.setAir(this.getNextAirOnLand(this.getAir()));
                }
            }
        }
    }*/
}
