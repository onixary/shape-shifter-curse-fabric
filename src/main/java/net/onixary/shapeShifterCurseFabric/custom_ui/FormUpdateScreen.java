package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.MinecraftClient;
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
import java.util.List;

// 标记 UNTESTED 代表这个函数没测试 测试完了就删(估计最后得有一堆没测试函数 还是标一下大概率炸的函数吧)

public class FormUpdateScreen extends Screen implements WidgetEXUtils.IWidgetEX {
    public boolean isLocked;
    public @NotNull PerkTree perkTree;

    public @Nullable PerkTree.PerkNode nowSelectNode;
    // TODO 需要调中心点 目前左上角作为中心点不太行
    // 中心点:
    // Camera 中心
    // Node 左中
    public int cameraPosX = 0;
    public int cameraPosY = 0;
    public float cameraScale = 1.0f;  // 不一定实现 得看手动鼠标计算位置好不好算

    public static final int nodeWindowX = 0;
    public static final int nodeWindowY = 0;
    public static final int nodeWindowWidth = 250;
    public static final int nodeWindowHeight = 200;

    // 基础渲染原点(左上) -> cameraCenter(中心) -> nodeCenter(左中)
    public static final Vector2i cameraCenter = new Vector2i(nodeWindowX + nodeWindowWidth / 2, nodeWindowY + nodeWindowHeight / 2);
    public static final Vector2i nodeCenter = new Vector2i( -nodeWindowWidth / 2, 0);  // 参考的是cameraCenter为原点

    public static final int nodeBaseX = 50;
    public static final int posXPerTier = 50;
    public static final int nodeLineRootXOffset = 9;
    public static final int nodeLineDependXOffset = -9;
    public static final int LineColor = 0xFF9F9F9F;

    public static final int NodeDrawStartX = -7;
    public static final int NodeDrawStartY = -7;
    public static final int NodeTextureWidth = 16;
    public static final int NodeTextureHeight = 16;

    public static final int NodeSelectStartX = -8;
    public static final int NodeSelectStartY = -8;
    public static final int NodeSelectRectWidth = 18;
    public static final int NodeSelectRectHeight = 18;

    @Override
    public WidgetEXUtils.WidgetRect getRect() {
        return null;
    }

    public List<WidgetEXUtils.IWidgetEX> WidgetList = new ArrayList<>();

    @Override
    public List<WidgetEXUtils.IWidgetEX> getWidgetList() {
        return this.WidgetList;
    }

    public FormUpdateScreen(Text title, boolean isLocked, @NotNull PerkTree perkTree) {
        super(title);
        this.isLocked = isLocked;
        this.perkTree = perkTree;
    }

