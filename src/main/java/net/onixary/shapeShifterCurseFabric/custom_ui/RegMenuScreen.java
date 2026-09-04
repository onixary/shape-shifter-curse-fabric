package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class RegMenuScreen {
    public static void init() {
        HandledScreens.register(RegMenuType.AlterCraftUI, AlterCraftUI::new);
    }
}
