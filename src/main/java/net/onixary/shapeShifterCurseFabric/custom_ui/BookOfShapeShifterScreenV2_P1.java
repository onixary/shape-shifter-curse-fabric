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
        int BookScale = 1;
        float Scale = 0.5f;
        if (ShapeShifterCurseFabric.clientConfig.newStartBookForBiggerScreen) {
            BookScale = 2;
            Scale *= BookScale;
        }
        int BookPosX = width / 2 - (BookSizeX * BookScale) / 2;
        int BookPosY = height / 2 - (BookSizeY * BookScale) / 2;
        int DefaultTextColor = 0xFFFFFF;  // 这里的颜色属于乘法模式 (float)(R1*R2,G1*G2,B1*B2) 需要在lang中修改
        ScaleTextRenderer scaleTextRenderer = new ScaleTextRenderer(textRenderer);
        scaleTextRenderer.Scale = Scale;
        // Title
        // Size -> (108, 48) Pos -> (17, 92)
        MultilineTextWidget TitleLabel = new ScaleMultilineTextWidget(BookPosX + 17 * BookScale, BookPosY + 92 * BookScale, CodexData.getContentText(CodexData.ContentType.TITLE, currentPlayer), scaleTextRenderer, Scale).setMaxWidth(108 * BookScale).setTextColor(DefaultTextColor);
        this.addDrawableChild(TitleLabel);
        // Status
        // Size -> (107, 56) Pos -> (17, 153)
        this.addDrawableChild(new TextWidget(BookPosX + 17 * BookScale, BookPosY + 143 * BookScale, 107 * BookScale, 8 * BookScale, CodexData.headerStatus, textRenderer).setTextColor(DefaultTextColor));
        MultilineTextWidget StatusLabel = new ScaleMultilineTextWidget(BookPosX + 17 * BookScale, BookPosY + 153 * BookScale, CodexData.getPlayerStatusText(currentPlayer), scaleTextRenderer, Scale).setMaxWidth(107 * BookScale).setTextColor(DefaultTextColor);
        this.addDrawableChild(StatusLabel);
        // Appearance
        // Size -> (176, 184) Pos -> (142, 23)
        this.addDrawableChild(new TextWidget(BookPosX + 142 * BookScale, BookPosY + 11 * BookScale, 176 * BookScale, 8 * BookScale, CodexData.headerAppearance, textRenderer).setTextColor(DefaultTextColor));
        MultilineTextWidget AppearanceLabel = new ScaleMultilineTextWidget(BookPosX + 142 * BookScale, BookPosY + 23 * BookScale, CodexData.getContentText(CodexData.ContentType.APPEARANCE, currentPlayer), scaleTextRenderer, Scale).setMaxWidth(176 * BookScale).setTextColor(DefaultTextColor);
        this.addDrawableChild(AppearanceLabel);
        // 下一页按钮
        int NextPage_ButtonSizeX = 15 * BookScale;
        int NextPage_ButtonSizeY = 30 * BookScale;
        int NextPage_ButtonPosX = width / 2 + (BookSizeX * BookScale) / 2 - 18 * BookScale;
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
        int FinalBookSizeX = BookSizeX;
        int FinalBookSizeY = BookSizeY;
        if (ShapeShifterCurseFabric.clientConfig.newStartBookForBiggerScreen) {
            FinalBookSizeX = (BookSizeX * 2);
            FinalBookSizeY = (BookSizeY * 2);
        }
        int BookPosX = width / 2 - FinalBookSizeX / 2;
        int BookPosY = height / 2 - FinalBookSizeY / 2;
        context.drawTexture(page_texID, BookPosX, BookPosY, 0, 0, FinalBookSizeX, FinalBookSizeY, FinalBookSizeX, FinalBookSizeY);
    }

    private void NextPage() {
        BookOfShapeShifterScreenV2_P2 NextPage = new BookOfShapeShifterScreenV2_P2();
        NextPage.currentPlayer = currentPlayer;
        MinecraftClient.getInstance().setScreen(NextPage);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int BookScale = 1;
        int FinalBookSizeX = BookSizeX;
        int FinalBookSizeY = BookSizeY;
        if (ShapeShifterCurseFabric.clientConfig.newStartBookForBiggerScreen) {
            BookScale *= 2;
            FinalBookSizeX = (BookSizeX * BookScale);
            FinalBookSizeY = (BookSizeY * BookScale);
        }
        int BookPosX = width / 2 - FinalBookSizeX / 2;
        int BookPosY = height / 2 - FinalBookSizeY / 2;
        this.RenderBook(context);
        // 实体渲染原点为实体中心脚下
        // Size -> (70, 66) Pos -> (35, 15)
        int PlayerX = BookPosX + 70 * BookScale;
        int PlayerY = BookPosY + 75 * BookScale;
        this.RenderEntity(context, PlayerX, PlayerY, 30 * BookScale, PlayerX - mouseX, PlayerY - 37 * BookScale - mouseY, currentPlayer);
        super.render(context, mouseX, mouseY, delta);
    }
}
