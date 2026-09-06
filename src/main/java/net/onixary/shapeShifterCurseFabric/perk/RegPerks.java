package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RegPerks {
    public static final HashMap<Identifier, IPerk> PerkRegistry = new HashMap<>();
    public static final HashMap<Identifier, PerkTree> PerkTreeRegistry = new HashMap<>();

    public static Identifier registerPerk(IPerk perk) {
        PerkRegistry.put(perk.getID(), perk);
        return perk.getID();
    }

    public static @Nullable IPerk getPerk(Identifier perkID) {
        return PerkRegistry.get(perkID);
    }

    public static Identifier registerPerkTree(PerkTree perkTree) {
        PerkTreeRegistry.put(perkTree.getID(), perkTree);
        return perkTree.getID();
    }

    public static @Nullable PerkTree getPerkTree(Identifier perkTreeID) {
        return PerkTreeRegistry.get(perkTreeID);
    }
}
