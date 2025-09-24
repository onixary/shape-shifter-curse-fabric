package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.mixin.EntityAccessor;
import io.github.apace100.apoli.power.Power;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.CustomWaterBreathingPower;
import net.onixary.shapeShifterCurseFabric.additional_power.LootingPower;
import net.onixary.shapeShifterCurseFabric.integration.origins.power.OriginsPowerTypes;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModDamageSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class CustomWaterBreathingMixin {

    @Mixin(LivingEntity.class)
    public static abstract class CanBreatheInWater extends Entity {

        public CanBreatheInWater(EntityType<?> type, World world) {
            super(type, world);
        }

        @Inject(at = @At("HEAD"), method = "canBreatheInWater", cancellable = true)
        public void doWaterBreathing(CallbackInfoReturnable<Boolean> info) {
            if(PowerHolderComponent.getPowers(this, CustomWaterBreathingPower.class).stream().anyMatch(Power::isActive)) {
                info.setReturnValue(true);
            }
        }
    }


    @Mixin(PlayerEntity.class)
    public static abstract class UpdateAir extends LivingEntity {
        protected UpdateAir(EntityType<? extends LivingEntity> entityType, World world) {
            super(entityType, world);
        }
        @Unique
        private int getNextAirUnderwaterSlow(int air, int waterBreathLevel) {
            int i = waterBreathLevel;
            return i > 0 && this.random.nextInt(i + 1) > 0 ? air : air - 1;
        }

        // 使用原版水下呼吸逻辑的反向来实现陆地上慢速失去氧气
        // 水下呼吸等级越大，陆地上失去氧气的速度越慢
        // 24级为体验相对较好的数值
        @Inject(at = @At("TAIL"), method = "tick")
        private void tick(CallbackInfo info) {
            if(PowerHolderComponent.getPowers(this, CustomWaterBreathingPower.class).stream().anyMatch(Power::isActive)) {
                if(!this.isSubmergedIn(FluidTags.WATER)
                        && !this.hasStatusEffect(StatusEffects.WATER_BREATHING)
                        && !this.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    if(!((EntityAccessor) this).callIsBeingRainedOn()) {
                        int landWaterBreathLevel = PowerHolderComponent.getPowers(this, CustomWaterBreathingPower.class)
                                .stream()
                                .mapToInt(CustomWaterBreathingPower::getLandWaterBreathLevel).sum();

                        int landGain = this.getNextAirOnLand(0);
                        this.setAir(this.getNextAirUnderwaterSlow(this.getAir(), landWaterBreathLevel) - landGain);
                    } else if(this.getAir() < this.getMaxAir()){
                        //int landGain = this.getNextAirOnLand(0);
                        //this.setAir(this.getAir() - landGain);
                        this.setAir(this.getNextAirOnLand(this.getAir()));
                    }
                } else if(this.getAir() < this.getMaxAir()){
                    this.setAir(this.getNextAirOnLand(this.getAir()));
                }

                boolean isDamageWhenNoAir = PowerHolderComponent.getPowers(this, CustomWaterBreathingPower.class)
                        .stream()
                        .anyMatch(CustomWaterBreathingPower::isDamage_when_no_air);

                if(isDamageWhenNoAir)
                {
                    // 正常造成溺水伤害
                    if (this.getAir() == -20) {
                        this.setAir(0);

                        for(int i = 0; i < 8; ++i) {
                            double f = this.random.nextDouble() - this.random.nextDouble();
                            double g = this.random.nextDouble() - this.random.nextDouble();
                            double h = this.random.nextDouble() - this.random.nextDouble();
                            this.getWorld().addParticle(ParticleTypes.BUBBLE, this.getParticleX(0.5), this.getEyeY() + this.random.nextGaussian() * 0.08D, this.getParticleZ(0.5), f * 0.5F, g * 0.5F + 0.25F, h * 0.5F);
                        }

                        this.damage(ModDamageSources.getSource(getDamageSources(), ModDamageSources.NO_WATER_FOR_GILLS), 2.0F);
                    }
                }
                else{
                    // 不造成溺水伤害
                    if (this.getAir() < 0) {
                        // 没有氧气（湿润度）时设为-1定值来便于判定
                        this.setAir(-1);
                    }
                }

            }
        }

        @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"), method = "updateTurtleHelmet")
        public boolean isSubmergedInProxy(PlayerEntity player, TagKey<Fluid> fluidTag) {
            boolean submerged = this.isSubmergedIn(fluidTag);
            if(PowerHolderComponent.getPowers(this, CustomWaterBreathingPower.class).stream().anyMatch(Power::isActive)) {
                return !submerged;
            }
            return submerged;
        }
    }
}
