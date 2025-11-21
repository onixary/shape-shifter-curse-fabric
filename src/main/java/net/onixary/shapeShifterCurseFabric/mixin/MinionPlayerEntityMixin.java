package net.onixary.shapeShifterCurseFabric.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import net.onixary.shapeShifterCurseFabric.minion.MinionBase;
import net.onixary.shapeShifterCurseFabric.minion.PlayerMinionComponent;
import net.onixary.shapeShifterCurseFabric.minion.RegPlayerMinionComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class MinionPlayerEntityMixin implements IPlayerEntityMinion {
    @Unique
    private @Nullable PlayerMinionComponent getPlayerMinionComponent() {
        try {
            return RegPlayerMinionComponent.PLAYER_MINION_DATA.get((PlayerEntity)(Object)this);
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to get PlayerMinionComponent", e);
            return null;
        }
    }

    @Unique
    private boolean syncPlayerMinionComponent() {
        try {
            RegPlayerMinionComponent.PLAYER_MINION_DATA.sync((PlayerEntity)(Object)this);
            return true;
        } catch (Exception e) {
            ShapeShifterCurseFabric.LOGGER.error("Failed to sync PlayerMinionComponent", e);
            return false;
        }
    }

    @Override
    public HashMap<Identifier, List<UUID>> shape_shifter_curse$getAllMinions() {
        PlayerMinionComponent playerMinionComponent = this.getPlayerMinionComponent();
        if (playerMinionComponent != null) {
            return playerMinionComponent.minions;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public List<UUID> shape_shifter_curse$getMinionsByMinionID(Identifier MinionID) {
        return this.shape_shifter_curse$getAllMinions().computeIfAbsent(MinionID, k -> new ArrayList<>());
    }

    @Override
    public int shape_shifter_curse$getMinionsCount() {
        int total = 0;
        for (Identifier minionID : shape_shifter_curse$getAllMinions().keySet()) {
            total += this.shape_shifter_curse$getMinionsCount(minionID);
        }
        return total;
    }

    @Override
    public int shape_shifter_curse$getMinionsCount(Identifier MinionID) {
        return this.shape_shifter_curse$getMinionsByMinionID(MinionID).size();
    }

    @Override
    public boolean shape_shifter_curse$minionExist(Identifier MinionID, UUID minionUUID) {
        return this.shape_shifter_curse$getMinionsByMinionID(MinionID).contains(minionUUID);
    }

    @Override
    public boolean shape_shifter_curse$removeMinion(Identifier MinionID, UUID minionUUID) {
        boolean result = this.shape_shifter_curse$getMinionsByMinionID(MinionID).remove(minionUUID);
        this.syncPlayerMinionComponent();
        return result;
    }

    @Override
    public boolean shape_shifter_curse$addMinion(MinionBase minionBase) {
        this.shape_shifter_curse$getMinionsByMinionID(minionBase.minionTypeID).add(minionBase.getUuid());
        minionBase.setOwner((PlayerEntity)(Object)this);
        this.syncPlayerMinionComponent();
        return true;
    }

    // 检查召唤物是否存在
    @Unique
    private void checkMinion(PlayerEntity realThis, ServerWorld world) {
        HashMap<Identifier, List<UUID>> minions = this.shape_shifter_curse$getAllMinions();
        for (Identifier minionID : minions.keySet()) {
            for (UUID minionUUID : minions.get(minionID)) {
                if (world.getEntity(minionUUID) == null) {
                    this.shape_shifter_curse$removeMinion(minionID, minionUUID);
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void shape_shifter_curse$onTick(CallbackInfo ci) {
        PlayerEntity realThis = (PlayerEntity)(Object)this;
        World world = realThis.getWorld();
        if (world instanceof ServerWorld serverWorld && realThis.age % 20 == 0) {
            this.checkMinion(realThis, serverWorld);
        }
    }
}
