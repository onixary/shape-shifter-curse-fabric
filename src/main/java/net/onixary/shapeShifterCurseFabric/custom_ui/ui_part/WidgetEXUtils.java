package net.onixary.shapeShifterCurseFabric.custom_ui.ui_part;


import net.minecraft.util.Pair;

import java.util.List;

public class WidgetEXUtils {
    public static class WidgetRect {
        public int x;
        public int y;
        public int width;
        public int height;

        public WidgetRect(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean isMouseInside(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }

        public Pair<Double, Double> getMousePos(double mouseX, double mouseY) {
            return new Pair<>(mouseX - x, mouseY - y);
        }
    }

    public static interface IWidgetEX {
        public WidgetRect getRect();
        public List<IWidgetEX> getWidgetList();

        public default void addWidget(IWidgetEX widget) {
            getWidgetList().add(widget);
        }

        public default void onClickWidget(double mouseX, double mouseY, int button) {
            for (IWidgetEX widget : getWidgetList()) {
                WidgetRect rect = widget.getRect();
                if (rect.isMouseInside(mouseX, mouseY)) {
                    Pair<Double, Double> newMousePos = rect.getMousePos(mouseX, mouseY);
                    widget.onClickWidget(newMousePos.getLeft(), newMousePos.getRight(), button);
                }
            }
        };
        public default void onReleaseWidget(double mouseX, double mouseY, int button) {
            for (IWidgetEX widget : getWidgetList()) {
                WidgetRect rect = widget.getRect();
                if (rect.isMouseInside(mouseX, mouseY)) {
                    Pair<Double, Double> newMousePos = rect.getMousePos(mouseX, mouseY);
                    widget.onReleaseWidget(newMousePos.getLeft(), newMousePos.getRight(), button);
                }
            }
        };
        public default void onDragWidget(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            for (IWidgetEX widget : getWidgetList()) {
                WidgetRect rect = widget.getRect();
                if (rect.isMouseInside(mouseX, mouseY)) {
                    Pair<Double, Double> newMousePos = rect.getMousePos(mouseX, mouseY);
                    widget.onDragWidget(newMousePos.getLeft(), newMousePos.getRight(), button, deltaX, deltaY);
                }
            }
        };
        public default void onScrollWidget(double mouseX, double mouseY, double mouseZ) {
            for (IWidgetEX widget : getWidgetList()) {
                WidgetRect rect = widget.getRect();
                if (rect.isMouseInside(mouseX, mouseY)) {
                    Pair<Double, Double> newMousePos = rect.getMousePos(mouseX, mouseY);
                    widget.onScrollWidget(newMousePos.getLeft(), newMousePos.getRight(), mouseZ);
                }
            }
        };
    }
}
