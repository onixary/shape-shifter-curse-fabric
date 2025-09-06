package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import org.joml.Quaternionf;

import java.util.OptionalInt;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class BookOfShapeShifterScreenV2_P1 extends Screen {
    private static final Identifier page_texID = new Identifier(MOD_ID,"textures/gui/codex_page_1.png");
    public PlayerEntity currentPlayer;
    public static final int BookSizeX = 350;
    public static final int BookSizeY = 220;

    public BookOfShapeShifterScreenV2_P1() {
        super(Text.of("ShapeShifterCurse_Book_Screen_V2"));
    }

    @Override
    public void init() {
        // 这字够大了, 不用添加展开按钮了
        int BookPosX = width / 2 - BookSizeX / 2;
        int BookPosY = height / 2 - BookSizeY / 2;
        int DefaultTextColor = 0xFFFFFF;  // 这里的颜色属于乘法模式 (float)(R1*R2,G1*G2,B1*B2) 需要在lang中修改
        // Title
        // Size -> (108, 48) Pos -> (17, 92)
        MultilineTextWidget TitleLabel = new MultilineTextWidget(BookPosX + 17, BookPosY + 92, CodexData.getContentText(CodexData.ContentType.TITLE, currentPlayer), textRenderer).setMaxWidth(108).setTextColor(DefaultTextColor);
        this.addDrawableChild(TitleLabel);
        // Status
        // Size -> (107, 56) Pos -> (17, 153)
        this.addDrawableChild(new TextWidget(BookPosX + 17, BookPosY + 153 - 10, 107, 8, CodexData.headerStatus, textRenderer).setTextColor(DefaultTextColor));
        MultilineTextWidget StatusLabel = new MultilineTextWidget(BookPosX + 17, BookPosY + 153, CodexData.getPlayerStatusText(currentPlayer), textRenderer).setMaxWidth(107).setTextColor(DefaultTextColor);
        this.addDrawableChild(StatusLabel);
        // Appearance
        // Size -> (176, 184) Pos -> (142, 23)
        this.addDrawableChild(new TextWidget(BookPosX + 142, BookPosY + 23 - 10, 176, 8, CodexData.headerAppearance, textRenderer).setTextColor(DefaultTextColor));
        MultilineTextWidget AppearanceLabel = new MultilineTextWidget(BookPosX + 142, BookPosY + 23, CodexData.getContentText(CodexData.ContentType.APPEARANCE, currentPlayer), textRenderer).setMaxWidth(176).setTextColor(DefaultTextColor);
        this.addDrawableChild(AppearanceLabel);
        // 下一页按钮
        int NextPage_ButtonSizeX = 15;
        int NextPage_ButtonSizeY = 30;
        int NextPage_ButtonPosX = width / 2 + BookSizeX / 2 - 18;
        int NextPage_ButtonPosY = height / 2 - NextPage_ButtonSizeY / 2;
        this.addDrawableChild(
                ButtonWidget.builder(Text.of(">"), button -> NextPage()).size(NextPage_ButtonSizeX, NextPage_ButtonSizeY).position(NextPage_ButtonPosX, NextPage_ButtonPosY).build()
        );
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
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        InventoryScreen.drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    private void RenderBook(DrawContext context) {
        int BookPosX = width / 2 - BookSizeX / 2;
        int BookPosY = height / 2 - BookSizeY / 2;
        context.drawTexture(page_texID, BookPosX, BookPosY, 0, 0, BookSizeX, BookSizeY, BookSizeX, BookSizeY);
    }

    private void NextPage() {
        BookOfShapeShifterScreenV2_P2 NextPage = new BookOfShapeShifterScreenV2_P2();
        NextPage.currentPlayer = currentPlayer;
        MinecraftClient.getInstance().setScreen(NextPage);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int BookPosX = width / 2 - BookSizeX / 2;
        int BookPosY = height / 2 - BookSizeY / 2;
        this.RenderBook(context);
        // 实体渲染原点为实体中心脚下
        // Size -> (70, 66) Pos -> (35, 15)
        int PlayerX = BookPosX + 70;
        int PlayerY = BookPosY + 70;
        this.RenderEntity(context, PlayerX, PlayerY, 30, PlayerX - mouseX, PlayerY - 37 - mouseY, currentPlayer);
        super.render(context, mouseX, mouseY, delta);
    }
}
