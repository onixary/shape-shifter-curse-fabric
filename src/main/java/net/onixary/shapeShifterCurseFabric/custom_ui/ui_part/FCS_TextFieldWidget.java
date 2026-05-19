package net.onixary.shapeShifterCurseFabric.custom_ui.ui_part;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FCS_TextFieldWidget extends TextFieldWidget {
    public int ButtonType = -1;
    public int Index = -1;

    public Consumer<FCS_TextFieldWidget> onChanged = null;

    public FCS_TextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    public FCS_TextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, Text text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
    }

    @Override
    public void write(String text) {
        super.write(text);
        if (onChanged != null) {
            onChanged.accept(this);
        }
    }
}
