package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.WidgetEXUtils;
import net.onixary.shapeShifterCurseFabric.perk.PerkTree;
import net.onixary.shapeShifterCurseFabric.perk.PerkUtils;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

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
    public int cameraPosX = 0;
    public int cameraPosY = 0;
    public float cameraScale = 1.0f;  // 不一定实现 得看手动鼠标计算位置好不好算

    public final int nodeWindowX = 0;
    public final int nodeWindowY = 0;
    public final int nodeWindowWidth = 250;
    public final int nodeWindowHeight = 200;

    public final int nodeBaseX = 50;
    public final int nodeBaseY = nodeWindowHeight / 2;
    public final int posXPerTier = 50;
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
        int X1 = nodeWindowX + nodeBaseX + this.posXPerTier * perkNode.tier + nodeLineDependXOffset;
        int X2 = nodeWindowX + nodeBaseX + this.posXPerTier * dependNodeMetaData.tier + nodeLineRootXOffset;
        int Y1 = nodeWindowY + nodeBaseY + perkNode.y;
        int Y2 = nodeWindowY + nodeBaseY + dependNodeMetaData.y;
        int HalfX = (X1 + X2) / 2;
        context.fill(X1, Y1, HalfX + 1, Y1, LineColor);
        context.fill(HalfX, Y1, HalfX + 1, Y2, LineColor);
        context.fill(HalfX, Y2, X2 + 1, Y2, LineColor);
    }

    // UNTESTED
    // playerGainedPerk 由调用方获取 毕竟drawNode调用频繁
    public void drawNode(DrawContext context, PerkTree.PerkNode perkNode, List<Identifier> playerGainedPerk, int mouseX, int mouseY, float delta) {
        this.drawConnectLine(context, perkNode);
        Identifier icon = RegPerks.getPerkIcon(perkNode.perkID);
        if (icon == null) {
            icon = RegPerks.FALLBACK_PERK_ICON;
        }
        if (playerGainedPerk.contains(perkNode.perkID)) {
            // TODO
        }
        context.drawTexture(icon, nodeWindowX + nodeBaseX + this.posXPerTier * perkNode.tier, nodeWindowY + nodeBaseY + perkNode.y, 0, 0, 16, 16, 16, 16);
    }

    // UNTESTED
    public void drawAllNode(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client == null) return;
        context.enableScissor(nodeWindowX, nodeWindowY, nodeWindowX + nodeWindowWidth, nodeWindowY + nodeWindowHeight);
        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();
        matrixStack.translate(cameraPosX, cameraPosY, 0);
        matrixStack.scale(cameraScale, cameraScale, 1.0f);
        PerkTree tree = this.perkTree;
        List<Identifier> playerGainedPerk = PerkUtils.getPlayerPerks(this.client.player, tree.getID());
        for (PerkTree.PerkNode perkNode : tree.getAllNodes()) {
            this.drawNode(context, perkNode, playerGainedPerk, mouseX, mouseY, delta);
        }
        matrixStack.pop();
        context.disableScissor();
    }

    // UNTESTED
    public Vector2i getVirtualMousePos(int mouseX, int mouseY) {
        return new Vector2i(mouseX - nodeWindowX - cameraPosX, mouseY - nodeWindowY - cameraPosY).div(cameraScale);
    }
}
