package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class BookOfShapeShifterScreenV2_P2 extends Screen {
    private static final Identifier page_texID = new Identifier(MOD_ID,"textures/gui/codex_page_2.png");
    public PlayerEntity currentPlayer;
    public static final int BookSizeX = 350;
    public static final int BookSizeY = 320;

    public BookOfShapeShifterScreenV2_P2() {
        super(Text.of("ShapeShifterCurse_Book_Screen_V2"));
    }

    @Override
    public void init() {
        int BookPosX = width / 2 - BookSizeX / 2;
        int BookPosY = height / 2 - BookSizeY / 2;
        // 文本数据
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
