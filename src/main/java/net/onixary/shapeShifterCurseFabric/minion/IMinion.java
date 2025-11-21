package net.onixary.shapeShifterCurseFabric.minion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public interface IMinion<T extends LivingEntity> {
    void InitMinion(PlayerEntity player);

    void setOwner(PlayerEntity player);

    UUID getMinionOwnerUUID();

    void setMinionOwnerUUID(UUID uuid);

    Identifier getMinionTypeID();

    T getSelf();
}