    @Override
    public void init() {
        super.init();
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.onClickWidget(mouseX, mouseY, button);
        this.NodeScreenMouseClickHandler((int)mouseX, (int)mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(nodeWindowX, nodeWindowY, nodeWindowX + nodeWindowWidth, nodeWindowY + nodeWindowHeight, 0xFF000000);
        this.drawAllNode(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    // Utils

    // UNTESTED
    public void drawConnectLine(DrawContext context, PerkTree.PerkNode perkNode) {
        Identifier depend = perkNode.dependentPerkID;
        if (depend == null) return;
        PerkTree.PerkNode dependNodeMetaData = perkTree.getNode(depend);
        if (dependNodeMetaData == null) return;
        int X1 = nodeCenter.x + nodeBaseX + posXPerTier * perkNode.tier + nodeLineDependXOffset;
        int X2 = nodeCenter.x + nodeBaseX + posXPerTier * dependNodeMetaData.tier + nodeLineRootXOffset;
        int Y1 = nodeCenter.y + perkNode.y;
        int Y2 = nodeCenter.y + dependNodeMetaData.y;
        int HalfX = (X1 + X2) / 2;
        context.fill(X1, Y1, HalfX + 1, Y1 + 1, LineColor);
        context.fill(HalfX, Y1, HalfX + 1, Y2 + 1, LineColor);
        context.fill(HalfX, Y2, X2 + 1, Y2 + 1, LineColor);
    }

    // UNTESTED
    // playerGainedPerk 由调用方获取 毕竟drawNode调用频繁
    public void drawNode(DrawContext context, PerkTree.PerkNode perkNode, @Nullable List<Identifier> playerGainedPerk, int mouseX, int mouseY, float delta) {
        this.drawConnectLine(context, perkNode);
        Identifier icon = RegPerks.getPerkIcon(perkNode.perkID);
        if (icon == null) {
            icon = RegPerks.FALLBACK_PERK_ICON;
        }
        if (playerGainedPerk != null && playerGainedPerk.contains(perkNode.perkID)) {
            // TODO
        }
        // TODO 有点晚了 明天再优化一下 先让AI整一个能用的 行吧 还没法用 明天再算
        int virtualNodeX = nodeBaseX + posXPerTier * perkNode.tier;
        int virtualNodeY = perkNode.y;
        int NodePosX = nodeCenter.x + virtualNodeX;
        int NodePosY = nodeCenter.y + virtualNodeY;
        int selectLeft = virtualNodeX + NodeSelectStartX;
        int selectTop = virtualNodeY + NodeSelectStartY;
        int selectRight = selectLeft + NodeSelectRectWidth;
        int selectBottom = selectTop + NodeSelectRectHeight;
        if (mouseX >= selectLeft && mouseX < selectRight && mouseY >= selectTop && mouseX < selectBottom) {
            context.fill(
                    NodePosX + NodeSelectStartX,
                    NodePosY + NodeSelectStartY,
                    NodePosX + NodeSelectStartX + NodeSelectRectWidth,
                    NodePosY + NodeSelectStartY + NodeSelectRectHeight,
                    0xFFFFFFFF
            );
        }
        context.drawTexture(icon, NodePosX + NodeDrawStartX, NodePosY + NodeDrawStartY, 0, 0, NodeTextureWidth, NodeTextureHeight, NodeTextureWidth, NodeTextureHeight);
    }

    // UNTESTED
    public void drawAllNode(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client == null) return;
        context.enableScissor(nodeWindowX, nodeWindowY, nodeWindowX + nodeWindowWidth, nodeWindowY + nodeWindowHeight);
        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();
        matrixStack.translate(cameraCenter.x + cameraPosX, cameraCenter.y + cameraPosY, 0);
        matrixStack.scale(cameraScale, cameraScale, 1.0f);
        PerkTree tree = this.perkTree;
        List<Identifier> playerGainedPerk = PerkUtils.getPlayerPerks(this.client.player, tree.getID());
        Vector2i vMousePos = getVirtualMousePos(mouseX, mouseY);
        for (PerkTree.PerkNode perkNode : tree.getAllNodes()) {
            this.drawNode(context, perkNode, playerGainedPerk, vMousePos.x, vMousePos.y, delta);
        }
        matrixStack.pop();
        context.disableScissor();
    }

    // UNTESTED
    public Vector2i getVirtualMousePos(int mouseX, int mouseY) {
        float relX = (mouseX - cameraCenter.x - cameraPosX) / cameraScale - nodeCenter.x;
        float relY = (mouseY - cameraCenter.y - cameraPosY) / cameraScale - nodeCenter.y;
        return new Vector2i((int) relX, (int) relY);
    }

    public @Nullable PerkTree.PerkNode getMouseNode(int mouseX, int mouseY) {
        for (PerkTree.PerkNode perkNode : this.perkTree.getAllNodes()) {
            int centerX = nodeBaseX + posXPerTier * perkNode.tier;
            int centerY = perkNode.y;
            int left = centerX + NodeSelectStartX;
            int top = centerY + NodeSelectStartY;
            if (mouseX >= left && mouseX < left + NodeSelectRectWidth && mouseY >= top && mouseY < top + NodeSelectRectHeight) {
                return perkNode;
            }
        }
        return null;
    }

    public void NodeScreenMouseClickHandler(int mouseX, int mouseY, int mode) {
        if (mouseX < nodeWindowX || mouseX >= nodeWindowX + nodeWindowWidth || mouseY < nodeWindowY || mouseY >= nodeWindowY + nodeWindowHeight) {
            return;
        }
        Vector2i trueMousePos = getVirtualMousePos(mouseX, mouseY);
        @Nullable PerkTree.PerkNode node = getMouseNode(trueMousePos.x, trueMousePos.y);
        this.nowSelectNode = node;
        this.onNodeSelect();
    }

    public void onNodeSelect() {
        try {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Node Selected: " + this.nowSelectNode.perkID.toString()), false);
        } catch (Exception e) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("No Node Selected"), false);
        }
    }
}
