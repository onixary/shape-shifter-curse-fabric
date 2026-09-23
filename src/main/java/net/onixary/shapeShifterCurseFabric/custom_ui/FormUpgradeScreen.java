package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleScrollTextWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.WidgetEXUtils;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.perk.PerkTree;
import net.onixary.shapeShifterCurseFabric.perk.PerkUtils;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

// 标记 UNTESTED 代表这个函数没测试 测试完了就删(估计最后得有一堆没测试函数 还是标一下大概率炸的函数吧)

public class FormUpgradeScreen extends Screen implements WidgetEXUtils.IWidgetEX {
    public static final Identifier TEXTURE = ShapeShifterCurseFabric.identifier("textures/gui/shape_shifter_tuner_ui.png");
    public static final int TEXTURE_WIDTH = 420;
    public static final int TEXTURE_HEIGHT = 190;

    public static final int BACKGROUND_WIDTH = 420;
    public static final int BACKGROUND_HEIGHT = 190;

    public static final int FORM_MODEL_REVIEW_X = 8;
    public static final int FORM_MODEL_REVIEW_Y = 30;
    public static final int FORM_MODEL_REVIEW_WIDTH = 100;
    public static final int FORM_MODEL_REVIEW_HEIGHT = 130;

    public static final int PERK_UI_X = 110;
    public static final int PERK_UI_Y = 8;
    public static final int PERK_UI_WIDTH = 200;
    public static final int PERK_UI_HEIGHT = 174;

    public static final int PERK_INFO_UI_X = 312;
    public static final int PERK_INFO_UI_Y = 8;
    public static final int PERK_INFO_UI_WIDTH = 99;
    public static final int PERK_INFO_UI_HEIGHT = 174;

    public static final int PERK_INFO_NAME_X = 316;
    public static final int PERK_INFO_NAME_Y = 12;
    public static final int PERK_INFO_NAME_WIDTH = 91;
    public static final int PERK_INFO_NAME_HEIGHT = 14;

    public static final int PERK_INFO_DESC_X = 316;
    public static final int PERK_INFO_DESC_Y = 28;
    public static final int PERK_INFO_DESC_WIDTH = 91;
    public static final int PERK_INFO_DESC_HEIGHT = 115;

    public static final int PERK_INFO_XP_ICON_X = 316;
    public static final int PERK_INFO_XP_ICON_Y = 145;
    public static final int PERK_INFO_XP_ICON_WIDTH = 16;
    public static final int PERK_INFO_XP_ICON_HEIGHT = 17;

    public static final int PERK_INFO_XP_COST_X = 334;
    public static final int PERK_INFO_XP_COST_Y = 145;
    public static final int PERK_INFO_XP_COST_WIDTH = 73;
    public static final int PERK_INFO_XP_COST_HEIGHT = 17;

    public static final int PERK_INFO_GAIN_BUTTON_X = 316;
    public static final int PERK_INFO_GAIN_BUTTON_Y = 164;
    public static final int PERK_INFO_GAIN_BUTTON_WIDTH = 91;
    public static final int PERK_INFO_GAIN_BUTTON_HEIGHT = 14;

    public int baseX = 0;
    public int baseY = 0;

    public static final Identifier LABEL_GAINED = ShapeShifterCurseFabric.identifier("textures/perk/system/gained.png");
    public static final Identifier LABEL_SELECT = ShapeShifterCurseFabric.identifier("textures/perk/system/select.png");
    public static final Identifier LABEL_SELECTED = ShapeShifterCurseFabric.identifier("textures/perk/system/selected.png");
    public static final Identifier LABEL_CAN_NOT_GAIN = ShapeShifterCurseFabric.identifier("textures/perk/system/can_not_gain.png");
    public static final Identifier LABEL_DEPEND = ShapeShifterCurseFabric.identifier("textures/perk/system/depend.png");

    public static final HashMap<Identifier, Boolean> perkAvailableMap = new HashMap<>();  // 仅客户端数据 仅影响渲染 仅代表服务器获取这个表时无法获取这个Perk
    public static final HashMap<Identifier, Integer> perkXpCostMap = new HashMap<>();  // 仅客户端数据 实际消耗由服务器决定

    public int tier = -1;
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

    // 基础渲染原点(左上) -> cameraCenter(中心) -> nodeCenter(左中)
    public Vector2i cameraCenter = new Vector2i(0, 0);
    public Vector2i nodeCenter = new Vector2i( 0, 0);

