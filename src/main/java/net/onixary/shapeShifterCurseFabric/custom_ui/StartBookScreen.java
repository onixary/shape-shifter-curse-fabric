package net.onixary.shapeShifterCurseFabric.custom_ui;

import io.netty.buffer.Unpooled;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.networking.ModPackets;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import net.onixary.shapeShifterCurseFabric.player_form.transform.TransformManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;


public class StartBookScreen extends BaseOwoScreen<FlowLayout> {
    // 出于翻译与动态文本的考量，不使用XML来构建
    public PlayerEntity currentPlayer;

    private static final Identifier StartBook_TexID = new Identifier(MOD_ID,"textures/gui/start_book.png");

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.
                surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        rootComponent.child(
                Containers.verticalFlow(Sizing.fixed(360), Sizing.fixed(330))
                        .children(
                        List.of(
                                Components.label(
                                        Text.translatable("screen.shape-shifter-curse.book_of_shape_shifter.start_content_text")
                                )
                                        .maxWidth(270)
                                        .margins(Insets.of(0, 15, 0, 0))
                                ,
                                Components.button(
                                        Text.translatable("screen.shape-shifter-curse.book_of_shape_shifter.start_button_text"),
                                        button ->
                                        {
                                            //TransformManager.handleDirectTransform(currentPlayer, PlayerForms.ORIGINAL_SHIFTER, false);
                                            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                                            buf.writeUuid(currentPlayer.getUuid());
                                            // 发送到服务端
                                            ClientPlayNetworking.send(ModPackets.VALIDATE_START_BOOK_BUTTON, buf);

                                            // disable book screen
                                            if(MinecraftClient.getInstance().currentScreen instanceof StartBookScreen){
                                                MinecraftClient.getInstance().setScreen(null);
                                            }
                                        }
                                ))
                        )
                        .surface(Surface.tiled(StartBook_TexID, 360, 330))
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .verticalAlignment(VerticalAlignment.CENTER)
                        .padding(Insets.of(20))
                ).allowOverflow(true);
    }

    @Override
    public boolean shouldPause() {
        return false; // 核心控制
    }

    public void setCurrentPlayer(PlayerEntity player){
        this.currentPlayer = player;
    }
}
