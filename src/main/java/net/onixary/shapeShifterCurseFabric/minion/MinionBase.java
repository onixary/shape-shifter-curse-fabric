package net.onixary.shapeShifterCurseFabric.minion;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;

import java.util.UUID;

// 推荐直接继承MinionBase而非单独实现IMinion接口

public abstract class MinionBase extends TameableEntity implements IMinion<MinionBase> {
    public MinionBase(EntityType<? extends MinionBase> entityType, World world) {
        super(entityType, world);
    }

    public void InitMinion(PlayerEntity player) {
        if (player instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            iPlayerEntityMinion.shape_shifter_curse$addMinion(this);
        }
        else {
            ShapeShifterCurseFabric.LOGGER.error("PlayerEntity is not IPlayerEntityMinion, It Shouldn't Happen!");
            this.setHealth(0.0f);   // 自动死亡
        }
    }

    public Identifier minionTypeID;

    @Override
    public Identifier getMinionTypeID() {
        return this.minionTypeID;
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
    public MinionBase getSelf() {
        return this;
    }

    @Override
    public World method_48926() {
        return this.getWorld();
    }

    public double getMinionDisappearRange() {
        return 128.0d;  // 128格外自动消失 如果不需要这个功能可以填Double.MAX_VALUE 如果没有让召唤物强制传送功能必须要设置一个合理的值 否则召唤物可能会卸载
    }

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
    protected void initGoals() {
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
    }

    @Override
    public void setOwner(PlayerEntity player) {
        super.setOwner(player);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putUuid("ownerUUID", this.getMinionOwnerUUID());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setMinionOwnerUUID(nbt.getUuid("ownerUUID"));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.shouldExist()) {
            this.setHealth(0.0f);  // 自动死亡
        }
    }

    // 从玩家召唤物列表中移除
    @Override
    public void onDeath(DamageSource source) {
        if (this.getMinionOwnerUUID() != null && this.getWorld().getPlayerByUuid(this.getMinionOwnerUUID()) instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            iPlayerEntityMinion.shape_shifter_curse$removeMinion(this.getMinionTypeID(), this.getUuid());
        }
        super.onDeath(source);
    }
}