    public static final int nodeBaseX = 25;
    public static final int posXPerTier = 50;
    public static final int nodeLineRootXOffset = 11;
    public static final int nodeLineDependXOffset = -10;
    public static final int LineColor = 0xFF9F9F9F;

    public static final int NodeDrawStartX = -7;
    public static final int NodeDrawStartY = -7;
    public static final int NodeTextureWidth = 16;
    public static final int NodeTextureHeight = 16;

    public static final int NodeSelectStartX = -8;
    public static final int NodeSelectStartY = -8;
    public static final int NodeSelectRectWidth = 18;
    public static final int NodeSelectRectHeight = 18;

    // Widgets
    public TextWidget PerkNameWidget;
    public ScaleScrollTextWidget PerkDescWidget;
    public ButtonWidget AcquirePerkButton;

    @Override
    public WidgetEXUtils.WidgetRect getRect() {
        return null;
    }

    public List<WidgetEXUtils.IWidgetEX> WidgetList = new ArrayList<>();

    @Override
    public List<WidgetEXUtils.IWidgetEX> getWidgetList() {
        return this.WidgetList;
    }

    public FormUpgradeScreen(int tier, Text title, @Nullable PerkTree perkTree) {
        super(title);
        this.tier = tier;
        this.perkTree = perkTree != null ? perkTree : Objects.requireNonNull(RegPerks.getPerkTree(RegPerks.EMPTY_PERK_TREE));
        ModPacketsS2C.sendRequestPerkAvailability();
        ModPacketsS2C.sendRequestPerkData();
    }

    @Override
    public void init() {
        baseX = this.width / 2 - BACKGROUND_WIDTH / 2;
        baseY = this.height / 2 - BACKGROUND_HEIGHT / 2;
        this.PerkNameWidget = new TextWidget(baseX + PERK_INFO_NAME_X, baseY + PERK_INFO_NAME_Y, PERK_INFO_NAME_WIDTH, PERK_INFO_NAME_HEIGHT, Text.literal(""), this.textRenderer);
        this.PerkDescWidget = new ScaleScrollTextWidget(baseX + PERK_INFO_DESC_X, baseY + PERK_INFO_DESC_Y, PERK_INFO_DESC_WIDTH, PERK_INFO_DESC_HEIGHT, 1.0f, Text.literal(""), this.textRenderer);
        this.PerkDescWidget.setEnableScrollableIconRender(true);
        this.WidgetList.add(this.PerkDescWidget);
        this.AcquirePerkButton = ButtonWidget.builder(Text.literal("GET"), button -> {
            if (this.nowSelectNode != null) {
                PerkUtils.addPerk(MinecraftClient.getInstance().player, this.perkTree.getID(), this.nowSelectNode.perkID);
                ModPacketsS2C.sendRequestPerkAvailability();
            }
        }).position(baseX + PERK_INFO_GAIN_BUTTON_X, baseY + PERK_INFO_GAIN_BUTTON_Y).size(PERK_INFO_GAIN_BUTTON_WIDTH, PERK_INFO_GAIN_BUTTON_HEIGHT).build();
        this.addDrawableChild(this.PerkNameWidget);
        this.addDrawableChild(this.PerkDescWidget);
        this.addDrawableChild(this.AcquirePerkButton);
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
        baseX = this.width / 2 - BACKGROUND_WIDTH / 2;
        baseY = this.height / 2 - BACKGROUND_HEIGHT / 2;
        context.drawTexture(TEXTURE, baseX, baseY, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        nodeWindowX = baseX + PERK_UI_X;
        nodeWindowY = baseY + PERK_UI_Y;
        cameraCenter = new Vector2i(nodeWindowX + PERK_UI_WIDTH / 2, nodeWindowY + PERK_UI_HEIGHT / 2);
        nodeCenter = new Vector2i( -PERK_UI_WIDTH / 2, 0);
        this.drawAllNode(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return false;
    }

    // Utils

    public void drawConnectLine(DrawContext context, PerkTree.PerkNode perkNode) {
        List<Identifier> depends = perkNode.dependentPerkIDs;
        if (depends.isEmpty()) return;
        for (Identifier depend : depends) {
            PerkTree.PerkNode dependNodeMetaData = perkTree.getNode(depend);
            if (dependNodeMetaData == null) return;
            int ox = nodeCenter.x;
            int oy = nodeCenter.y;
            int x1 = nodeBaseX + posXPerTier * perkNode.tier + nodeLineDependXOffset;
            int x2 = nodeBaseX + posXPerTier * dependNodeMetaData.tier + nodeLineRootXOffset;
            int y1 = perkNode.y;
            int y2 = dependNodeMetaData.y;
            if (perkNode.tier - 1 == dependNodeMetaData.tier) {
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
            } else {
                // AI整的虚线 看起来应该没有对应的API了 所以尽量别整需要虚线的Perk 这种比较费性能 除非使用贴图 但是这种不太好改
                int dashLen = posXPerTier / 2 - 10;
                int dashSize = 2;
                int gapSize = 1;
                int lastPixelX = x1;
                for (int i = 0; i < dashLen; i += dashSize + gapSize) {
                    int to = Math.min(i + dashSize, dashLen);
                    if (i >= to) break;
                    context.fill(
                            ox + x1 - to, oy + y1,
                            ox + x1 - i, oy + y1 + 1,
                            LineColor);
                    lastPixelX = x1 - to;
                }
                context.fill(ox + lastPixelX - 2, oy + y1 - 1, ox + lastPixelX - 1, oy + y1 + 2, LineColor);
            }
        }
    }

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
        } else if (!perkAvailableMap.getOrDefault(perkNode.perkID, true)) {
            context.drawTexture(LABEL_CAN_NOT_GAIN, NodePosX - 9, NodePosY - 9, 0, 0, 20, 20, 20, 20);
        }

        if (this.nowSelectNode != null) {
            if (perkNode == this.nowSelectNode) {
                context.drawTexture(LABEL_SELECTED, NodePosX - 9, NodePosY - 9, 0, 0, 20, 20, 20, 20);
            } else if (this.nowSelectNode.dependentPerkIDs.contains(perkNode.perkID)) {
                context.drawTexture(LABEL_DEPEND, NodePosX - 9, NodePosY - 9, 0, 0, 20, 20, 20, 20);
            }
        }
        if (mouseX >= left && mouseX < left + NodeSelectRectWidth && mouseY >= top && mouseY < top + NodeSelectRectHeight) {
            context.drawTexture(LABEL_SELECT, NodePosX - 9, NodePosY - 9, 0, 0, 20, 20, 20, 20);
        }
        context.drawTexture(icon, NodePosX + NodeDrawStartX, NodePosY + NodeDrawStartY, 0, 0, NodeTextureWidth, NodeTextureHeight, NodeTextureWidth, NodeTextureHeight);
    }

