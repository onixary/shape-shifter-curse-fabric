package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.Text;

public class DetailScreen extends Screen {
    private final Screen PreviousScreen;
    private final Text DetailText;

    public DetailScreen(Screen PreviousScreen, Text DetailText) {
        super(Text.of("Detail Screen"));
        this.PreviousScreen = PreviousScreen;
        this.DetailText = DetailText;
    }

    public void init() {
        int TextX = 20;
        int TextY = 40;
        int TextSizeX = width - TextX * 2;
        int TextSizeY = height - 60;
        int TextDefaultColor = 0xFFFFFF;
        MultilineTextWidget DetailTextWidget = new MultilineTextWidget(TextX, TextY, DetailText, textRenderer).setMaxWidth(TextSizeX).setMaxRows(TextSizeY).setTextColor(TextDefaultColor);
        this.addDrawableChild(DetailTextWidget);
        int ButtonX = width - 30;
        int ButtonY = 10;
        int ButtonSizeX = 20;
        int ButtonSizeY = 20;
        ButtonWidget CloseButton = ButtonWidget.builder(Text.of("X"), (button) -> {this.close();}).position(ButtonX, ButtonY).size(ButtonSizeX, ButtonSizeY).build();
        this.addDrawableChild(CloseButton);
    }

    @Override
    public void close() {
        this.client.setScreen(this.PreviousScreen);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
