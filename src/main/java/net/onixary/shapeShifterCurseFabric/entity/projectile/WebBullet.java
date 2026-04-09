package net.onixary.shapeShifterCurseFabric.entity.projectile;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.Item;
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
import net.onixary.shapeShifterCurseFabric.additional_power.WebBridgeAction;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import net.onixary.shapeShifterCurseFabric.items.RegCustomItem;
import net.onixary.shapeShifterCurseFabric.status_effects.EntangledEffectUtils;

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
                if(this.owner != null){
                    switch (Tier){
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
            switch (Tier){
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
        switch (Tier) {
            case 1 -> {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 1));
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 1));
                }
            }
            case 2 -> {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 120, 2));
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 120, 2));
                }
            }
            case 3 -> {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 160, 3));
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 160, 3));
                }
                // 蛛网减速与现有施加buff有些重复，改为击杀被裹茧的实体会产生蛛网
//                if (entity.getWorld().getBlockState(entity.getBlockPos()).isAir()) {
//                    entity.getWorld().setBlockState(entity.getBlockPos(), Blocks.COBWEB.getDefaultState());
//                }
            }
        }
        if (entity instanceof LivingEntity livingEntity) {
            switch (Tier) {
                case 1 -> EntangledEffectUtils.applyEntangledEffect(livingEntity, Tier1BuffTime);
                case 2 -> EntangledEffectUtils.applyEntangledEffect(livingEntity, Tier2BuffTime);
                case 3 -> EntangledEffectUtils.applyEntangledEffect(livingEntity, Tier3BuffTime);
            }
        }
        playHitEffects();
        this.discard();
    }
}
