package net.onixary.shapeShifterCurseFabric.entity.projectile;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.TrinketsConditionAction;
import net.onixary.shapeShifterCurseFabric.additional_power.WebBridgeAction;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.EntangledEffectUtils;

import java.util.Map;
import java.util.Optional;

import static net.onixary.shapeShifterCurseFabric.entity.RegCustomEntity.WEB_BULLET;

public class WebBullet extends ThrownItemEntity {
    public @Nullable LivingEntity owner = null;
    public int Tier = 1;
    private boolean launched = false;

    public static final WebBridgeAction.WebLadderConfig ladderConfigTier1 = new WebBridgeAction.WebLadderConfig(10, 14, 8, false, 0.0f);
    public static final WebBridgeAction.WebLadderConfig ladderConfigTier2 = new WebBridgeAction.WebLadderConfig(14, 18, 12, true, 0.25f);
    public static final WebBridgeAction.WebLadderConfig ladderConfigTier3 = new WebBridgeAction.WebLadderConfig(18, 24, 16, true, 0.4f);

    public static final int Tier1BuffTime = 200;
    public static final int Tier2BuffTime = 400;
    public static final int Tier3BuffTime = 600;

    public WebBullet(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
        this.Tier = 1;
    }

    public WebBullet(double d, double e, double f, World world, int Tier) {
        super(WEB_BULLET, d, e, f, world);
        this.Tier = Tier;
    }

    public WebBullet(LivingEntity livingEntity, int Tier) {
        super(WEB_BULLET, livingEntity, livingEntity.getWorld());
        this.Tier = Tier;
        this.owner = livingEntity;
    }

    @Override
    public Item getDefaultItem() {
        return RegCustomItem.WEB_PROJECTILE;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            if (!launched) {
                launched = true;
                if (this.owner != null) {
                    switch (Tier) {
                        case 1 -> {
                            serverWorld.playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(),
                                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0f, 0.6f + this.random.nextFloat() * 0.4f);
                        }
                        case 2 -> {
                            serverWorld.playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(),
                                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0f, 0.9f + this.random.nextFloat() * 0.4f);
                        }
                        case 3 -> {
                            serverWorld.playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(),
                                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0f, 1.2f + this.random.nextFloat() * 0.4f);
                        }
                    }
                }
            }
            switch (Tier) {
                case 1 -> {
                    serverWorld.spawnParticles(ParticleTypes.ASH,
                            this.getX(), this.getY(), this.getZ(),
                            3, 0.05, 0.05, 0.05, 0.01);
                }
                case 2 -> {
                    serverWorld.spawnParticles(ParticleTypes.SPIT,
                            this.getX(), this.getY(), this.getZ(),
                            1, 0.05, 0.05, 0.05, 0.01);
                }
                case 3 -> {
                    serverWorld.spawnParticles(ParticleTypes.CLOUD,
                            this.getX(), this.getY(), this.getZ(),
                            2, 0.05, 0.05, 0.05, 0.01);
                }
            }

            if (this.getWorld().getBlockState(this.getBlockPos()).isLiquid()) {
                this.discard();
            }

            if (this.getWorld().getBlockState(this.getBlockPos()).isOf(RegCustomBlock.TEMP_WEB_BRIDGE)) {
                this.onBlockHit(new BlockHitResult(this.getPos(), Direction.DOWN, this.getBlockPos(), false));
            }
        }
    }

    private boolean isExtraHandVenomSpindleEquipped(PlayerEntity player) {
        // Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
        // if (component.isEmpty()) {
        //     return false;
        // }
        // Map<String, TrinketInventory> groupInv = component.get().getInventory().get("hand");
        // if (groupInv == null) {
        //     return false;
        // }
        // TrinketInventory inv = groupInv.get("extra_hand");
        // if (inv == null) {
        //     return false;
        // }
        // return inv.getStack(0).isOf(RegCustomItem.VENOM_SPINDLE);
        return TrinketsConditionAction.CheckEquipped(
                player, "auto", "hand", "extra_hand", 0,
                (stack) -> {
                    return stack.isOf(RegCustomItem.VENOM_SPINDLE);
                },
                false
        );
    }

    private void playHitEffects() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.CLOUD,
                    this.getX(), this.getY(), this.getZ(),
                    20, 0.3, 0.3, 0.3, 0.05);
            serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_WET_GRASS_BREAK, SoundCategory.NEUTRAL, 1.0f, 0.8f + this.random.nextFloat() * 0.4f);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("web_projectile", true);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {
        WebBridgeAction.WebLadderConfig nowConfig = null;
        switch (Tier) {
            case 1 -> nowConfig = ladderConfigTier1;
            case 2 -> nowConfig = ladderConfigTier2;
            case 3 -> nowConfig = ladderConfigTier3;
            default -> nowConfig = ladderConfigTier1;
        }
        WebBridgeAction.BuildWebLadder(this.getWorld(), blockHitResult, nowConfig, RegCustomBlock.TEMP_WEB_BRIDGE);
        playHitEffects();
        this.discard();
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();

        // 检测 owner 的 extra_hand 槽位是否装备了箭毒纺锤，并根据tier形态施加效果
        if (this.owner instanceof PlayerEntity player && entity instanceof LivingEntity target) {
            if (isExtraHandVenomSpindleEquipped(player)) {
                switch (Tier) {
                    case 1 -> {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 1));
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 20, 2));
                        target.damage(this.getDamageSources().thrown(this, this.owner), 5.0F);
                    }
                    case 2 -> {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 120, 2));
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 2));
                        target.damage(this.getDamageSources().thrown(this, this.owner), 6.0F);
                    }
                    case 3 -> {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 160, 3));
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 2));
                        target.damage(this.getDamageSources().thrown(this, this.owner), 8.0F);
                    }
                }
            }
            else {
                // 原版蛛网弹效果
                switch (Tier) {
                    case 1 -> {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 1));
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 1));
                        EntangledEffectUtils.applyEntangledEffect(target, Tier1BuffTime);
                    }
                    case 2 -> {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 120, 2));
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 120, 2));
                        EntangledEffectUtils.applyEntangledEffect(target, Tier2BuffTime);
                    }
                    case 3 -> {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 160, 3));
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 160, 3));
                        EntangledEffectUtils.applyEntangledEffect(target, Tier3BuffTime);
                    }
                }
            }
        }

        playHitEffects();
        this.discard();
    }
}
