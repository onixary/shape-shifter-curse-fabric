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
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
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
        float Scale = 0.5f;
        int BookScale = 1;
        if (ShapeShifterCurseFabric.clientConfig.newStartBookForBiggerScreen) {
            BookScale = 2;
            Scale *= BookScale;
        }
        int BookPosX = width / 2 - (BookSizeX * BookScale) / 2;
        int BookPosY = height / 2 - (BookSizeY * BookScale) / 2;
        int DefaultTextColor = 0x222222;   // 这里的颜色属于乘法模式 (float)(R1*R2,G1*G2,B1*B2) 需要在lang中修改
        int HeaderTextColor = 0xDDDDDD;
        ScaleTextRenderer scaleTextRenderer = new ScaleTextRenderer(textRenderer);
        scaleTextRenderer.Scale = Scale;
        // Pros
        // D -> (8, 8), (81, 13)
        // Size -> (83, 181) Pos -> (13, 26)
        this.addDrawableChild(BuildDetailScreenButton(81, 13, 8, 8, CodexData.getContentText(CodexData.ContentType.PROS, currentPlayer)));
        this.addDrawableChild(new TextWidget(BookPosX + 26 * BookScale, BookPosY + 10 * BookScale, 53 * BookScale, 11 * BookScale, CodexData.headerPros, textRenderer).setTextColor(HeaderTextColor));
        MultilineTextWidget Pros = new ScaleMultilineTextWidget(BookPosX + 13 * BookScale, BookPosY + 26 * BookScale, CodexData.getContentText(CodexData.ContentType.PROS, currentPlayer), scaleTextRenderer, Scale).shadow(false).setMaxWidth(83 * BookScale).setTextColor(DefaultTextColor);
        this.addDrawableChild(Pros);
        // Cons
        // D -> (8, 8), (186, 13)
        // Size -> (82, 182) Pos -> (110, 26)
        this.addDrawableChild(BuildDetailScreenButton(186, 13, 8, 8, CodexData.getContentText(CodexData.ContentType.CONS, currentPlayer)));
        this.addDrawableChild(new TextWidget(BookPosX + 120 * BookScale, BookPosY + 10 * BookScale, 63 * BookScale, 11 * BookScale, CodexData.headerCons, textRenderer).setTextColor(HeaderTextColor));
        MultilineTextWidget Cons = new ScaleMultilineTextWidget(BookPosX + 110 * BookScale, BookPosY + 26 * BookScale, CodexData.getContentText(CodexData.ContentType.CONS, currentPlayer), scaleTextRenderer, Scale).shadow(false).setMaxWidth(82 * BookScale).setTextColor(DefaultTextColor);
        this.addDrawableChild(Cons);
        // Instincts
        // D -> (8, 8), (309, 14)
        // Size -> (106, 136) Pos -> (220, 24)
        this.addDrawableChild(BuildDetailScreenButton(309, 14, 8, 8, CodexData.getContentText(CodexData.ContentType.INSTINCTS, currentPlayer)));
        this.addDrawableChild(new TextWidget(BookPosX + 242 * BookScale, BookPosY + 10 * BookScale, 63 * BookScale, 12 * BookScale, CodexData.headerInstincts, textRenderer).setTextColor(HeaderTextColor));
        MultilineTextWidget Instincts = new ScaleMultilineTextWidget(BookPosX + 220 * BookScale, BookPosY + 24 * BookScale, CodexData.getContentText(CodexData.ContentType.INSTINCTS, currentPlayer), scaleTextRenderer, Scale).shadow(false).setMaxWidth(106 * BookScale).setTextColor(DefaultTextColor);
        this.addDrawableChild(Instincts);
        // 下一页按钮
        int NextPage_ButtonSizeX = 15 * BookScale;
        int NextPage_ButtonSizeY = 30 * BookScale;
        int NextPage_ButtonPosX = width / 2 + (BookSizeX * BookScale) / 2 - 18 * BookScale;
        int NextPage_ButtonPosY = height / 2 - NextPage_ButtonSizeY / 2;
        this.addDrawableChild(
                ButtonWidget.builder(Text.of(">"), button -> NextPage()).size(NextPage_ButtonSizeX, NextPage_ButtonSizeY).position(NextPage_ButtonPosX, NextPage_ButtonPosY).build()
        );
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
        BookOfShapeShifterScreenV2_P1 NextPage = new BookOfShapeShifterScreenV2_P1();
        NextPage.currentPlayer = currentPlayer;
        MinecraftClient.getInstance().setScreen(NextPage);
    }

    private ButtonWidget BuildDetailScreenButton(int InBookPosX, int InBookPosY, int SizeX, int SizeY, Text DetailText) {
        int BookScale = 1;
        if (ShapeShifterCurseFabric.clientConfig.newStartBookForBiggerScreen) {
            BookScale = 2;
        }
        int BookPosX = width / 2 - (BookSizeX * BookScale) / 2;
        int BookPosY = height / 2 - (BookSizeY * BookScale) / 2;
        int FixedPosX = BookPosX + InBookPosX * BookScale;
        int FixedPosY = BookPosY + InBookPosY * BookScale;
        int FixedSizeX = SizeX * BookScale;
        int FixedSizeY = SizeY * BookScale;
        return ButtonWidget.builder(Text.of("+"), button -> {
            MinecraftClient.getInstance().setScreen(new DetailScreen(this, DetailText));
        }).size(FixedSizeX, FixedSizeY).position(FixedPosX, FixedPosY).build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.RenderBook(context);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
