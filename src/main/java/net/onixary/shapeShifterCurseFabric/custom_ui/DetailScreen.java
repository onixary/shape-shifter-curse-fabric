package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.Text;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleMultilineTextWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleScrollTextWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.WidgetEXUtils;

import java.util.ArrayList;
import java.util.List;

public class DetailScreen extends Screen implements WidgetEXUtils.IWidgetEX {
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
        ScaleScrollTextWidget DetailTextWidget = (ScaleScrollTextWidget) new ScaleScrollTextWidget(TextX, TextY, TextSizeX, TextSizeY / 9, 1.0f, DetailText, textRenderer).setTextColor(TextDefaultColor);
        this.addWidget(DetailTextWidget);
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
