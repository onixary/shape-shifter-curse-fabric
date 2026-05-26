package net.onixary.shapeShifterCurseFabric.custom_ui.ui_part;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BiPredicate;

// Button Widget Other Key 其实也可以直接Mixin的 不过这样写兼容性更强
public class ButtonWidgetOKey extends ButtonWidget {
    public BiPredicate<ButtonWidgetOKey, Integer> canClick = null;

    public static final BiPredicate<ButtonWidgetOKey, Integer> LEFT_CLICK = (buttonWidgetOKey, integer) -> integer == 0;
    public static final BiPredicate<ButtonWidgetOKey, Integer> RIGHT_CLICK = (buttonWidgetOKey, integer) -> integer == 1;
    public static final BiPredicate<ButtonWidgetOKey, Integer> MIDDLE_CLICK = (buttonWidgetOKey, integer) -> integer == 2;

    public ButtonWidgetOKey(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
    }

    @Override
    protected boolean isValidClickButton(int button) {
        if (canClick != null) {
            return canClick.test(this, button);
        }
        return super.isValidClickButton(button);
    }
}
