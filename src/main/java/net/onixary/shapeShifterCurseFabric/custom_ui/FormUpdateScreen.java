package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.WidgetEXUtils;
import net.onixary.shapeShifterCurseFabric.perk.PerkTree;
import net.onixary.shapeShifterCurseFabric.perk.PerkUtils;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 标记 UNTESTED 代表这个函数没测试 测试完了就删(估计最后得有一堆没测试函数 还是标一下大概率炸的函数吧)

public class FormUpdateScreen extends Screen implements WidgetEXUtils.IWidgetEX {
    public static final Identifier LABEL_GAINED = ShapeShifterCurseFabric.identifier("textures/perk/system/gained.png");
    public static final Identifier LABEL_SELECT = ShapeShifterCurseFabric.identifier("textures/perk/system/select.png");

    public boolean isLocked;
    public @NotNull PerkTree perkTree;

    public @Nullable PerkTree.PerkNode nowSelectNode;
    // 中心点:
    // Camera 中心
    // Node 左中
    public int cameraPosX = 0;
    public int cameraPosY = 0;
    public float cameraScale = 1.0f;  // 不一定实现 得看手动鼠标计算位置好不好算

    public int nodeWindowX = 0;
    public int nodeWindowY = 0;
    public static final int nodeWindowWidth = 250;
    public static final int nodeWindowHeight = 200;

    // 基础渲染原点(左上) -> cameraCenter(中心) -> nodeCenter(左中)
    public Vector2i cameraCenter = new Vector2i(0, 0);
    public Vector2i nodeCenter = new Vector2i( 0, 0);

    public static final int nodeBaseX = 25;
    public static final int posXPerTier = 50;
    public static final int nodeLineRootXOffset = 10;
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

    public FormUpdateScreen(Text title, boolean isLocked, @Nullable PerkTree perkTree) {
        super(title);
        this.isLocked = isLocked;
        this.perkTree = perkTree != null ? perkTree : Objects.requireNonNull(RegPerks.getPerkTree(RegPerks.EMPTY_PERK_TREE));
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
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.onReleaseWidget(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.onDragWidget(mouseX, mouseY, button, deltaX, deltaY);
        this.NodeScreenMouseDragHandler((int)mouseX, (int)mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double mouseZ) {
        this.onScrollWidget(mouseX, mouseY, mouseZ);
        this.NodeScreenMouseScrollHandler((int)mouseX, (int)mouseY, mouseZ);
        return super.mouseScrolled(mouseX, mouseY, mouseZ);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        nodeWindowX = this.width / 2 - nodeWindowWidth / 2;
        nodeWindowY = this.height / 2 - nodeWindowHeight / 2;
        cameraCenter = new Vector2i(nodeWindowX + nodeWindowWidth / 2, nodeWindowY + nodeWindowHeight / 2);
        nodeCenter = new Vector2i( -nodeWindowWidth / 2, 0);
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
        int ox = nodeCenter.x;
        int oy = nodeCenter.y;
        int x1 = nodeBaseX + posXPerTier * perkNode.tier + nodeLineDependXOffset;
        int x2 = nodeBaseX + posXPerTier * dependNodeMetaData.tier + nodeLineRootXOffset;
        int y1 = perkNode.y;
        int y2 = dependNodeMetaData.y;
        int halfX = (x1 + x2) / 2;
        context.fill(
                ox + Math.min(x1, halfX), oy + y1,
                ox + Math.max(x1, halfX) + 1, oy + y1 + 1,
                LineColor);
        context.fill(
                ox + halfX, oy + Math.min(y1, y2),
                ox + halfX + 1, oy + Math.max(y1, y2) + 1,
                LineColor);
        context.fill(
                ox + Math.min(x2, halfX), oy + y2,
                ox + Math.max(x2, halfX) + 1, oy + y2 + 1,
                LineColor);
    }

    // UNTESTED
    // playerGainedPerk 由调用方获取 毕竟drawNode调用频繁
    public void drawNode(DrawContext context, PerkTree.PerkNode perkNode, @Nullable List<Identifier> playerGainedPerk, int mouseX, int mouseY, float delta) {
        this.drawConnectLine(context, perkNode);
        Identifier icon = RegPerks.getPerkIcon(perkNode.perkID);
        if (icon == null) {
            icon = RegPerks.FALLBACK_PERK_ICON;
        }
        int virtualNodeX = nodeBaseX + posXPerTier * perkNode.tier;
        int virtualNodeY = perkNode.y;
        int NodePosX = nodeCenter.x + virtualNodeX;
        int NodePosY = nodeCenter.y + virtualNodeY;
        int left = virtualNodeX + NodeSelectStartX;
        int top = virtualNodeY + NodeSelectStartY;
        if (playerGainedPerk != null && playerGainedPerk.contains(perkNode.perkID)) {
            context.drawTexture(LABEL_GAINED, NodePosX - 9, NodePosY - 9, 0, 0, 20, 20, 20, 20);
        }
        if (mouseX >= left && mouseX < left + NodeSelectRectWidth && mouseY >= top && mouseY < top + NodeSelectRectHeight) {
            context.drawTexture(LABEL_SELECT, NodePosX - 9, NodePosY - 9, 0, 0, 20, 20, 20, 20);
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

    public double totalDragX = 0;
    public double totalDragY = 0;

    public void NodeScreenMouseDragHandler(int mouseX, int mouseY, int mode, double deltaX, double deltaY) {
        if (mouseX < nodeWindowX || mouseX >= nodeWindowX + nodeWindowWidth || mouseY < nodeWindowY || mouseY >= nodeWindowY + nodeWindowHeight) {
            return;
        }
        if (mode == 0) {
            totalDragX += deltaX;
            totalDragY += deltaY;
            int dragX = (int) totalDragX;
            int dragY = (int) totalDragY;
            if (dragX != 0 || dragY != 0) {
                cameraPosX += dragX;
                cameraPosY += dragY;
                totalDragX -= dragX;
                totalDragY -= dragY;
            }
        }
    }

    public void NodeScreenMouseScrollHandler(int mouseX, int mouseY, double scroll) {
        if (mouseX < nodeWindowX || mouseX >= nodeWindowX + nodeWindowWidth
                || mouseY < nodeWindowY || mouseY >= nodeWindowY + nodeWindowHeight) {
            return;
        }
        if (scroll == 0) return;
        float oldScale = cameraScale;
        float newScale = oldScale * (float) Math.pow(1.1, scroll);
        newScale = Math.max(0.25f, Math.min(4.0f, newScale));
        if (newScale == oldScale) return;
        float worldX = (mouseX - cameraCenter.x - cameraPosX) / oldScale;
        float worldY = (mouseY - cameraCenter.y - cameraPosY) / oldScale;
        cameraPosX = (int) (mouseX - cameraCenter.x - worldX * newScale);
        cameraPosY = (int) (mouseY - cameraCenter.y - worldY * newScale);
        cameraScale = newScale;
    }

    public void onNodeSelect() {
        try {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Node Selected: " + this.nowSelectNode.perkID.toString()), false);
        } catch (Exception e) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("No Node Selected"), false);
        }
        if (this.nowSelectNode != null) {
            PerkUtils.addPerk(MinecraftClient.getInstance().player, this.perkTree.getID(), this.nowSelectNode.perkID);
        }
    }
}
