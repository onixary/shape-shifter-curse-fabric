package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;

import java.util.ArrayList;
import java.util.List;

public class PatronFormSelectScreen extends Screen {
    private final ClientPlayerEntity player;

    public PatronFormSelectScreen(Text title, ClientPlayerEntity player) {
        super(title);
        this.player = player;
    }

    private List<Identifier> getAvailableForms() {
        List<Identifier> availableForms = new ArrayList<>();
        for (Identifier formID : RegPlayerForms.dynamicPlayerForms) {
            PlayerFormBase form = RegPlayerForms.getPlayerForm(formID);
            if (form instanceof PlayerFormDynamic pfd) {
                if (pfd.IsPatronForm && pfd.IsPlayerCanUse(player)) {
                    if (!availableForms.contains(formID)) {
                        availableForms.add(formID);
                    }
                }
            }
        }
        return availableForms;
    }

    private void SendSetPatronForm(Identifier formID) {
        ModPacketsS2C.sendSetPatronForm(formID);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }
}
