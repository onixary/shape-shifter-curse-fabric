package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Common Side
public class PerkTree {
    public static class PerkNode {
        public final Identifier perkID;
        public final int tier;
        public final int y;
        public final @NotNull ArrayList<@NotNull Identifier> dependentPerkIDs;

        public PerkNode(Identifier perkID, int tier, int y) {
            this.perkID = perkID;
            this.tier = tier;
            this.y = y;
            this.dependentPerkIDs = new ArrayList<>();
        }

        public PerkNode(Identifier perkID, int tier, int y, @NotNull Identifier... dependentPerkIDs) {
            this.perkID = perkID;
            this.tier = tier;
            this.y = y;
            this.dependentPerkIDs = new ArrayList<>(Arrays.asList(dependentPerkIDs));
        }
    }

    public final Identifier treeID;
    public final List<PerkNode> perkNodes = new ArrayList<>();
    public final Map<Identifier, PerkNode> perkNodeMap = new HashMap<>();

    public PerkTree(Identifier treeID) {
        this.treeID = treeID;
    }

    public Identifier getID() {
        return treeID;
    }

    public PerkTree addNode(Identifier perkID, int tier, int y) {
        return this.addNode(new PerkNode(perkID, tier, y));
    }

    public PerkTree addNode(Identifier perkID, int tier, int y, Identifier... dependentPerkIDs) {
        return this.addNode(new PerkNode(perkID, tier, y, dependentPerkIDs));
    }

    public PerkTree addNode(PerkNode perkNode) {
        perkNodes.add(perkNode);
        perkNodeMap.put(perkNode.perkID, perkNode);
        return this;
    }

    public @Nullable PerkNode getNode(Identifier perkID) {
        return perkNodeMap.get(perkID);
    }

    public @NotNull List<PerkNode> getDependentNode(Identifier perkID) {
        PerkNode perkNode = getNode(perkID);
        if (perkNode != null) {
            return perkNodes.stream().filter(perkNode1 -> perkNode1.dependentPerkIDs.contains(perkID)).toList();
        }
        return null;
    }

    public @NotNull List<Identifier> getAllPerks() {
        return new ArrayList<>(perkNodeMap.keySet());
    }

    public @NotNull List<PerkNode> getAllNodes() {
        return new ArrayList<>(perkNodes);
    }
}
