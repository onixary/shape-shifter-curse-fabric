package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormDynamic;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;

import java.util.ArrayList;
import java.util.List;

public class PatronFormSelectScreen extends Screen {
    private final ClientPlayerEntity player;

    private List<Identifier> availableForms;
    private int nowPage = 0;
    private static final int pageSize = 16;
    private final List<Identifier> buttonForms = new ArrayList<>();
    private final List<ButtonWidget> buttonWidgetList = new ArrayList<>();

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
        // TODO 移除填充项
        // 下面是填充项
        RegPlayerForms.playerForms.forEach((formID, form) -> {
            availableForms.add(form.FormID);
        });
        // 填充项结束
        return availableForms;
    }

    private void SendSetPatronForm(Identifier formID) {
        ModPacketsS2C.sendSetPatronForm(formID);
    }

    private void LoadPage() {
        buttonForms.clear();
        for (int i = nowPage * pageSize; i < (nowPage + 1) * pageSize; i++) {
            if (i < availableForms.size()) {
                buttonForms.add(availableForms.get(i));
            }
            else {
                buttonForms.add(null);
            }
        }
        RefreshButtons();
    }

    private void RefreshButtons() {
        if (buttonForms.size() != buttonWidgetList.size()) {
            ShapeShifterCurseFabric.LOGGER.warn("ButtonForms.size() != buttonWidgetList.size()");
            return;
        }
        for (int i = 0; i < buttonForms.size(); i++) {
            ButtonWidget buttonWidget = buttonWidgetList.get(i);
            if (buttonForms.get(i) != null) {
                try {
                    buttonWidget.setMessage(RegPlayerForms.getPlayerForm(buttonForms.get(i)).getFormName());
                } catch (Exception e) {
                    buttonWidget.setMessage(Text.of(buttonForms.get(i).toString()));
                }
                buttonWidget.visible = true;
            }
            else {
                buttonWidget.visible = false;
            }
        }
    }

    @Override
    public void init() {
        availableForms = getAvailableForms();
        // 或许可以对按钮进行排版
        // 修改数量请同时修改pageSize
        // 一列显示8个 共2列
        int ButtonWidth = 200;
        int ButtonHeight = 20;
        int ButtonStartX = width / 2 - (ButtonWidth + 10);
        int ButtonStartY = height / 2 - 4 * (ButtonHeight + 5) - 12;
        for (int Col = 0; Col < 2; Col++) {
            for (int Row = 0; Row < 8; Row++) {
                int ButtonX = ButtonStartX + Col * (ButtonWidth + 20);
                int ButtonY = ButtonStartY + Row * (ButtonHeight + 5);
                ButtonWidget button = ButtonWidget.builder(Text.of("<-------->"), (buttonWidget) -> {
                    int ID = buttonWidgetList.indexOf(buttonWidget);
                    if (ID >= 0 && ID < buttonForms.size()) {
                        if (buttonForms.get(ID) != null) {
                            SendSetPatronForm(buttonForms.get(ID));
                        }
                    }
                    this.close();
                }).size(ButtonWidth, ButtonHeight).position(ButtonX, ButtonY).build();
                button.visible = false;
                buttonWidgetList.add(button);
                addDrawableChild(button);
            }
        }
        // 翻页
        ButtonWidget PagePrevButton = ButtonWidget.builder(Text.of("<"), (buttonWidget) -> PrevPage()).size(20, 20).position(width / 2 - 130, height / 2 + 4 * (ButtonHeight + 5) - 5).build();
        this.addDrawableChild(PagePrevButton);
        ButtonWidget PageNextButton = ButtonWidget.builder(Text.of(">"), (buttonWidget) -> NextPage()).size(20, 20).position(width / 2 + 110, height / 2 + 4 * (ButtonHeight + 5) - 5).build();
        this.addDrawableChild(PageNextButton);
        LoadPage();
        super.init();
    }

    public void NextPage() {
        int MaxPage = availableForms.size() / pageSize;
        this.nowPage++;
        if (this.nowPage > MaxPage) {
            this.nowPage = 0;
        }
        LoadPage();
    }

    public void PrevPage() {
        int MaxPage = availableForms.size() / pageSize;
        this.nowPage--;
        if (this.nowPage < 0) {
            this.nowPage = MaxPage;
        }
        LoadPage();
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
