package net.onixary.shapeShifterCurseFabric.custom_ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleScrollTextWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.WidgetEXUtils;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.perk.PerkTree;
import net.onixary.shapeShifterCurseFabric.perk.PerkUtils;
import net.onixary.shapeShifterCurseFabric.perk.RegPerks;
import net.onixary.shapeShifterCurseFabric.util.util.BaseSprite;
import net.onixary.shapeShifterCurseFabric.util.util.ISprite;
import net.onixary.shapeShifterCurseFabric.util.util.cost.BaseCost;
import net.onixary.shapeShifterCurseFabric.util.util.cost.ICost;
import net.onixary.shapeShifterCurseFabric.util.util.cost.IFUSDrawableCostType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

// 标记 UNTESTED 代表这个函数没测试 测试完了就删(估计最后得有一堆没测试函数 还是标一下大概率炸的函数吧)

public class FormUpgradeScreen extends Screen implements WidgetEXUtils.IWidgetEX {
    public static final Identifier TEXTURE = ShapeShifterCurseFabric.identifier("textures/gui/shape_shifter_tuner_ui.png");
    public static final HashMap<Integer, ISprite> levelSprites = new HashMap<>();
    public static final int TEXTURE_WIDTH = 454;
    public static final int TEXTURE_HEIGHT = 190;

