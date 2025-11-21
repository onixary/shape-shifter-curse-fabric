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

public abstract class MinionBase extends TameableEntity {
    public MinionBase(EntityType<? extends MinionBase> entityType, World world) {
        super(entityType, world);
    }

    public MinionBase(EntityType<? extends MinionBase> entityType, World world, PlayerEntity player) {
        this(entityType, world);
        if (player instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            iPlayerEntityMinion.shape_shifter_curse$addMinion(this);
            // this.setOwner(player);
        }
        else {
            ShapeShifterCurseFabric.LOGGER.error("PlayerEntity is not IPlayerEntityMinion, It Shouldn't Happen!");
            this.setHealth(0.0f);   // 自动死亡
            return;
        }
    }

    public Identifier minionTypeID;

    public UUID ownerUUID;  // 自动设置为玩家UUID

    public double getMinionDisappearRange() {
        return 128.0d;  // 128格外自动消失 如果不需要这个功能可以填Double.MAX_VALUE 如果没有让召唤物强制传送功能必须要设置一个合理的值 否则召唤物可能会卸载
    }

    public boolean shouldExist() {
        if (this.ownerUUID == null) {
            return false;
        }
        PlayerEntity owner = this.getWorld().getPlayerByUuid(this.ownerUUID);
        if (owner == null) {
            return false;
        }
        if (owner instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            return iPlayerEntityMinion.shape_shifter_curse$minionExist(this.minionTypeID, this.getUuid());
        }
        if (this.squaredDistanceTo(owner) > this.getMinionDisappearRange()) {
            return true;
        }
        return false;
    }

    @Override
    public void initGoals() {
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
    }

    @Override
    public void setOwner(PlayerEntity player) {
        this.ownerUUID = player.getUuid();
        super.setOwner(player);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("ownerUUID", this.ownerUUID.toString());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.ownerUUID = UUID.fromString(nbt.getString("ownerUUID"));
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
        if (this.ownerUUID != null && this.getWorld().getPlayerByUuid(this.ownerUUID) instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            iPlayerEntityMinion.shape_shifter_curse$removeMinion(this.minionTypeID, this.getUuid());
        }
        super.onDeath(source);
    }
}
