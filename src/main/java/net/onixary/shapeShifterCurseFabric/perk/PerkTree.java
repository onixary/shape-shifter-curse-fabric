package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Common Side
public class PerkTree {
    public static class PerkNode {
        public final Identifier perkID;
        public final int tier;
        public final int y;
        public final @Nullable Identifier dependentPerkID;

        public PerkNode(Identifier perkID, int tier, int y, @Nullable Identifier dependentPerkID) {
            this.perkID = perkID;
            this.tier = tier;
            this.y = y;
            this.dependentPerkID = dependentPerkID;
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

    public PerkTree addNode(Identifier perkID, int tier, int y, @Nullable Identifier dependentPerkID) {
        return this.addNode(new PerkNode(perkID, tier, y, dependentPerkID));
    }

    public PerkTree addNode(PerkNode perkNode) {
        perkNodes.add(perkNode);
        perkNodeMap.put(perkNode.perkID, perkNode);
        return this;
    }

    public @Nullable PerkNode getNode(Identifier perkID) {
        return perkNodeMap.get(perkID);
    }

    public @Nullable PerkNode getDependentNode(Identifier perkID) {
        PerkNode perkNode = getNode(perkID);
        if (perkNode != null && perkNode.dependentPerkID != null) {
            return getNode(perkNode.dependentPerkID);
        }
        return null;
    }
}
