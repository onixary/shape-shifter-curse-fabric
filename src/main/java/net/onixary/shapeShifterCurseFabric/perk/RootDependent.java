package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.List;

public class RootDependent implements IDependent {
    private final int rootTier;
    private final int rootY;

    public RootDependent(int rootTier, int rootY) {
        this.rootTier = rootTier;
        this.rootY = rootY;
    }

    @Override
    public boolean isDependentPerk(@NotNull Identifier perk) {
        return false;
    }

    @Override
    public boolean isAllDependentGained(PlayerEntity player, List<Identifier> playerGainedPerk) {
        return true;
    }

    @Override
    public void drawDependentLine(DrawContext drawContext, int nowNodeBaseX, Vector2i nodeCenter, PerkTree tree, PerkTree.PerkNode perkNode) {
        int ox = nodeCenter.x;
        int oy = nodeCenter.y;
        int x1 = nowNodeBaseX + posXPerTier * perkNode.tier + nodeLineDependXOffset;
        int x2 = nowNodeBaseX + posXPerTier * rootTier + nodeLineRootXOffset;
        int y1 = perkNode.y;
        int y2 = rootY;
        // 思考了一下 这个一般接的是虚拟节点 没法显示依赖的框 So 直接不渲染这种特殊情况吧
        if (rootTier >= perkNode.tier) {
            return;
        }
        int vx = nowNodeBaseX + posXPerTier * rootTier + posXPerTier / 2;
        drawContext.fill(
                ox + Math.min(x1, vx), oy + y1,
                ox + Math.max(x1, vx) + 1, oy + y1 + 1,
                LineColor);
        drawContext.fill(
                ox + vx, oy + Math.min(y1, y2),
                ox + vx + 1, oy + Math.max(y1, y2) + 1,
                LineColor);
        drawContext.fill(
                ox + Math.min(x2, vx), oy + y2,
                ox + Math.max(x2, vx) + 1, oy + y2 + 1,
                LineColor);
    }
}
