package net.onixary.shapeShifterCurseFabric.integration.origins.mixin;

import io.github.apace100.apoli.mixin.EntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.onixary.shapeShifterCurseFabric.integration.origins.power.OriginsPowerTypes;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModDamageSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class WaterBreathingSlowMixin {

    @Mixin(LivingEntity.class)
    public static abstract class CanBreatheInWater extends Entity {

        public CanBreatheInWater(EntityType<?> type, World world) {
            super(type, world);
        }

        @Inject(at = @At("HEAD"), method = "canBreatheInWater", cancellable = true)
        public void doWaterBreathing(CallbackInfoReturnable<Boolean> info) {
            if(OriginsPowerTypes.WATER_BREATHING_SLOW.isActive(this)) {
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

        @Inject(at = @At("TAIL"), method = "tick")
        private void tick(CallbackInfo info) {
            if(OriginsPowerTypes.WATER_BREATHING_SLOW.isActive(this)) {
                if(!this.isSubmergedIn(FluidTags.WATER)
                        && !this.hasStatusEffect(StatusEffects.WATER_BREATHING)
                        && !this.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    if(!((EntityAccessor) this).callIsBeingRainedOn()) {
                        int landGain = this.getNextAirOnLand(0);
                        this.setAir(this.getNextAirUnderwaterSlow(this.getAir(), 24) - landGain);
                    } else {
                        int landGain = this.getNextAirOnLand(0);
                        this.setAir(this.getAir() - landGain);
                    }
                } else if(this.getAir() < this.getMaxAir()){
                    this.setAir(this.getNextAirOnLand(this.getAir()));
                }

                if (this.getAir() < 0) {
                    // 没有氧气（湿润度）时设为-1定值来便于判定
                    this.setAir(-1);
                }
            }
        }

        @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"), method = "updateTurtleHelmet")
        public boolean isSubmergedInProxy(PlayerEntity player, TagKey<Fluid> fluidTag) {
            boolean submerged = this.isSubmergedIn(fluidTag);
            if(OriginsPowerTypes.WATER_BREATHING_SLOW.isActive(this)) {
                return !submerged;
            }
            return submerged;
        }
    }
}
