package net.onixary.shapeShifterCurseFabric.minion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface IPlayerEntityMinion {
    public ConcurrentHashMap<Identifier, ArrayList<UUID>> shape_shifter_curse$getAllMinions();

    public ArrayList<UUID> shape_shifter_curse$getMinionsByMinionID(Identifier MinionID);

    public int shape_shifter_curse$getMinionsCount();

    public int shape_shifter_curse$getMinionsCount(Identifier MinionID);

    public boolean shape_shifter_curse$minionExist(Identifier MinionID, UUID minionUUID);

    public boolean shape_shifter_curse$removeMinion(Identifier MinionID, UUID minionUUID);

    public <T extends IMinion<? extends LivingEntity>> boolean shape_shifter_curse$addMinion(T minion);

    public void shape_shifter_curse$applyCooldown(Identifier MinionID, long time);

    public long shape_shifter_curse$getCooldownTime(Identifier MinionID);
}
