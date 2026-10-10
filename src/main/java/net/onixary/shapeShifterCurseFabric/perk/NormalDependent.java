package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.List;

public class NormalDependent implements IDependent {
    public final Identifier[] dependentPerkIDs;

    public NormalDependent(Identifier... dependentPerkIDs) {
        this.dependentPerkIDs = dependentPerkIDs == null ? new Identifier[0] : dependentPerkIDs;
    }

    @Override
    public boolean isDependentPerk(@NotNull Identifier perk) {
        for (Identifier dependentPerkID : dependentPerkIDs) {
            if (dependentPerkID.equals(perk)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAllDependentGained(PlayerEntity player, List<Identifier> playerGainedPerk) {
        if (this.dependentPerkIDs.length == 0) return true;
        if (playerGainedPerk == null) return false;
        for (Identifier id : this.dependentPerkIDs) {
            if (!playerGainedPerk.contains(id)) return false;
        }
        return true;
    }

    @Override
    public void drawDependentLine(DrawContext drawContext, Vector2i nodeCenter, PerkTree tree, PerkTree.PerkNode perkNode) {
        if (this.dependentPerkIDs.length == 0) return;
        final int ox = nodeCenter.x;
        final int oy = nodeCenter.y;
        for (Identifier dependPerkID : this.dependentPerkIDs) {
            PerkTree.PerkNode dependNodeMetaData = tree.getNode(dependPerkID);
            if (dependNodeMetaData == null) continue;
            int x1 = nodeBaseX + posXPerTier * perkNode.tier + nodeLineDependXOffset;
            int x2 = nodeBaseX + posXPerTier * dependNodeMetaData.tier + nodeLineRootXOffset;
            int y1 = perkNode.y;
            int y2 = dependNodeMetaData.y;
            if (perkNode.tier - 1 == dependNodeMetaData.tier) {
                int halfX = (x1 + x2) / 2;
                drawContext.fill(
                        ox + Math.min(x1, halfX), oy + y1,
                        ox + Math.max(x1, halfX) + 1, oy + y1 + 1,
                        LineColor);
                drawContext.fill(
                        ox + halfX, oy + Math.min(y1, y2),
                        ox + halfX + 1, oy + Math.max(y1, y2) + 1,
                        LineColor);
                drawContext.fill(
                        ox + Math.min(x2, halfX), oy + y2,
                        ox + Math.max(x2, halfX) + 1, oy + y2 + 1,
                        LineColor);
            } else {
                // AI整的虚线 看起来应该没有对应的API了 所以尽量别整需要虚线的Perk 这种比较费性能 除非使用贴图 但是这种不太好改
                int dashLen = posXPerTier / 2 - 10;
                int dashSize = 2;
                int gapSize = 1;
                int lastPixelX = x1;
                for (int i = 0; i < dashLen; i += dashSize + gapSize) {
                    int to = Math.min(i + dashSize, dashLen);
                    if (i >= to) break;
                    drawContext.fill(
                            ox + x1 - to, oy + y1,
                            ox + x1 - i, oy + y1 + 1,
                            LineColor);
                    lastPixelX = x1 - to;
                }
                drawContext.fill(ox + lastPixelX - 2, oy + y1 - 1, ox + lastPixelX - 1, oy + y1 + 2, LineColor);
            }
        }
    }
}
