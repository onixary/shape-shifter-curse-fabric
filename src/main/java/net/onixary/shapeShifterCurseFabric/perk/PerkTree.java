package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

// Common Side
public class PerkTree {
    public static class PerkNode {
        public final Identifier perkID;
        public final int tier;
        public final int y;
        public final @NotNull ArrayList<@NotNull IDependent> dependents;

        public PerkNode(Identifier perkID, int tier, int y) {
            this.perkID = perkID;
            this.tier = tier;
            this.y = y;
            this.dependents = new ArrayList<>();
        }

        public PerkNode(Identifier perkID, int tier, int y, @NotNull IDependent... dependentPerkIDs) {
            this.perkID = perkID;
            this.tier = tier;
            this.y = y;
            this.dependents = new ArrayList<>(Arrays.asList(dependentPerkIDs));
        }
    }

    public final Identifier treeID;
    public final List<PerkNode> perkNodes = new ArrayList<>();
    public final List<PerkNode> virtualNodes = new ArrayList<>();
    public final Map<Identifier, PerkNode> perkNodeMap = new HashMap<>();
    public final Map<Identifier, PerkNode> virtualNodeMap = new HashMap<>();

    // 仅用于渲染
    public int perkLowestTier_r = 0;

    public PerkTree(Identifier treeID) {
        this.treeID = treeID;
    }

    public Identifier getID() {
        return treeID;
    }

    public PerkTree addNode(Identifier perkID, int tier, int y) {
        return this.addNode(new PerkNode(perkID, tier, y));
    }

    public PerkTree addVirtualNode(Identifier perkID, int tier, int y) {
        return this.addVirtualNode(new PerkNode(perkID, tier, y));
    }

    public PerkTree addNode(Identifier perkID, int tier, int y, IDependent... dependents) {
        return this.addNode(new PerkNode(perkID, tier, y, dependents));
    }

    public PerkTree addVirtualNode(Identifier perkID, int tier, int y, IDependent... dependents) {
        return this.addVirtualNode(new PerkNode(perkID, tier, y, dependents));
    }

    public PerkTree addNode(PerkNode perkNode) {
        perkNodes.add(perkNode);
        perkNodeMap.put(perkNode.perkID, perkNode);
        if (perkNode.tier < perkLowestTier_r) perkLowestTier_r = perkNode.tier;
        return this;
    }

    public PerkTree addVirtualNode(PerkNode perkNode) {
        virtualNodes.add(perkNode);
        virtualNodeMap.put(perkNode.perkID, perkNode);
        if (perkNode.tier < perkLowestTier_r) perkLowestTier_r = perkNode.tier;
        return this;
    }

    public @Nullable PerkNode getNode(Identifier perkID) {
        return perkNodeMap.get(perkID);
    }

    public @Nullable PerkNode getVirtualNode(Identifier perkID) {
        return virtualNodeMap.get(perkID);
    }

    public @NotNull List<PerkNode> getDependentNode(Identifier perkID) {
        PerkNode perkNode = getNode(perkID);
        if (perkNode != null) {
            return perkNodes.stream().filter(perkNode1 -> perkNode1.dependents.contains(perkID)).toList();
        }
        return null;
    }

    public @NotNull List<Identifier> getAllPerks() {
        return new ArrayList<>(perkNodeMap.keySet());
    }

    public @NotNull List<Identifier> getAllVirtualPerks() {
        return new ArrayList<>(virtualNodeMap.keySet());
    }

    public @NotNull List<PerkNode> getAllNodes() {
        return new ArrayList<>(perkNodes);
    }

    public @NotNull List<PerkNode> getAllVirtualNodes() {
        return new ArrayList<>(virtualNodes);
    }

    public PerkTree forEachNode(Consumer<PerkNode> consumer) {
        perkNodes.forEach(consumer);
        return this;
    }
}
