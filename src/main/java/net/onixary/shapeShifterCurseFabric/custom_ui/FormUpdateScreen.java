package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.WidgetEXUtils;
import net.onixary.shapeShifterCurseFabric.perk.PerkTree;
import net.onixary.shapeShifterCurseFabric.perk.PerkUtils;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// 标记 UNTESTED 代表这个函数没测试 测试完了就删(估计最后得有一堆没测试函数 还是标一下大概率炸的函数吧)

public class FormUpdateScreen extends Screen implements WidgetEXUtils.IWidgetEX {
    // Node得建立一个注册表 用来标记Power更改 Icon路径 再上几个API什么的
    // NodeMetaData生成也得要注册表 根据FormData里的数据选对应的初始化函数

    public boolean isLocked;
    public @NotNull PerkTree perkTree;

    public @Nullable Identifier nowSelectNode;
    public final int nodeBaseX = 0;
    public final int nodeBaseY = 0;
    public final int Tier0X = 100;
    public final int posXPerTier = 100;
    public final int nodeLineRootXOffset = 9;
    public final int nodeLineDependXOffset = -9;
    public final int LineColor = 0xFF9F9F9F;

    @Override
    public WidgetEXUtils.WidgetRect getRect() {
        return null;
    }

    public List<WidgetEXUtils.IWidgetEX> WidgetList = new ArrayList<>();

    @Override
    public List<WidgetEXUtils.IWidgetEX> getWidgetList() {
        return this.WidgetList;
    }

    protected FormUpdateScreen(Text title, boolean isLocked, @NotNull PerkTree perkTree) {
        super(title);
        this.isLocked = isLocked;
        this.perkTree = perkTree;
    }

    @Override
    public void init() {
        super.init();
    }

    // Utils

    // UNTESTED
    public void drawConnectLine(DrawContext context, PerkTree.PerkNode perkNode) {
        Identifier depend = perkNode.dependentPerkID;
        if (depend == null) return;
        PerkTree.PerkNode dependNodeMetaData = perkTree.getNode(depend);
        if (dependNodeMetaData == null) return;
        int X1 = nodeBaseX + Tier0X + this.posXPerTier * perkNode.tier + nodeLineDependXOffset;
        int X2 = nodeBaseX + Tier0X + this.posXPerTier * dependNodeMetaData.tier + nodeLineRootXOffset;
        int Y1 = nodeBaseY + perkNode.y;
        int Y2 = nodeBaseY + dependNodeMetaData.y;
        int HalfX = (X1 + X2) / 2;
        context.fill(X1, Y1, HalfX + 1, Y1, LineColor);
        context.fill(HalfX, Y1, HalfX + 1, Y2, LineColor);
        context.fill(HalfX, Y2, X2 + 1, Y2, LineColor);
    }

    // UNTESTED
    // playerGainedPerk 由调用方获取 毕竟drawNode调用频繁
    public void drawNode(DrawContext context, PerkTree.PerkNode perkNode, List<Identifier> playerGainedPerk) {
        this.drawConnectLine(context, perkNode);
        Identifier icon = RegPerks.getPerkIcon(perkNode.perkID);
        if (icon == null) {
            icon = RegPerks.FALLBACK_PERK_ICON;
        }
        if (playerGainedPerk.contains(perkNode.perkID)) {
            // TODO
        }
        context.drawTexture(icon, nodeBaseX + Tier0X + this.posXPerTier * perkNode.tier, nodeBaseY + perkNode.y, 0, 0, 16, 16, 16, 16);
    }
}
