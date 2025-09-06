package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.data.CodexData;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class BookOfShapeShifterScreenV2_P2 extends Screen {
    private static final Identifier page_texID = new Identifier(MOD_ID,"textures/gui/codex_page_2.png");
    public PlayerEntity currentPlayer;
    public static final int BookSizeX = 350;
    public static final int BookSizeY = 220;

    public BookOfShapeShifterScreenV2_P2() {
        super(Text.of("ShapeShifterCurse_Book_Screen_V2"));
    }

    @Override
    public void init() {
        // 这字够大了, 不用添加展开按钮了
        int BookPosX = width / 2 - BookSizeX / 2;
        int BookPosY = height / 2 - BookSizeY / 2;
        int DefaultTextColor = 0xFFFFFF;  // 这里的颜色属于乘法模式 (float)(R1*R2,G1*G2,B1*B2) 需要在lang中修改
        // Pros
        // Size -> (83, 181) Pos -> (13, 26)
        this.addDrawableChild(new TextWidget(BookPosX + 26, BookPosY + 10, 53, 11, CodexData.headerPros, textRenderer).setTextColor(DefaultTextColor));
        MultilineTextWidget Pros = new MultilineTextWidget(BookPosX + 13, BookPosY + 26, CodexData.getContentText(CodexData.ContentType.PROS, currentPlayer), textRenderer).setMaxWidth(83).setTextColor(DefaultTextColor);
        this.addDrawableChild(Pros);
        // Cons
        // Size -> (82, 182) Pos -> (110, 26)
        this.addDrawableChild(new TextWidget(BookPosX + 120, BookPosY + 10, 63, 11, CodexData.headerCons, textRenderer).setTextColor(DefaultTextColor));
        MultilineTextWidget Cons = new MultilineTextWidget(BookPosX + 110, BookPosY + 26, CodexData.getContentText(CodexData.ContentType.CONS, currentPlayer), textRenderer).setMaxWidth(82).setTextColor(DefaultTextColor);
        this.addDrawableChild(Cons);
        // Instincts
        // Size -> (106, 136) Pos -> (220, 24)
        this.addDrawableChild(new TextWidget(BookPosX + 242, BookPosY + 10, 63, 12, CodexData.headerInstincts, textRenderer).setTextColor(DefaultTextColor));
        MultilineTextWidget Instincts = new MultilineTextWidget(BookPosX + 220, BookPosY + 24, CodexData.getContentText(CodexData.ContentType.INSTINCTS, currentPlayer), textRenderer).setMaxWidth(106).setTextColor(DefaultTextColor);
        this.addDrawableChild(Instincts);
        // 下一页按钮
        int NextPage_ButtonSizeX = 15;
        int NextPage_ButtonSizeY = 30;
        int NextPage_ButtonPosX = width / 2 + BookSizeX / 2 - 18;
        int NextPage_ButtonPosY = height / 2 - NextPage_ButtonSizeY / 2;
        this.addDrawableChild(
                ButtonWidget.builder(Text.of(">"), button -> NextPage()).size(NextPage_ButtonSizeX, NextPage_ButtonSizeY).position(NextPage_ButtonPosX, NextPage_ButtonPosY).build()
        );
    }

    private void RenderBook(DrawContext context) {
        int BookPosX = width / 2 - BookSizeX / 2;
        int BookPosY = height / 2 - BookSizeY / 2;
        context.drawTexture(page_texID, BookPosX, BookPosY, 0, 0, BookSizeX, BookSizeY, BookSizeX, BookSizeY);
    }

    private void NextPage() {
        BookOfShapeShifterScreenV2_P1 NextPage = new BookOfShapeShifterScreenV2_P1();
        NextPage.currentPlayer = currentPlayer;
        MinecraftClient.getInstance().setScreen(NextPage);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.RenderBook(context);
        super.render(context, mouseX, mouseY, delta);
    }
}
