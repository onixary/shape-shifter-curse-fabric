package net.onixary.shapeShifterCurseFabric.minion.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.minion.IMinion;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;

import java.util.UUID;

public class WolfMinion extends WolfEntity implements IMinion<WolfMinion> {
    private static final Identifier MinionID = ShapeShifterCurseFabric.identifier("wolf_minion");

    public WolfMinion(EntityType<? extends WolfMinion> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, -1.0F);
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
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
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

    @Override
    public Identifier getMinionTypeID() {
        return MinionID;
    }

    @Override
    public WolfMinion getSelf() {
        return this;
    }

    public static DefaultAttributeContainer.Builder createWolfMinionAttributes() {
        // 速度0.3
        // 生命10
        // 攻击2
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
            case 1:
                break;
            case 2:
                health.setBaseValue(16.0d);
                break;
            case 3:
                health.setBaseValue(24.0d);
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
    public void tick() {
        if (!this.getWorld().isClient && !this.shouldExist()) {
            this.setHealth(0.0f);  // 自动死亡
        }
        if (!this.hasStatusEffect(StatusEffects.WITHER)) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, -1, 0));
        }
        super.tick();
    }

    @Override
    public void applyDamageEffects(LivingEntity attacker, Entity target) {
        if (attacker instanceof WolfMinion minion && target instanceof LivingEntity livingEntity)  {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * minion.MinionLevel, 2));
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
        nbt.putUuid("ownerUUID", this.getMinionOwnerUUID());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.MinionLevel = nbt.getInt("MinionLevel");
        this.setMinionOwnerUUID(nbt.getUuid("ownerUUID"));
    }

    public double getMinionDisappearRange() {
        return 128.0d;  // 128格外自动消失 如果不需要这个功能可以填Double.MAX_VALUE 如果没有让召唤物强制传送功能必须要设置一个合理的值 否则召唤物可能会卸载
    }

    // TODO 修一下客户端返回False的问题
    public boolean shouldExist() {
        if (this.getMinionOwnerUUID() == null) {
            return false;
        }
        PlayerEntity owner = this.getWorld().getPlayerByUuid(this.getMinionOwnerUUID());
        if (owner == null) {
            return false;
        }
        if (owner instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            return iPlayerEntityMinion.shape_shifter_curse$minionExist(this.getMinionTypeID(), this.getUuid());
        }
        if (this.squaredDistanceTo(owner) > this.getMinionDisappearRange()) {
            return true;
        }
        return false;
    }

    @Override
    public void onDeath(DamageSource source) {
        if (this.getMinionOwnerUUID() != null && this.getWorld().getPlayerByUuid(this.getMinionOwnerUUID()) instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            iPlayerEntityMinion.shape_shifter_curse$removeMinion(this.getMinionTypeID(), this.getUuid());
        }
        super.onDeath(source);
    }

    @Override
    public World method_48926() {
        return this.getWorld();
    }

    class WolfMinionEscapeDangerGoal extends EscapeDangerGoal {
        public WolfMinionEscapeDangerGoal(double speed) {
            super(WolfMinion.this, speed);
        }

        protected boolean isInDanger() {
            return this.mob.shouldEscapePowderSnow() || this.mob.isOnFire();
        }
    }
}