    public void drawAllNode(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client == null) return;
        context.enableScissor(nodeWindowX, nodeWindowY, nodeWindowX + PERK_UI_WIDTH, nodeWindowY + PERK_UI_HEIGHT);
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
        if (mouseX < nodeWindowX || mouseX >= nodeWindowX + PERK_UI_WIDTH || mouseY < nodeWindowY || mouseY >= nodeWindowY + PERK_UI_HEIGHT) {
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
        if (mouseX < nodeWindowX || mouseX >= nodeWindowX + PERK_UI_WIDTH || mouseY < nodeWindowY || mouseY >= nodeWindowY + PERK_UI_HEIGHT) {
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
        if (mouseX < nodeWindowX || mouseX >= nodeWindowX + PERK_UI_WIDTH
                || mouseY < nodeWindowY || mouseY >= nodeWindowY + PERK_UI_HEIGHT) {
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

    public boolean isNowPerkCanGain() {
        if (this.nowSelectNode == null) {
            return false;
        }
        if (this.nowSelectNode.tier > this.tier) {
            return false;
        }
        int requireXp = this.client.player.getAbilities().creativeMode ? 0 : perkXpCostMap.getOrDefault(this.nowSelectNode.perkID, 0);
        if (this.client.player.totalExperience < requireXp) {
            return false;
        }
        return true;
    }

    public void onNodeSelect() {
        try {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Node Selected: " + this.nowSelectNode.perkID.toString()), false);
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Can Gained (Cache): " + this.perkAvailableMap.getOrDefault(this.nowSelectNode.perkID, true)), false);
        } catch (Exception e) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("No Node Selected"), false);
        }
        if (this.nowSelectNode != null) {
            this.PerkNameWidget.setMessage(RegPerks.getPerkName(this.nowSelectNode.perkID));
            this.PerkDescWidget.reloadText(RegPerks.getPerkDescription(this.nowSelectNode.perkID));
            this.AcquirePerkButton.active = this.isNowPerkCanGain();
        } else {
            this.PerkNameWidget.setMessage(Text.literal(""));
            this.PerkDescWidget.reloadText(Text.literal(""));
            this.AcquirePerkButton.active = false;
        }
        ModPacketsS2C.sendRequestPerkAvailability();
    }
}