    static {
        levelSprites.put(1, new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 420, 0, 11, 11));
        levelSprites.put(2, new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 420, 11, 11, 11));
        levelSprites.put(3, new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 420, 22, 11, 11));
        levelSprites.put(4, new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 420, 33, 11, 11));
        levelSprites.put(5, new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 420, 44, 11, 11));
        levelSprites.put(6, new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 420, 55, 11, 11));
        levelSprites.put(7, new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 420, 66, 11, 11));
        levelSprites.put(8, new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 420, 77, 11, 11));
        levelSprites.put(9, new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 420, 88, 11, 11));
        levelSprites.put(10, new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 420, 99, 11, 11));
    }

    public static final int BACKGROUND_WIDTH = 420;
    public static final int BACKGROUND_HEIGHT = 190;

    public static final int FORM_MODEL_REVIEW_X = 8;
    public static final int FORM_MODEL_REVIEW_Y = 30;
    public static final int FORM_MODEL_REVIEW_WIDTH = 100;
    public static final int FORM_MODEL_REVIEW_HEIGHT = 130;

    public static final int PERK_UI_X = 110;
    public static final int PERK_UI_Y = 8;
    public static final int PERK_UI_VIEW_Y = 20;
    public static final int PERK_UI_WIDTH = 200;
    public static final int PERK_UI_HEIGHT = 174;

    public static final int PERK_UI_ICON_X = 123;
    public static final int PERK_UI_ICON_Y = 10;
    public static final int PERK_UI_ICON_WIDTH = 17;
    public static final int PERK_UI_ICON_HEIGHT = 17;

    public static final int LEVEL_ICON_Y = 5;  // 以摄像机中心计算
    public static final int LEVEL_ICON_DRAW_X = -5;
    public static final int LEVEL_ICON_WIDTH = 11;
    public static final int LEVEL_ICON_HEIGHT = 11;

    public static final int PERK_INFO_NAME_X = 316;
    public static final int PERK_INFO_NAME_Y = 12;
    public static final int PERK_INFO_NAME_WIDTH = 91;
    public static final int PERK_INFO_NAME_HEIGHT = 14;

    public static final int PERK_INFO_DESC_X = 316;
    public static final int PERK_INFO_DESC_Y = 28;
    public static final int PERK_INFO_DESC_WIDTH = 91;
    public static final int PERK_INFO_DESC_HEIGHT = 115;

    public static final int PERK_INFO_COST_ICON_X = 315;
    public static final int PERK_INFO_COST_ICON_Y = 144;
    public static final int PERK_INFO_COST_ICON_WIDTH = 18;
    public static final int PERK_INFO_COST_ICON_HEIGHT = 18;

    public static final int PERK_INFO_COST_AMOUNT_X = 334;
    public static final int PERK_INFO_COST_AMOUNT_Y = 145;
    public static final int PERK_INFO_COST_AMOUNT_WIDTH = 73;
    public static final int PERK_INFO_COST_AMOUNT_HEIGHT = 17;

    public static final int PERK_INFO_GAIN_BUTTON_X = 316;
    public static final int PERK_INFO_GAIN_BUTTON_Y = 164;
    public static final int PERK_INFO_GAIN_BUTTON_WIDTH = 91;
    public static final int PERK_INFO_GAIN_BUTTON_HEIGHT = 14;

    public int baseX = 0;
    public int baseY = 0;

    public static final ISprite GAINED_SPRITE = new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 434, 112, 20, 20);
    public static final ISprite SELECTED_SPRITE = new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 434, 72, 20, 20);
    public static final ISprite CAN_NOT_GAIN_SPRITE = new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 434, 132, 20, 20);
    public static final ISprite DEPEND_SPRITE = new BaseSprite(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 434, 92, 20, 20);

    public static final HashMap<Identifier, Boolean> perkAvailableMap = new HashMap<>();  // 仅客户端数据 仅影响渲染 仅代表服务器获取这个表时无法获取这个Perk
    public static final HashMap<Identifier, ICost> perkCostMap = new HashMap<>();  // 仅客户端数据 实际消耗由服务器决定
    public static final ICost EMPTY_COST = new BaseCost();

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
    public TextWidget PerkCostAmountWidget;

    public int MaxPerkLevel = 0;

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
        for (PerkTree.PerkNode node : this.perkTree.getAllNodes()) {
            if (node.tier > MaxPerkLevel) {
                MaxPerkLevel = node.tier;
            }
        }
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
        this.PerkCostAmountWidget = new TextWidget(baseX + PERK_INFO_COST_AMOUNT_X, baseY + PERK_INFO_COST_AMOUNT_Y, PERK_INFO_COST_AMOUNT_WIDTH, PERK_INFO_COST_AMOUNT_HEIGHT, Text.literal(""), this.textRenderer);
        this.PerkCostAmountWidget.alignRight();
        this.addDrawableChild(this.PerkNameWidget);
        this.addDrawableChild(this.PerkDescWidget);
        this.addDrawableChild(this.AcquirePerkButton);
        this.addDrawableChild(this.PerkCostAmountWidget);
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

    private void RenderEntity(DrawContext context, int x, int y, int size, int mouseX, int mouseY, LivingEntity entity) {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float g = (float)Math.atan((double)(mouseY / 40.0F));
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        float m = entity.prevBodyYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.prevBodyYaw = entity.bodyYaw;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        InventoryScreen.drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
        entity.bodyYaw = h;
        entity.prevBodyYaw = m;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    private void RenderEntityInViewport(DrawContext context, int viewportX, int viewportY, int viewportWidth, int viewportHeight, int x, int y, int size, int mouseX, int mouseY, LivingEntity entity) {
        context.enableScissor(viewportX, viewportY, viewportX + viewportWidth, viewportY + viewportHeight);
        try {
            RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
            RenderEntity(context, x, y, size, mouseX, mouseY, entity);
        } finally {
            context.disableScissor();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        baseX = this.width / 2 - BACKGROUND_WIDTH / 2;
        baseY = this.height / 2 - BACKGROUND_HEIGHT / 2;
        context.drawTexture(TEXTURE, baseX, baseY, -1, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        nodeWindowX = baseX + PERK_UI_X;
        nodeWindowY = baseY + PERK_UI_Y;
        cameraCenter = new Vector2i(nodeWindowX + PERK_UI_WIDTH / 2, nodeWindowY + PERK_UI_HEIGHT / 2);
        nodeCenter = new Vector2i( -PERK_UI_WIDTH / 2, 0);
        super.render(context, mouseX, mouseY, delta);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.drawTexture(TEXTURE, baseX + PERK_UI_ICON_X, baseY + PERK_UI_ICON_Y, 434, 0, PERK_UI_ICON_WIDTH, PERK_UI_ICON_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        RenderSystem.disableBlend();
        this.drawAllNode(context, mouseX, mouseY, delta);

        if (client.player != null) {
            int viewportX = baseX + FORM_MODEL_REVIEW_X;
            int viewportY = baseY + FORM_MODEL_REVIEW_Y;
            int entityX = viewportX + FORM_MODEL_REVIEW_WIDTH / 2;
            int entityY = viewportY + FORM_MODEL_REVIEW_HEIGHT - 15;
            int entitySize = 50;
            RenderEntityInViewport(
                    context,
                    viewportX, viewportY,
                    FORM_MODEL_REVIEW_WIDTH, FORM_MODEL_REVIEW_HEIGHT,
                    entityX, entityY,
                    entitySize,
                    entityX - mouseX, entityY - mouseY - entitySize,
                    client.player
            );
        }

        if (this.nowSelectNode != null) {
            ICost nowCost = perkCostMap.getOrDefault(this.nowSelectNode.perkID, EMPTY_COST);
            if (nowCost.getType() instanceof IFUSDrawableCostType<?> fusDrawable) {
                int rx = baseX + PERK_INFO_COST_ICON_X;
                int ry = baseY + PERK_INFO_COST_ICON_Y;
                fusDrawable.drawIcon(context, nowCost, client.player, rx, ry, 0);
                fusDrawable.drawOnHover(context, nowCost, client.player, rx, ry, 0, mouseX - rx, mouseY - ry);
            }
        }
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
        // this.drawConnectLine(context, perkNode);
        Identifier icon = RegPerks.getPerkIcon(perkNode.perkID);
        if (icon == null) {
            icon = RegPerks.FALLBACK_PERK_ICON;
        }
        int virtualNodeX = nodeBaseX + posXPerTier * perkNode.tier;
        int virtualNodeY = perkNode.y;
        int NodePosX = nodeCenter.x + virtualNodeX;
        int NodePosY = nodeCenter.y + virtualNodeY;
        // int left = virtualNodeX + NodeSelectStartX;
        // int top = virtualNodeY + NodeSelectStartY;
        if (playerGainedPerk != null && playerGainedPerk.contains(perkNode.perkID)) {
            GAINED_SPRITE.draw(context, NodePosX - 9, NodePosY - 9);
        } else if (!perkAvailableMap.getOrDefault(perkNode.perkID, true)) {
            CAN_NOT_GAIN_SPRITE.draw(context, NodePosX - 9, NodePosY - 9);
        }

        if (this.nowSelectNode != null) {
            if (perkNode == this.nowSelectNode) {
                SELECTED_SPRITE.draw(context, NodePosX - 9, NodePosY - 9);
            } else if (this.nowSelectNode.dependentPerkIDs.contains(perkNode.perkID)) {
                DEPEND_SPRITE.draw(context, NodePosX - 9, NodePosY - 9);
            }
        }
        // if (mouseX >= left && mouseX < left + NodeSelectRectWidth && mouseY >= top && mouseY < top + NodeSelectRectHeight) {
        //     context.drawTexture(LABEL_SELECT, NodePosX - 9, NodePosY - 9, 0, 0, 20, 20, 20, 20);
        // }
        context.drawTexture(icon, NodePosX + NodeDrawStartX, NodePosY + NodeDrawStartY, 0, 0, NodeTextureWidth, NodeTextureHeight, NodeTextureWidth, NodeTextureHeight);
        // final int PerkNameBoxWidth = 33;
        // Text perkNameText = RegPerks.getPerkName(perkNode.perkID);
        // int perkNameTextWidth = this.textRenderer.getWidth(perkNameText);
        // int iconCenterX = NodePosX + NodeDrawStartX + NodeTextureWidth / 2;
        // int perkNameY = NodePosY + NodeDrawStartY + NodeTextureHeight + 2;
        // int perkNameBoxLeftX = iconCenterX - PerkNameBoxWidth / 2;
        // int perkNameX = perkNameBoxLeftX + (PerkNameBoxWidth - perkNameTextWidth) / 2;
        // context.drawText(
        //                 this.textRenderer,
        //         perkNameText,
        //         perkNameX,
        //         perkNameY,
        //         0xFFFFFFFF,
        //         false
        //         );
    }

    public void drawAllNode(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client == null) return;
        context.enableScissor(nodeWindowX, nodeWindowY, nodeWindowX + PERK_UI_WIDTH, nodeWindowY + PERK_UI_HEIGHT);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        MatrixStack matrixStack = context.getMatrices();
        int firstX = nodeBaseX + nodeCenter.x;
        int firstY = nodeWindowY + LEVEL_ICON_Y;
        for (int tierIndex = 1; tierIndex <= this.MaxPerkLevel; tierIndex++) {
            int localLineX = firstX + tierIndex * posXPerTier;
            float iconCenterScreenX =
                    cameraCenter.x + cameraPosX + cameraScale * (localLineX + 1.0f);
            int lineLeftX = Math.round(iconCenterScreenX - 0.5f);
            // context.fill(
            //         lineLeftX, nodeWindowY,
            //         lineLeftX + 1, nodeWindowY + PERK_UI_HEIGHT,
            //         LineColor
            // );
            context.drawTexture(TEXTURE, lineLeftX - 1, nodeWindowY, 431, 0, 3, PERK_UI_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            int screenIconX = lineLeftX - (LEVEL_ICON_WIDTH - 1) / 2;
            ISprite icon = levelSprites.get(tierIndex);
            if (icon != null) {
                icon.draw(context, screenIconX, firstY);
            } else {
                context.fill(
                        screenIconX, firstY,
                        screenIconX + LEVEL_ICON_WIDTH, firstY + LEVEL_ICON_HEIGHT,
                        LineColor
                );
            }
        }
        RenderSystem.disableBlend();
        context.disableScissor();
        context.enableScissor(nodeWindowX, nodeWindowY + PERK_UI_VIEW_Y, nodeWindowX + PERK_UI_WIDTH, nodeWindowY + PERK_UI_HEIGHT);
        matrixStack.push();
        matrixStack.translate(cameraCenter.x + cameraPosX, cameraCenter.y + cameraPosY, 0);
        matrixStack.scale(cameraScale, cameraScale, 1.0f);
        PerkTree tree = this.perkTree;
        List<Identifier> playerGainedPerk = PerkUtils.getPlayerPerks(this.client.player, tree.getID());
        Vector2i vMousePos = getVirtualMousePos(mouseX, mouseY);
        for (PerkTree.PerkNode perkNode : tree.getAllNodes()) {
            this.drawConnectLine(context, perkNode);
        }
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
        List<Identifier> playerGainedPerk = PerkUtils.getPlayerPerks(this.client.player, this.perkTree.getID());
        if (playerGainedPerk != null && playerGainedPerk.contains(this.nowSelectNode.perkID)) {
            return false;
        }
        if (this.nowSelectNode.dependentPerkIDs != null && !this.nowSelectNode.dependentPerkIDs.isEmpty()) {
            if (playerGainedPerk == null) return false;
            for (Identifier dependentPerkID : this.nowSelectNode.dependentPerkIDs) if (!playerGainedPerk.contains(dependentPerkID)) return false;
        }
        ICost cost = perkCostMap.get(this.nowSelectNode.perkID);
        if (cost != null && !cost.getType().canPay_CLIENT(cost, client.player)) {
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
            ICost cost = perkCostMap.get(this.nowSelectNode.perkID);
            if (cost != null && cost.getType() instanceof IFUSDrawableCostType<?> ifusDrawableCostType) {
                this.PerkCostAmountWidget.setMessage(ifusDrawableCostType.getAmountText(cost, client.player));
            } else {
                this.PerkCostAmountWidget.setMessage(Text.literal(""));
            }
            this.AcquirePerkButton.active = this.isNowPerkCanGain();
        } else {
            this.PerkNameWidget.setMessage(Text.literal(""));
            this.PerkDescWidget.reloadText(Text.literal(""));
            this.PerkCostAmountWidget.setMessage(Text.literal(""));
            this.AcquirePerkButton.active = false;
        }
        ModPacketsS2C.sendRequestPerkAvailability();
    }
}
