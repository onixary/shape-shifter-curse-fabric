package net.onixary.shapeShifterCurseFabric.minion.mobs;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.minion.IMinion;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AnubisWolfMinionEntity extends WolfEntity implements IMinion<AnubisWolfMinionEntity> {
    public static final Identifier MinionID = ShapeShifterCurseFabric.identifier("anubis_wolf_minion");

    public AnubisWolfMinionEntity(EntityType<? extends AnubisWolfMinionEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, -1.0F);
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

    public int MinionLevel = 1;

    public void setMinionLevel(int level) {
        this.MinionLevel = level;
        this.ApplyMinionLevel(true);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new WolfMinionEscapeDangerGoal(1.5));
        this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(6, new FollowOwnerGoalNoTP(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, AbstractSkeletonEntity.class, false));
    }

    @Override
    public void InitMinion(PlayerEntity player) {
        if (player instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            iPlayerEntityMinion.shape_shifter_curse$addMinion(this);
        }
        else {
            ShapeShifterCurseFabric.LOGGER.error("PlayerEntity is not IPlayerEntityMinion, It Shouldn't Happen!");
            this.setHealth(0.0f);   // 自动死亡
        }
    }

    @Override
    public UUID getMinionOwnerUUID() {
        return super.getOwnerUuid();
    }

    @Override
    public void setMinionOwnerUUID(UUID uuid) {
        this.setOwnerUuid(uuid);
    }

    @Override
    public void setOwner(PlayerEntity player) {
        super.setOwner(player);
    }

    public Identifier getMinionTypeID() {
        return MinionID;
    }

    @Override
    public AnubisWolfMinionEntity getSelf() {
        return this;
    }

    @Override
    public boolean isUndead() {
        return true;
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        StatusEffect statusEffect = effect.getEffectType();
        return statusEffect != StatusEffects.REGENERATION && statusEffect != StatusEffects.POISON;
    }

    public static DefaultAttributeContainer.Builder createWolfMinionAttributes() {
        // 速度0.3
        // 生命10/16/24
        // 攻击2/3/4
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30000001192092896)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    private void ApplyMinionLevel(boolean modifyHP) {
        EntityAttributeInstance health = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeInstance attack_damage = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (health == null || attack_damage == null) {
            ShapeShifterCurseFabric.LOGGER.error("wolf minion attribute error");
            return;
        }
        switch (MinionLevel) {
            // 默认参数似乎直接break不会生效，依然要设置下
            case 1:
                health.setBaseValue(10.0d);
                attack_damage.setBaseValue(2.0d);
                break;
            case 2:
                health.setBaseValue(16.0d);
                attack_damage.setBaseValue(3.0d);
                break;
            case 3:
                health.setBaseValue(20.0d);
                attack_damage.setBaseValue(4.0d);
                break;
            default:
                ShapeShifterCurseFabric.LOGGER.error("wolf minion level error");
                break;
        }
        if (modifyHP) {
            this.setHealth((float) health.getValue());
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean IsSuccess = super.tryAttack(target);
        LivingEntity Owner = this.getOwner();
        if (Owner == null) {
            return IsSuccess;
        }
        if (IsSuccess) {
            switch (MinionLevel) {
                case 1:
                    break;
                case 2:
                    Owner.heal(1.0f);
                case 3:
                    Owner.heal(2.0f);
                default:
                    break;
            }
        }
        return IsSuccess;
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient) {
            if (!this.shouldExist()) {
                this.setHealth(0.0f);  // 自动死亡
            }
            if (!this.hasStatusEffect(StatusEffects.WITHER)) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, -1, 0));
            }
        }
        super.tick();
        // processAnim();
    }
    /* 弃用 不会做AzureLib模型 原先的模型不兼容 连测试都没法测试
    public void processAnim() {
        if (this.isAttacking()) {
            WolfMinionAnimator.ATTACK_COMMAND.sendForEntity(this);
        } else if (this.isTouchingWater()) {
            WolfMinionAnimator.FLOAT_COMMAND.sendForEntity(this);
        } else if (!this.isOnGround()) {
            if (this.getVelocity().getY() > 0.0) {
                WolfMinionAnimator.JUMP_COMMAND.sendForEntity(this);
            }
            else {
                WolfMinionAnimator.FALL_COMMAND.sendForEntity(this);
            }
        } else {
            if (this.getVelocity().getX() != 0 || this.getVelocity().getZ() != 0) {
                WolfMinionAnimator.WALK_COMMAND.sendForEntity(this);
            }
            else {
                WolfMinionAnimator.IDLE_COMMAND.sendForEntity(this);
            }
        }
    }
     */

    @Override
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        if (attacker instanceof AnubisWolfMinionEntity minion && target instanceof LivingEntity livingEntity)  {
            // 额外加5tick防止效果消失在伤害判定边缘
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * minion.MinionLevel + 5, 2));
        }
        super.applyDamageEffects(attacker, target);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        return ActionResult.PASS;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("MinionLevel", this.MinionLevel);
        nbt.putFloat("MinionHealth", this.getHealth());  // 原版Bug不知道什么时候修
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.MinionLevel = nbt.getInt("MinionLevel");
        this.ApplyMinionLevel(false);
        this.setHealth(nbt.getFloat("MinionHealth"));
    }

    public double getMinionDisappearRange() {
        return 1024.0d;  // 自动消失距离的二次方 如果不需要这个功能可以填Double.MAX_VALUE 如果没有让召唤物强制传送功能必须要设置一个合理的值 否则召唤物可能会卸载
    }

    public boolean shouldExist() {
        if (this.getWorld().isClient) {
            return true;
        }
        if (this.getMinionOwnerUUID() == null) {
            return false;
        }
        PlayerEntity owner = this.getWorld().getPlayerByUuid(this.getMinionOwnerUUID());
        if (owner == null) {
            return false;
        }
        if (this.squaredDistanceTo(owner) > this.getMinionDisappearRange()) {
            return false;
        }
        if (owner instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            return iPlayerEntityMinion.shape_shifter_curse$minionExist(this.getMinionTypeID(), this.getUuid());
        }
        return false;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
    }

    @Override
    public void onDeath(DamageSource source) {
        if (this.getMinionOwnerUUID() != null && this.getWorld().getPlayerByUuid(this.getMinionOwnerUUID()) instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            iPlayerEntityMinion.shape_shifter_curse$removeMinion(this.getMinionTypeID(), this.getUuid());
        }
        // 清除死亡Message
        this.setOwnerUuid(null);
        super.onDeath(source);
    }

    @Override
    public World method_48926() {
        return super.getWorld();
    }

    class WolfMinionEscapeDangerGoal extends EscapeDangerGoal {
        public WolfMinionEscapeDangerGoal(double speed) {
            super(AnubisWolfMinionEntity.this, speed);
        }

        protected boolean isInDanger() {
            return this.mob.shouldEscapePowderSnow() || this.mob.isOnFire();
        }
    }
}
