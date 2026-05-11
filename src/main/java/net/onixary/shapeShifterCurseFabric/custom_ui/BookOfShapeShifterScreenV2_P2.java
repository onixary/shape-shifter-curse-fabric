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
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleMultilineTextWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleScrollTextWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleTextRenderer;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.WidgetEXUtils;
import net.onixary.shapeShifterCurseFabric.data.CodexData;

import java.util.ArrayList;
import java.util.List;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class BookOfShapeShifterScreenV2_P2 extends Screen implements WidgetEXUtils.IWidgetEX {
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
        // D -> (9, 9), (80, 12)
        // Size -> (83, 181) Pos -> (13, 26)
        this.addDrawableChild(BuildDetailScreenButton(80, 12, 9, 9, CodexData.getContentText(CodexData.ContentType.PROS, currentPlayer)));
        this.addDrawableChild(new TextWidget(BookPosX + 26 * BookScale, BookPosY + 10 * BookScale, 53 * BookScale, 11 * BookScale, CodexData.headerPros, textRenderer).setTextColor(HeaderTextColor));
        ScaleScrollTextWidget Pros = (ScaleScrollTextWidget) new ScaleScrollTextWidget(BookPosX + 13 * BookScale, BookPosY + 26 * BookScale, 83 * BookScale, 20 * BookScale, Scale, CodexData.getContentText(CodexData.ContentType.PROS, currentPlayer), scaleTextRenderer).shadow(false).setTextColor(DefaultTextColor);
        // ScaleScrollTextWidget Pros = (ScaleScrollTextWidget) new ScaleScrollTextWidget(BookPosX + 13 * BookScale, BookPosY + 26 * BookScale, 83 * BookScale, 4 * BookScale, Scale, CodexData.getContentText(CodexData.ContentType.PROS, currentPlayer), scaleTextRenderer).shadow(false).setTextColor(DefaultTextColor);
        this.addWidget(Pros);
        this.addDrawableChild(Pros);
        // Cons
        // D -> (9, 9), (185, 12)
        // Size -> (82, 182) Pos -> (110, 26)
        this.addDrawableChild(BuildDetailScreenButton(185, 12, 9, 9, CodexData.getContentText(CodexData.ContentType.CONS, currentPlayer)));
        this.addDrawableChild(new TextWidget(BookPosX + 120 * BookScale, BookPosY + 10 * BookScale, 63 * BookScale, 11 * BookScale, CodexData.headerCons, textRenderer).setTextColor(HeaderTextColor));
        ScaleScrollTextWidget Cons = (ScaleScrollTextWidget) new ScaleScrollTextWidget(BookPosX + 110 * BookScale, BookPosY + 26 * BookScale, 82 * BookScale, 20 * BookScale, Scale, CodexData.getContentText(CodexData.ContentType.CONS, currentPlayer), scaleTextRenderer).shadow(false).setTextColor(DefaultTextColor);
        // ScaleScrollTextWidget Cons = (ScaleScrollTextWidget) new ScaleScrollTextWidget(BookPosX + 110 * BookScale, BookPosY + 26 * BookScale, 82 * BookScale, 4 * BookScale, Scale, CodexData.getContentText(CodexData.ContentType.CONS, currentPlayer), scaleTextRenderer).shadow(false).setTextColor(DefaultTextColor);
        this.addWidget(Cons);
        this.addDrawableChild(Cons);
        // Instincts
        // D -> (9, 9), (308, 13)
        // Size -> (106, 136) Pos -> (220, 24)
        this.addDrawableChild(BuildDetailScreenButton(308, 13, 9, 9, CodexData.getContentText(CodexData.ContentType.INSTINCTS, currentPlayer)));
        this.addDrawableChild(new TextWidget(BookPosX + 242 * BookScale, BookPosY + 10 * BookScale, 63 * BookScale, 12 * BookScale, CodexData.headerInstincts, textRenderer).setTextColor(HeaderTextColor));
        // 在 BookOfShapeShifterScreen 未上色
        MultilineTextWidget InstinctsDesc = new ScaleMultilineTextWidget(BookPosX + 220 * BookScale, BookPosY + 24 * BookScale, CodexData.getDescText(CodexData.ContentType.INSTINCTS, currentPlayer), scaleTextRenderer, Scale).shadow(false).setMaxWidth(106 * BookScale);
        this.addDrawableChild(InstinctsDesc);
        int InstinctsDescHeight = InstinctsDesc.getHeight();
        ScaleScrollTextWidget Instincts = (ScaleScrollTextWidget) new ScaleScrollTextWidget(BookPosX + 220 * BookScale, BookPosY + 24 * BookScale + InstinctsDescHeight + Math.round(9 * Scale), 106 * BookScale, ((112 - (BookPosY * BookScale + InstinctsDescHeight)) / 9 + 1) * BookScale, Scale, CodexData.getContentText(CodexData.ContentType.INSTINCTS, currentPlayer), scaleTextRenderer).shadow(false).setTextColor(DefaultTextColor);
        this.addWidget(Instincts);
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

    @Override
    public WidgetEXUtils.WidgetRect getRect() {
        return null;
    }

    public List<WidgetEXUtils.IWidgetEX> WidgetList = new ArrayList<>();

    @Override
    public List<WidgetEXUtils.IWidgetEX> getWidgetList() {
        return this.WidgetList;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.onClickWidget(mouseX, mouseY, button);
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
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double mouseZ) {
        this.onScrollWidget(mouseX, mouseY, mouseZ);
        return super.mouseScrolled(mouseX, mouseY, mouseZ);
    }
}
