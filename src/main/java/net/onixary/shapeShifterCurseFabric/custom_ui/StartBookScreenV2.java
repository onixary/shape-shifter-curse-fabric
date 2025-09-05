package net.onixary.shapeShifterCurseFabric.custom_ui;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.networking.ModPackets;

import java.util.OptionalInt;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class StartBookScreenV2 extends Screen {
    private static final Identifier StartBook_TexID = new Identifier(MOD_ID,"textures/gui/start_book.png");
    public PlayerEntity currentPlayer;

    public static final int BookSizeX = 360;
    public static final int BookSizeY = 330;
    public static final int TextSizeX = 270;
    public static final int TextSizeY = 300;
    public static final int ButtonSizeX = 200;
    public static final int ButtonSizeY = 30;

    public StartBookScreenV2() {
        super(Text.of("ShapeShifterCurse_StartBook_Screen_V2"));
    }

    @Override
    public void init() {
        // 渲染文字
        int TextPosX = width / 2 - TextSizeX / 2;
        int TextPosY = height / 2 - TextSizeY / 2;
        MultilineTextWidget StartBookLabel = new MultilineTextWidget(TextPosX, TextPosY, Text.translatable("screen.shape-shifter-curse.book_of_shape_shifter.start_content_text"), textRenderer);
        StartBookLabel.setMaxWidth(TextSizeX);
        this.addDrawableChild(StartBookLabel);
        // 渲染按钮
        int BookBottomY = height / 2 + BookSizeY / 2;
        int ButtonPosX = width / 2 - ButtonSizeX / 2;
        int ButtonPosY = BookBottomY - 50;
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("screen.shape-shifter-curse.book_of_shape_shifter.start_button_text"),
                button -> {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeUuid(currentPlayer.getUuid());
                    // 发送到服务端
                    ClientPlayNetworking.send(ModPackets.VALIDATE_START_BOOK_BUTTON, buf);
                    if(MinecraftClient.getInstance().currentScreen instanceof StartBookScreen){
                        MinecraftClient.getInstance().setScreen(null);
                    }
                    this.close(); // 关闭当前界面
                }
        ).size(ButtonSizeX, ButtonSizeY).position(ButtonPosX, ButtonPosY).build());
    }

    private void RenderBook(DrawContext context) {
        int BookPosX = width / 2 - BookSizeX / 2;
        int BookPosY = height / 2 - BookSizeY / 2;
        context.drawTexture(StartBook_TexID, BookPosX, BookPosY, 0, 0, BookSizeX, BookSizeY, BookSizeX, BookSizeY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.RenderBook(context);
        super.render(context, mouseX, mouseY, delta);
    }
}
