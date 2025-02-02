package net.onixary.shapeShifterCurseFabric.custom_ui;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import net.onixary.shapeShifterCurseFabric.data.PlayerDataStorage;
import net.onixary.shapeShifterCurseFabric.data.PlayerForms;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class StartBookScreen extends BaseOwoScreen<FlowLayout> {
    // 出于翻译与动态文本的考量，不使用XML来构建

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
                Containers.verticalFlow(Sizing.fixed(200), Sizing.fill(80)).children(
                        List.of(
                                Components.label(
                                        Text.literal("Hello, World!")
                                ),
                                Components.button(
                                        Text.translatable("screen.shape-shifter-curse.start_book.start_button_text"),
                                        button ->
                                        {
                                            // toggle skin override
                                            if(!PlayerDataStorage.loadPlayerDataAsBoolean("isEnableContent")){
                                                PlayerDataStorage.savePlayerData("isEnableContent", true);
                                                PlayerDataStorage.savePlayerForm("playerForm", PlayerForms.ORIGINAL_SHIFTER);
                                            }
                                            else{
                                                PlayerDataStorage.savePlayerData("isEnableContent", false);
                                                PlayerDataStorage.savePlayerForm("playerForm", PlayerForms.ORIGINAL_BEFORE_ENABLE);
                                            }

                                        }
                                ))
                        )
                        .surface(Surface.DARK_PANEL)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .verticalAlignment(VerticalAlignment.CENTER)
                );
    }

    @Override
    public boolean shouldPause() {
        return false; // 核心控制
    }
}
