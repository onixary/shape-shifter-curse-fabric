package net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.wolf;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.TWolfFriendlyPower;
import net.onixary.shapeShifterCurseFabric.data.StaticParams;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.status_effects.TStatusApplier;
import org.jetbrains.annotations.Nullable;

import static net.onixary.shapeShifterCurseFabric.status_effects.RegTStatusEffect.TO_ANUBIS_WOLF_0_EFFECT;

public class TransformativeWolfEntity extends WolfEntity {

    public static float T_WOLF_STATUS_CHANCE = 0.5f;

    public TransformativeWolfEntity(EntityType<? extends WolfEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        PassiveData data;
        if (entityData instanceof PassiveData passiveData) {
            passiveData.babyAllowed = false;
            data = passiveData;
        }
        else {
            data = new PassiveData(false);
        }
        return super.initialize(world, difficulty, spawnReason, data, entityNbt);
    }

    // 20 ticks = 1 second
    private static final float ATTACK_COOLDOWN = 100.0F;

    // 当前冷却时间
    private float cooldown = 0;

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new WolfEscapeDangerGoal(1.5));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new AvoidLlamaGoal(this, LlamaEntity.class, 24.0F, 1.5, 1.5));
        this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(6, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(8, new WolfBegGoal(this, 8.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0]));
        this.targetSelector.add(3, new UntamedActiveTargetGoal(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
        this.targetSelector.add(4, new ActiveTargetGoal(this, AbstractSkeletonEntity.class, false));
        this.targetSelector.add(5, new UniversalAngerGoal(this, true));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        // 我觉得把逆天攻击距离移除之后 攻击力高点也没多大问题
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }

    public static boolean canCustomSpawn(EntityType<TransformativeWolfEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        BlockPos NowCheckPos = pos;
        // 脚下如果藏有TNT 则不生成 防止沙漠神殿自爆
        for (int i = 0; i < 5; i++) {
            if (world.getBlockState(NowCheckPos).getBlock() == Blocks.TNT) {
                return false;
            }
            NowCheckPos = NowCheckPos.down();
        }
        float Chance = ShapeShifterCurseFabric.commonConfig.transformativeWolfSpawnChance;
        if (Chance <= 0.0f) { return false; }
        if (Chance >= 1.0f) { return true; }
        return random.nextFloat() < Chance;
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (target instanceof PlayerEntity playerEntity) {
            if (PowerHolderComponent.hasPower(playerEntity, TWolfFriendlyPower.class)) {
                return false;
            }
        }
        return super.canTarget(target);
    }

    @Override
    public void tick() {
        super.tick();
        // 更新冷却时间
        if (cooldown > 0) {
            cooldown--;
        }

        // 生成粒子效果
        if (this.getWorld().isClient) {
            for (int i = 0; i < 1; i++) {
                this.getWorld().addParticle(StaticParams.CUSTOM_MOB_DEFAULT_PARTICLE,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.5,
                        this.getY() + this.random.nextDouble() * 0.5,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
                        0, 0, 0);
            }
        }
    }

    @Override
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        // 在applyStatusByChance里面已经判断形态了 无需在外面判断
        if (target instanceof PlayerEntity player) {
            TStatusApplier.applyStatusByChance(T_WOLF_STATUS_CHANCE, player, TO_ANUBIS_WOLF_0_EFFECT);
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        if(target instanceof PlayerEntity) {
            boolean attacked = target.damage(this.getDamageSources().mobAttack(this), (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
            if (attacked) {
                this.applyDamageEffects(this, target);
            }
            return attacked;
        }
        return super.tryAttack(target);
    }

    // 禁止与此生物交互 防止使用Wolf的驯服逻辑
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        return ActionResult.PASS;
    }

    @Override
    public void setOwner(PlayerEntity player) {
        return;
    }

    @Override
    protected Identifier getLootTableId() {
        return new Identifier(ShapeShifterCurseFabric.MOD_ID, "entities/t_wolf");
    }

    @Override
    public void setTamed(boolean tamed) {
        return;
    }
}