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
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleMultilineTextWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleScrollTextWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleTextRenderer;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.WidgetEXUtils;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class BookOfShapeShifterScreenV2_P1 extends Screen implements WidgetEXUtils.IWidgetEX {
    private static final Identifier page_texID = new Identifier(MOD_ID,"textures/gui/codex_page_1.png");
    public PlayerEntity currentPlayer;
    public static final int BookSizeX = 350;
    public static final int BookSizeY = 220;

    public static final Text openFCSMenuButtonLabel = Text.translatable("gui.shape_shifter_curse_fabric.book_2_1.open_fcs_menu");

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
        int DefaultTextColor = 0x222222;  // 这里的颜色属于乘法模式 (float)(R1*R2,G1*G2,B1*B2) 需要在lang中修改
        int HeaderTextColor = 0xDDDDDD;
        ScaleTextRenderer scaleTextRenderer = new ScaleTextRenderer(textRenderer);
        scaleTextRenderer.Scale = Scale;
        // Title
        // D -> (9, 9), (19, 95)
        // Size -> (108, 48) Pos -> (17, 92)
        this.addDrawableChild(BuildDetailScreenButton(19, 95, 9, 9, CodexData.getContentText(CodexData.ContentType.TITLE, currentPlayer)));
        ScaleScrollTextWidget TitleLabel = (ScaleScrollTextWidget) new ScaleScrollTextWidget(BookPosX + 17 * BookScale, BookPosY + 105 * BookScale, 108 * BookScale, 5 * BookScale, Scale, CodexData.getContentText(CodexData.ContentType.TITLE, currentPlayer), scaleTextRenderer).shadow(false).setTextColor(DefaultTextColor);
        TitleLabel.setEnableScrollableIconRender(true);
        this.addWidget(TitleLabel);
        this.addDrawableChild(TitleLabel);
        // Status 190
        // D -> (9, 9), (116, 143)
        // Size -> (107, 56) Pos -> (17, 153)
        this.addDrawableChild(BuildDetailScreenButton(116, 143, 9, 9, CodexData.getPlayerStatusText(currentPlayer)));
        this.addDrawableChild(new TextWidget(BookPosX + 17 * BookScale, BookPosY + 143 * BookScale, 107 * BookScale, 6 * BookScale, CodexData.headerStatus, textRenderer).setTextColor(HeaderTextColor));
        ScaleScrollTextWidget StatusLabel = (ScaleScrollTextWidget) new ScaleScrollTextWidget(BookPosX + 17 * BookScale, BookPosY + 153 * BookScale, 107 * BookScale, 6 * BookScale, Scale, CodexData.getPlayerStatusText(currentPlayer), scaleTextRenderer).shadow(false).setTextColor(DefaultTextColor);
        StatusLabel.setEnableScrollableIconRender(true);
        this.addWidget(StatusLabel);
        this.addDrawableChild(StatusLabel);
        // Open FCS Menu Button
        // 21,194,98,11
        this.addDrawableChild(ButtonWidget.builder(openFCSMenuButtonLabel, button -> {
                    if (ShapeShifterCurseFabric.clientConfig.fcs_use_v1_menu) {
                        if (FormColorSelectMenu.instance == null) {
                            Screen screen = new FormColorSelectMenu(Text.literal("text.shape-shifter-curse.config.form_color_select_menu"), this);
                            client.setScreen(screen);
                        }
                    } else {
                        if (FormColorSelectMenuV2.instance == null) {
                            Screen screen = new FormColorSelectMenuV2(Text.literal("text.shape-shifter-curse.config.form_color_select_menu_v2"), this);
                            client.setScreen(screen);
                        }
                    }
        }).position(BookPosX + 21 * BookScale, BookPosY + 194 * BookScale).size(98 * BookScale, 11 * BookScale).build());
        // Appearance
        // D -> (9, 9), (311, 13)
        // Size -> (176, 184) Pos -> (142, 23)
        this.addDrawableChild(BuildDetailScreenButton(311, 13, 9, 9, CodexData.getContentText(CodexData.ContentType.APPEARANCE, currentPlayer)));
        this.addDrawableChild(new TextWidget(BookPosX + 142 * BookScale, BookPosY + 11 * BookScale, 176 * BookScale, 8 * BookScale, CodexData.headerAppearance, textRenderer).setTextColor(HeaderTextColor));
        ScaleScrollTextWidget AppearanceLabel = (ScaleScrollTextWidget) new ScaleScrollTextWidget(BookPosX + 142 * BookScale, BookPosY + 26 * BookScale, 176 * BookScale, 20 * BookScale, Scale, CodexData.getContentText(CodexData.ContentType.APPEARANCE, currentPlayer), scaleTextRenderer).shadow(false).setTextColor(DefaultTextColor);
        AppearanceLabel.setEnableScrollableIconRender(true);
        this.addWidget(AppearanceLabel);
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double mouseZ) {
        this.onScrollWidget(mouseX, mouseY, mouseZ);
        return super.mouseScrolled(mouseX, mouseY, mouseZ);
    }
}
