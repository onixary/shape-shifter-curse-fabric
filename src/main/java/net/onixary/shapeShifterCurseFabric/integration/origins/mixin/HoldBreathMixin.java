package net.onixary.shapeShifterCurseFabric.integration.origins.mixin;

import io.github.apace100.apoli.mixin.EntityAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.integration.origins.power.OriginsPowerTypes;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModDamageSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class HoldBreathMixin {

    /*@Mixin(LivingEntity.class)
    public static abstract class CanBreatheInWater extends Entity {

        public CanBreatheInWater(EntityType<?> type, World world) {
            super(type, world);
        }

        @Inject(at = @At("HEAD"), method = "canBreatheInWater", cancellable = true)
        public void doWaterBreathing(CallbackInfoReturnable<Boolean> info) {
            if(OriginsPowerTypes.HOLD_BREATH.isActive(this)) {
                info.setReturnValue(true);
            }
        }
    }*/

    @Mixin(LivingEntity.class)
    public static abstract class GetNextAirUnderWater extends Entity {

        public GetNextAirUnderWater(EntityType<?> type, World world) {
            super(type, world);
        }

        @Inject(at = @At("HEAD"), method = "getNextAirUnderwater", cancellable = true)
        private void getNextAirUnderwater(int air, CallbackInfoReturnable<Integer> info) {
            if(OriginsPowerTypes.HOLD_BREATH.isActive(this)) {
                int i = 3;
                int returnValue = this.random.nextInt(i + 1) > 0 ? air : air - 1;
                info.setReturnValue(returnValue);
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
            if(OriginsPowerTypes.HOLD_BREATH.isActive(this)) {
                /*if(this.isSubmergedIn(FluidTags.WATER) && !this.hasStatusEffect(StatusEffects.WATER_BREATHING) && !this.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    int landGain = this.getNextAirOnLand(0);
                    this.setAir(this.getNextAirUnderwaterSlow(this.getAir(), 1) - landGain);

                } else if(this.getAir() < this.getMaxAir()){
                    this.setAir(this.getNextAirOnLand(this.getAir()));
                }*/
                /*if (this.isSubmergedIn(FluidTags.WATER))
                {
                    boolean hasWaterBreathEffect = this.hasStatusEffect(StatusEffects.WATER_BREATHING) || this.hasStatusEffect(StatusEffects.CONDUIT_POWER);
                    if (!hasWaterBreathEffect) {
                        int landGain = this.getNextAirOnLand(0);
                        this.setAir(this.getNextAirUnderwaterSlow(this.getAir(), 24) - landGain);
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


                    if(this.isSubmergedIn(FluidTags.WATER)
                            && !this.hasStatusEffect(StatusEffects.WATER_BREATHING)
                            && !this.hasStatusEffect(StatusEffects.CONDUIT_POWER))
                    {
                            int landGain = this.getNextAirOnLand(0);
                            this.setAir(this.getNextAirUnderwaterSlow(this.getAir(), 1) + landGain);
                    }
                    else if(this.getAir() < this.getMaxAir())
                    {
                        this.setAir(this.getNextAirOnLand(this.getAir()));
                    }

            }
        }
    }*/
}
