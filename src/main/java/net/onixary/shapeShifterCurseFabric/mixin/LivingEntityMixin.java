package net.onixary.shapeShifterCurseFabric.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.ActionOnSplashPotionTakeEffect;
import net.onixary.shapeShifterCurseFabric.additional_power.BurnDamageModifierPower;
import net.onixary.shapeShifterCurseFabric.additional_power.FallingProtectionPower;
import net.onixary.shapeShifterCurseFabric.additional_power.InWaterSpeedModifierPower;
import net.onixary.shapeShifterCurseFabric.cursed_moon.CursedMoon;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.items.RegCustomPotions;
import net.onixary.shapeShifterCurseFabric.status_effects.BaseTransformativeStatusEffect;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract float getMovementSpeed();

    @Shadow protected abstract void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition);

    @Inject(
            method = "onDeath",
            at = @At(
                    value = "HEAD"
            )
    )
    private void onEntityDeath(DamageSource source, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        World world = entity.getWorld();

        // 仅在服务端执行，避免客户端重复触发
        if (world.isClient) return;
        Entity attacker = source.getAttacker();
        // 自定义实体的掉落逻辑
        if (attacker instanceof ServerPlayerEntity) {
            if(entity instanceof WitchEntity || entity instanceof EvokerEntity) {
                if (Math.random() < StaticParams.FAMILIAR_CURSE_POTION_DROP_PROBABILITY){
                    ItemStack customPotion = PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), RegCustomPotions.FAMILIAR_FOX_FORM_POTION);
                    entity.getWorld().spawnEntity(
                            new ItemEntity(
                                    entity.getWorld(),
                                    entity.getX(),
                                    entity.getY(),
                                    entity.getZ(),
                                    customPotion
                            )
                    );
                }
            }
        }

        if(!(CursedMoon.isCursedMoon(world) && CursedMoon.isNight())){
            return;
        }

        if (attacker instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) attacker;
            if (entity instanceof MobEntity) {
                handleMobDeathDrop((MobEntity) entity, player);
            }
        }
    }

    @Unique
    private void handleMobDeathDrop(MobEntity mob, ServerPlayerEntity player) {
        // 概率掉落未加工的月之尘
        if (Math.random() < StaticParams.MOONDUST_DROP_PROBABILITY) {
            ItemStack stack = new ItemStack(RegCustomItem.UNTREATED_MOONDUST);
            mob.getWorld().spawnEntity(
                    new ItemEntity(
                            mob.getWorld(),
                            mob.getX(),
                            mob.getY(),
                            mob.getZ(),
                            stack
                    )
            );
        }
    }

    /**
     * Injects into the fall damage calculation method to modify the fall distance
     * used for damage computation, without affecting the original fall distance value
     * used in form's falling protection powers.
     */
    @ModifyVariable(
            method = "computeFallDamage(FF)I",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifyFallDistanceForDamageCalc(float fallDistance) {
        LivingEntity self = (LivingEntity) (Object) this;

        List<FallingProtectionPower> powers = PowerHolderComponent.getPowers(self, FallingProtectionPower.class);
        if (powers.isEmpty()) {
            return fallDistance;
        }

        float maxProtection = 0f;
        for (FallingProtectionPower power : powers) {
            if (power.isActive() && power.getFallDistance() > maxProtection) {
                maxProtection = power.getFallDistance();
            }
        }

        return Math.max(0f, fallDistance - maxProtection);
    }

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At("HEAD"))
    private void onStatusEffectAdded(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof PlayerEntity player) {
            // 检查是否是溅射药水或滞留药水造成的效果
            if ((source instanceof PotionEntity || source instanceof AreaEffectCloudEntity)) {
                PowerHolderComponent.getPowers(player, ActionOnSplashPotionTakeEffect.class)
                        .forEach(ActionOnSplashPotionTakeEffect::executeAction);
            }
        }
    }

    // todo: 直接强制修改hasModifyWaterSpeed似乎会导致广泛的与其他模组的mixin冲突，暂时禁用
    /*
    @Unique
    private boolean hasModifyWaterSpeed;

    @Inject(method = "travel", at = @At("HEAD"))
    private void onTravel(Vec3d movementInput, CallbackInfo ci) {
        this.hasModifyWaterSpeed = false;
    }

    @ModifyVariable(method = "travel", at = @At("STORE"), name = "g")
    private float modifyInWaterSpeed(float g) {
        if (this.hasModifyWaterSpeed) { return g; }
        this.hasModifyWaterSpeed = true;
        return this.getMovementSpeed() * 0.2f;  // g * (this.getMovementSpeed() / 0.1f) 或者 0.10000000149011612f g = 0.02f
    }*/

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V"), index = 0)
    private float ModifyInWaterSpeed(float g) {
        if ((LivingEntity)(Object)this instanceof PlayerEntity player) {
            // g -> 水中速度
            // g 范围 0.02 -> PlayerSpeed
            // 目标 PlayerSpeed * 0.2 -> PlayerSpeed
            // 会让所有其他修改水中速度失效
            // float PlayerSpeed = player.getMovementSpeed();
            // float newG = PlayerSpeed * 0.2f;
            // float h = (float) EnchantmentHelper.getDepthStrider(player);
            // if (h > 3.0F) {
            //     h = 3.0f;
            // }
            // if (!player.isOnGround()) {
            //     h *= 0.5F;
            // }
            // if (h > 0.0F) {
            //     newG += (this.getMovementSpeed() - newG) * h / 3.0F;
            // }
            // return newG;
            List<InWaterSpeedModifierPower> powers = PowerHolderComponent.getPowers(player, InWaterSpeedModifierPower.class);
            float totalSpeedModifier = powers
                    .stream()
                    .map(InWaterSpeedModifierPower::getSpeedModifier)
                    .reduce(1.0f, (a, b) -> a * b);
            return g * totalSpeedModifier;
        }
        return g;
    }

    // TODO 重构EffectManager时记得删掉这个Inject
    @Inject(method = "onStatusEffectRemoved", at = @At("HEAD"))
    private void onStatusEffectRemoved(StatusEffectInstance effect, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            // 检查移除的是否是变形效果
            if (effect.getEffectType() instanceof BaseTransformativeStatusEffect) {
                player.getServer().execute(() -> {
                    // 延迟检查，确保效果确实被移除
                    boolean stillHasTransformEffect = player.getStatusEffects().stream()
                            .anyMatch(e -> e.getEffectType() instanceof BaseTransformativeStatusEffect);

                    if (!stillHasTransformEffect) {
                        EffectManager.safeResetAttachment(player);
                    }
                });
            }
        }
    }
}
