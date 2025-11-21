package net.onixary.shapeShifterCurseFabric.minion;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface IPlayerEntityMinion {
    public HashMap<Identifier, List<UUID>> shape_shifter_curse$getAllMinions();

    public List<UUID> shape_shifter_curse$getMinionsByMinionID(Identifier MinionID);

    public int shape_shifter_curse$getMinionsCount();

    public int shape_shifter_curse$getMinionsCount(Identifier MinionID);

    public boolean shape_shifter_curse$minionExist(Identifier MinionID, UUID minionUUID);

    public boolean shape_shifter_curse$removeMinion(Identifier MinionID, UUID minionUUID);

    public boolean shape_shifter_curse$addMinion(MinionBase minionBase);

}
