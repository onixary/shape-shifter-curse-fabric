package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
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
import java.util.UUID;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class NormalFormSelectScreen extends Screen {
    // 背景图片固定尺寸配置
    private static final int BG_WIDTH = 470;
    private static final int BG_HEIGHT = 247;
    private static final Identifier BG_TEXTURE = new Identifier(MOD_ID, "textures/gui/normal_form_select_menu.png");
    private final String targetName;
    private final UUID targetUUID;

    private List<Identifier> availableForms;
    private int nowPage = 0;
    private static final int pageSize = 16;
    private final List<Identifier> buttonForms = new ArrayList<>();
    private final List<ButtonWidget> buttonWidgetList = new ArrayList<>();

    public NormalFormSelectScreen(Text title, String targetName, UUID targetUUID) {
        super(title);
        this.targetName = targetName;
        this.targetUUID = targetUUID;
    }

    private List<Identifier> getAvailableForms() {
        List<Identifier> availableForms = new ArrayList<>();
        RegPlayerForms.playerForms.forEach((formID, form) -> {
            availableForms.add(form.FormID);
        });
        return availableForms;
    }

    private void SendSetForm(Identifier formID) {
        ModPacketsS2C.sendSetForm(formID, this.targetUUID);
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
        int ButtonWidth = 180;
        int ButtonHeight = 20;
        int ButtonStartX = width / 2 - (ButtonWidth + 10);
        int ButtonStartY = height / 2 - 4 * (ButtonHeight + 5) - 12;
        int InfoStartY = height / 2 + 4 * (ButtonHeight + 5) + 5;
        int totalButtonWidth = 2 * ButtonWidth + 20;
        int textX = width / 2 - totalButtonWidth / 2;
        TextWidget TargetInfoText_NAME = new TextWidget(textX, InfoStartY - 9, totalButtonWidth, 20, Text.translatable("message.shape-shifter-curse.select_form_ui.target_name", targetName), MinecraftClient.getInstance().textRenderer);
        TargetInfoText_NAME.alignCenter();
        // 暂时不需要UUID，太长了
        //TextWidget TargetInfoText_UUID = new TextWidget(ButtonStartX, InfoStartY, 420, 20, Text.translatable("message.shape-shifter-curse.select_form_ui.target_uuid", targetUUID.toString()), MinecraftClient.getInstance().textRenderer);
        addDrawableChild(TargetInfoText_NAME);
        //addDrawableChild(TargetInfoText_UUID);
        for (int Col = 0; Col < 2; Col++) {
            for (int Row = 0; Row < 8; Row++) {
                int ButtonX = ButtonStartX + Col * (ButtonWidth + 20);
                int ButtonY = ButtonStartY + Row * (ButtonHeight + 5);
                ButtonWidget button = ButtonWidget.builder(Text.of("<-------->"), (buttonWidget) -> {
                    int ID = buttonWidgetList.indexOf(buttonWidget);
                    if (ID >= 0 && ID < buttonForms.size()) {
                        if (buttonForms.get(ID) != null) {
                            SendSetForm(buttonForms.get(ID));
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
        ButtonWidget PagePrevButton = ButtonWidget.builder(Text.of("<"), (buttonWidget) -> PrevPage()).size(20, 20).position(width / 2 - 100, height / 2 + 4 * (ButtonHeight + 5) - 5).build();
        this.addDrawableChild(PagePrevButton);
        ButtonWidget PageNextButton = ButtonWidget.builder(Text.of(">"), (buttonWidget) -> NextPage()).size(20, 20).position(width / 2 + 80, height / 2 + 4 * (ButtonHeight + 5) - 5).build();
        this.addDrawableChild(PageNextButton);
        LoadPage();
        super.init();
    }

    public void NextPage() {
        int MaxPage = availableForms.size() /  pageSize;
        MaxPage += availableForms.size() % pageSize == 0 ? 0 : 1;
        this.nowPage++;
        if (this.nowPage >= MaxPage) {
            this.nowPage = 0;
        }
        LoadPage();
    }

    public void PrevPage() {
        int MaxPage = availableForms.size() / pageSize;
        MaxPage += availableForms.size() % pageSize == 0 ? 0 : 1;
        this.nowPage--;
        if (this.nowPage < 0) {
            this.nowPage = MaxPage - 1;
        }
        LoadPage();
    }

    @Override
    public void close() {
        super.close();
    }

    public void renderBackgroundTexture(DrawContext context) {
        // 计算居中位置，保持固定尺寸
        int bgX = (this.width - BG_WIDTH) / 2;
        int bgY = (this.height - BG_HEIGHT) / 2;
        context.drawTexture(BG_TEXTURE, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        //this.renderTexture(context);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
