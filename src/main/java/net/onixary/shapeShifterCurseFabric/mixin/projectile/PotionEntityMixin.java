package net.onixary.shapeShifterCurseFabric.mixin.projectile;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnSplashPotionTakeEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotionEntity.class)
public class PotionEntityMixin {

    @Inject(method = "applyWater", at = @At("HEAD"))
    private void onApplyWater(CallbackInfo ci) {
        PotionEntity self = (PotionEntity) (Object) this;
        Box box = self.getBoundingBox().expand(4.0, 2.0, 4.0);

        List<LivingEntity> entities = self.getWorld().getNonSpectatingEntities(LivingEntity.class, box);
        for (LivingEntity entity : entities) {
            if (entity instanceof PlayerEntity player) {
                double distance = self.squaredDistanceTo(entity);
                if (distance < 16.0) {
                    PowerHolderComponent.getPowers(player, ActionOnSplashPotionTakeEffect.class)
                            .stream()
                            .filter(ActionOnSplashPotionTakeEffect::shouldTriggerOnNoEffect)
                            .forEach(power -> {
                                //ShapeShifterCurseFabric.LOGGER.info("Water bottle hit player {}, triggering action", player.getName().getString());
                                power.executeAction();
                            });
                }
            }
        }
    }

    @Inject(method = "applySplashPotion", at = @At("HEAD"))
    private void onApplySplashPotion(List<StatusEffectInstance> effects, Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            PowerHolderComponent.getPowers(player, ActionOnSplashPotionTakeEffect.class)
                    .stream()
                    .filter(ActionOnSplashPotionTakeEffect::isActive)
                    .forEach(ActionOnSplashPotionTakeEffect::executeAction);
        }
    }

    @Inject(method = "applyLingeringPotion", at = @At("HEAD"))
    private void onApplyLingeringPotion(ItemStack stack, Potion potion, CallbackInfo ci) {
        List<StatusEffectInstance> effects = PotionUtil.getPotionEffects(stack);
        if (effects.isEmpty()) {
            PotionEntity self = (PotionEntity) (Object) this;
            Box box = self.getBoundingBox().expand(3.0, 2.0, 3.0);

            List<LivingEntity> entities = self.getWorld().getNonSpectatingEntities(LivingEntity.class, box);
            for (LivingEntity entity : entities) {
                if (entity instanceof PlayerEntity player) {
                    PowerHolderComponent.getPowers(player, ActionOnSplashPotionTakeEffect.class)
                            .stream()
                            .filter(ActionOnSplashPotionTakeEffect::isActive)
                            .forEach(ActionOnSplashPotionTakeEffect::executeAction);
                }
            }
        }
    }
}