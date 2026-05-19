package net.onixary.shapeShifterCurseFabric.custom_ui.ui_part;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class SimpleIntSliderWidget extends SliderWidget {
    public final int minValue;
    public final int maxValue;

    public int intValue = 0;
    public Consumer<SimpleIntSliderWidget> onChanged = null;


    public SimpleIntSliderWidget(int x, int y, int width, int height, Text text, double value, int minValue, int maxValue) {
        super(x, y, width, height, text, value);
        this.minValue = minValue;
        this.maxValue = maxValue;
        if (this.maxValue == this.minValue) {
            throw new IllegalArgumentException("Max value must be greater than min value"); // 这必须得throw了 否则会在setIntValue里报错
        }
    }

    @Override
    protected void updateMessage() {
    }

    @Override
    protected void applyValue() {
        double value = this.value;
        this.intValue = (int) (value * (maxValue - minValue) + minValue);
        if (this.onChanged != null) {
            this.onChanged.accept(this);
        }
    }

    public void setIntValue(int value) {
        this.value = (value - minValue) / (double) (maxValue - minValue);
        this.applyValue();
    }

    public int getIntValue() {
        return this.intValue;
    }
}
