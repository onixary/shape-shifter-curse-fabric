package net.onixary.shapeShifterCurseFabric.custom_ui;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.config.PlayerCustomConfig;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ButtonWidgetOKey;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.FCS_ButtonWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.SimpleIntSliderWidget;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.PlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.util.FormColorData;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.*;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;
import static net.onixary.shapeShifterCurseFabric.custom_ui.FormColorSelectMenu.*;

// XuHaoNan:
// 需要的功能
// RGB HSV 拉条
// 全局颜色槽位的上传和下载
// 玩家模型展示框
// 自动加载/保存 颜色数据 当开启自动同步颜色时会自动把数据写入到config中 否则仅写入到服务器中 数据用服务器端的数据 如果未进入游戏 则使用客户端的数据
// 剪切板上传下载
// 二级菜单

// 比V1少的功能
// 客户端 服务器 独立上传下载按钮 可以实现多个服务器不同颜色数据
// 形态默认颜色(功能保留 UI没了)
// 形态独立颜色槽位(功能有 但没给V2留槽位名字存储 仅V1使用 所以对应功能不删)
// 20260524整的新活 由于依赖"形态默认颜色"系统的更新机制 而且架构已经固定 所以V2不加此功能
// V1的部分Label 文本保留 反正写都写了 保留吧 我之后把V1迁移到我的拓展里时可以减少一点工作量

public class FormColorSelectMenuV2 extends Screen implements FormTextureUtils.TempFormTextureProcessor, FormTextureUtils.TempCustomSkinConfigOverrider, FormTextureUtils.TempFormModelProcessor {
    // LANG 从V1复制的
    private static final Text BoolBTN_ON = Text.translatable("text.cloth-config.boolean.value.true");
    private static final Text BoolBTN_OFF = Text.translatable("text.cloth-config.boolean.value.false");
    private static final Text RIGHT_CLICK_TO_MODIFY = Text.translatable("gui.shape_shifter_curse_fabric.fcsv2.right_click_to_modify");
    private static final Text HEX_TEXT = Text.translatable("gui.shape_shifter_curse_fabric.fcsv2.hex_text");

    // 其他UI部件
    private ButtonWidget formNameLabel = null;

    // Data0:
    private boolean isColorSettingDirty = true;
    private FormTextureUtils.ColorSetting colorSetting_ARGB = null;
    private FormTextureUtils.ColorSetting colorSetting_ABGR = null;

    // Data1: 由它更新Data0修改它后需要把isColorSettingDirty设为true 和触发更新Data2函数
    private int primaryColor = 0x00FFFFFF;
    private int accentColor1Color = 0x00FFFFFF;
    private int accentColor2Color = 0x00FFFFFF;
    private int eyeColorA = 0x00FFFFFF;
    private int eyeColorB = 0x00FFFFFF;
    private boolean primaryGreyReverse = false;
    private boolean accent1GreyReverse = false;
    private boolean accent2GreyReverse = false;
    private boolean keepCustomSkin = false;
    private boolean enableFormColorSystem = true;

    // Data2: 由Data1的数据更新 修改时直接修改对应int 需要一个flag标记 防止循环调用 更新对应Data5数据
    private boolean isUpdateConfigWidget = false;
    private final boolean UseSliderTextBox = true;

    private TextFieldWidget primaryColorTextBox;
    private TextFieldWidget accentColor1TextBox;
    private TextFieldWidget accentColor2TextBox;
    private TextFieldWidget eyeColorATextBox;
    private TextFieldWidget eyeColorBTextBox;

    private TextFieldWidget SliderTextBox; // 和上面的5个TextBox二选一
    // 点击直接切换
    private ButtonWidget primaryGreyReverseButton;
    private ButtonWidget accent1GreyReverseButton;
    private ButtonWidget accent2GreyReverseButton;
    private ButtonWidget keepCustomSkinButton;
    private ButtonWidget enableFormColorSystemButton;

    // Data3: 修改它后需要调用刷新函数 直接修改对应的TextBox 仅当flag为否时修改
    private int SliderIndex = -1;
    private boolean isUpdateSliderFormConfig = false;
    private int SliderR = 0;
    private int SliderG = 0;
    private int SliderB = 0;
    private int SliderA = 0;
    // Data3.1 用于更新RGB用
    private int SliderH = 0;
    private int SliderS = 0;
    private int SliderV = 0;

    // Data4: 如果有flag 则仅更新Data3 否则修改对应的Slider(不会更新Data3)
    private int isUpdateSlider = 0;
    private TextWidget PanelConfigNameLabel = null;
    private TextFieldWidget SliderRTextBox;
    private TextFieldWidget SliderGTextBox;
    private TextFieldWidget SliderBTextBox;
    private TextFieldWidget SliderHTextBox;
    private TextFieldWidget SliderSTextBox;
    private TextFieldWidget SliderVTextBox;
    // 点击直接切换
    private ButtonWidget SliderAButton;

    // Data5: 修改时将Data4的flag++ 更新对应的TextBox 检查自身Flag 选择是否触发更新RGB HSV
    private boolean isUpdateRGBHSV = false;  // 当此值为true 不会触发RGBHSV更新
    private boolean forceUpdateFormRGBHSV = false;
    private SimpleIntSliderWidget SliderRSlider;
    private SimpleIntSliderWidget SliderGSlider;
    private SimpleIntSliderWidget SliderBSlider;
    private SimpleIntSliderWidget SliderHSlider;
    private SimpleIntSliderWidget SliderSSlider;
    private SimpleIntSliderWidget SliderVSlider;

    // 用于二级菜单 覆盖和被覆盖需要注册在list中
    private boolean isOpenPanel02 = false;
    private List<ClickableWidget> ConfigPanel01 = new ArrayList<>();
    private List<ClickableWidget> ConfigPanel02 = new ArrayList<>();

    // 私有变量 用于部分逻辑
    private boolean isScreenInit = false;
    private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private static final Identifier BG_TEXTURE = new Identifier(MOD_ID,"textures/gui/v2_form_color_select_menu.png");
    private static final int BG_WIDTH = 420;
    private static final int BG_HEIGHT = 227;
    private static final int BG_IMAGE_WIDTH = 420;
    private static final int BG_IMAGE_HEIGHT = 427;
    private static final int EXTRA_PART_START_X = 0;
    private static final int EXTRA_PART_START_Y = 227;
    public static FormColorSelectMenuV2 instance;
    private boolean isLockTempTextureSystem = false;  // 用于还原
    private boolean isLockTempModelSystem = false;  // 用于还原
    private boolean isLockTempConfigSystem = false;  // 用于还原
    private @Nullable Screen parsetScreen = null;
    private final HashMap<String, HashMap<FormTextureUtils.ColorSetting, Identifier>> colorSettingCacheMap = new HashMap<>();  // 防止内存泄漏
    private int modelID = -1;
    private static final String identifierNameSpace = MOD_ID;
    private static final String identifierPrefix = "dynamic_fcs_v2_";
    private static long nowColorSettingIndex = 0;  // 自增ID
    private int timer = 0;
    private List<Pair<FCS_ButtonWidget, FCS_ButtonWidget>> globalSlotButton = new ArrayList<>();
    private List<TextFieldWidget> globalSlotNameInputs = new ArrayList<>();
    private int formIDIndex = -1;

    // 工具函数
    public static String encodeColor(int Color) {
        return String.format(Locale.ROOT, "#%08X", Color);
    }

    public static int decodeColor(String Color) {
        Integer color = null;
        try {
            if (Color.startsWith("#")) {
                color = Integer.parseUnsignedInt(Color.substring(1), 16);
            } else {
                color = Integer.parseUnsignedInt(Color, 10);
            }
        } catch (Exception ignored) {
        }
        if (color == null) {
            return 0x00FFFFFF;
        }
        return color;
    }

    public static int colorChannel2Int(String channel, int min, int max) {
        try {
            int value = Integer.parseInt(channel);
            return Math.min(Math.max(value, min), max);
        } catch (Exception ignored) {
            return min;
        }
    }

    // 形态预览翻页
    public void scrollFormID(int offset, boolean loop) {
        if (formIDIndex < 0) {
            formIDIndex = 0;
        }
        formIDIndex += offset;
        if (formIDIndex < 0) {
            formIDIndex = loop ? RegPlayerForms.playerForms.size() - 1 : 0;
        } else if (formIDIndex >= RegPlayerForms.playerForms.size()) {
            formIDIndex = loop ? 0 : RegPlayerForms.playerForms.size() - 1;
        }
        this.reloadFormIDName();
    }

    public void reloadFormIDName() {
        PlayerFormBase form = this.getFormNoCheckUnlock();
        boolean isUnlocked = ShapeShifterCurseFabricClient.formColorData.isUnlock(form.FormID);
        if (ShapeShifterCurseFabric.clientConfig.disableUnlockCheckInFormColorSelectMenu) {
            isUnlocked = true;
        }
        Text message = NoneFromNameLabel;
        if (!RegPlayerForms.ORIGINAL_BEFORE_ENABLE.equals(form)) {
            message = form.getFormName();
        }
        if (!isUnlocked) {
            if (message instanceof MutableText text) {
                text.setStyle(message.getStyle().withColor(TextColor.fromRgb(0xFF0000)));
            } else {
                message = message.copy().setStyle(message.getStyle().withColor(TextColor.fromRgb(0xFF0000)));
            }
        }
        this.formNameLabel.setMessage(message);
    }

    public void reloadFormIDIndex() {
        if (minecraftClient.player != null) {
            boolean isFind = false;
            PlayerFormBase form = RegPlayerFormComponent.PLAYER_FORM.get(minecraftClient.player).getCurrentForm();
            if (form != null) {
                int Index = 0;
                for (PlayerFormBase playerFormBase : RegPlayerForms.playerForms.values()) {
                    if (Objects.equals(playerFormBase.FormID, form.FormID)) {
                        formIDIndex = Index;
                        isFind = true;
                        break;
                    }
                    Index++;
                }
            }
            if (!isFind) {
                formIDIndex = -1;
            }
            return;
        }
        formIDIndex = -1;
    }

    public PlayerFormBase getFormNoCheckUnlock() {
        if (this.formIDIndex < 0) {
            return RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
        }
        Collection<PlayerFormBase> playerFormsCollection = RegPlayerForms.playerForms.values();
        if (this.formIDIndex >= playerFormsCollection.size()) {
            return RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
        }
        return playerFormsCollection.toArray(new PlayerFormBase[0])[this.formIDIndex];
    }

    @Override
    public PlayerFormBase getForm() {
        if (ShapeShifterCurseFabric.clientConfig.disableUnlockCheckInFormColorSelectMenu) {
            return this.getFormNoCheckUnlock();
        }
        PlayerFormBase finalForm = null;
        if (this.formIDIndex < 0) {
            finalForm = RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
        } else {
            Collection<PlayerFormBase> playerFormsCollection = RegPlayerForms.playerForms.values();
            if (this.formIDIndex >= playerFormsCollection.size()) {
                finalForm = RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
            } else {
                finalForm = playerFormsCollection.toArray(new PlayerFormBase[0])[this.formIDIndex];
            }
        }
        if (finalForm == null || !ShapeShifterCurseFabricClient.formColorData.isUnlock(finalForm.FormID)) {
            if (minecraftClient.player != null) {
                return RegPlayerFormComponent.PLAYER_FORM.get(minecraftClient.player).getCurrentForm();
            } else {
                return RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
            }
        }
        return finalForm;
    }

    @Override
    public Identifier getLayerID() {
        return this.getForm().getFormOriginID();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // FCS按钮生成函数
    private void createSaveDataButtons(int Index, int X, int Y) {
        // X,Y,80,15
        // X+0,Y+0,15,15 upload/download Button
        FCS_ButtonWidget updButtonWidget = new FCS_ButtonWidget(X, Y, EmptyText, (button -> {
            if (button instanceof FCS_ButtonWidget fcsButtonWidget) {
                // 靠UI判断 省的写一个变量了
                if (fcsButtonWidget.TEXTURE_X == 15) {
                    FormTextureUtils.ColorSetting colorSetting = this.getGlobalSetting(Index);
                    if (colorSetting != null) {
                        this.loadData(colorSetting);
                    }
                } else if (fcsButtonWidget.TEXTURE_X == 0) {
                    this.setGlobalSetting(Index);
                }
            }
        }), (textSupplier) -> (MutableText)textSupplier.get(), 0);

        // X+15,Y+0,40,15 slot name input
        TextFieldWidget textFieldWidget = new TextFieldWidget(this.textRenderer, X + 15, Y, 40, 15, EmptyText);
        textFieldWidget.setChangedListener((text) -> {
            this.onGlobalSlotNameChanged(Index);
        });
        // X+55,Y+0,15,15 delete Button
        FCS_ButtonWidget deleteButtonWidget = new FCS_ButtonWidget(X + 55, Y, EmptyText, (button -> {
            if (button instanceof FCS_ButtonWidget fcsButtonWidget) {
                if (fcsButtonWidget.TEXTURE_X == 30) {
                    this.removeGlobalSetting(Index);
                }
            }
        }), (textSupplier) -> (MutableText)textSupplier.get(), 30);
        globalSlotButton.add(new Pair<>(updButtonWidget, deleteButtonWidget));
        globalSlotNameInputs.add(textFieldWidget);
        textFieldWidget.setText(this.getGlobalSlotName(Index));
        this.addDrawableChild(updButtonWidget);
        this.addDrawableChild(textFieldWidget);
        this.addDrawableChild(deleteButtonWidget);
    }

    // Data0 函数

    private void updateColorSetting() {
        colorSetting_ARGB = new FormTextureUtils.ColorSetting(
                primaryColor,
                accentColor1Color,
                accentColor2Color,
                eyeColorA,
                eyeColorB,
                primaryGreyReverse,
                accent1GreyReverse,
                accent2GreyReverse
        );
        colorSetting_ABGR = new FormTextureUtils.ColorSetting(
                FormTextureUtils.ARGB2ABGR(primaryColor),
                FormTextureUtils.ARGB2ABGR(accentColor1Color),
                FormTextureUtils.ARGB2ABGR(accentColor2Color),
                FormTextureUtils.ARGB2ABGR(eyeColorB),
                FormTextureUtils.ARGB2ABGR(eyeColorA),
                primaryGreyReverse,
                accent2GreyReverse,
                accent1GreyReverse
        );
        this.isColorSettingDirty = false;
    }

    public FormTextureUtils.ColorSetting getColorSetting(boolean isABGR) {
        if (this.isColorSettingDirty) {
            updateColorSetting();
        }
        return isABGR ? colorSetting_ABGR : colorSetting_ARGB;
    }

    // Data1 函数
    private void onData1Changed() {
        if (!this.isScreenInit) {
            return;
        }
        // 更新Data2
        this.isUpdateConfigWidget = true;
        if (!this.UseSliderTextBox) {
            this.primaryColorTextBox.setText(encodeColor(this.primaryColor));
            this.accentColor1TextBox.setText(encodeColor(this.accentColor1Color));
            this.accentColor2TextBox.setText(encodeColor(this.accentColor2Color));
            this.eyeColorATextBox.setText(encodeColor(this.eyeColorA));
            this.eyeColorBTextBox.setText(encodeColor(this.eyeColorB));
        } else {
            int Color = 0x00FFFFFF;
            switch (this.SliderIndex) {
                case 0 -> { Color = this.primaryColor; }
                case 1 -> { Color = this.accentColor1Color; }
                case 2 -> { Color = this.accentColor2Color; }
                case 3 -> { Color = this.eyeColorA; }
                case 4 -> { Color = this.eyeColorB; }
            }
            this.SliderTextBox.setText(encodeColor(Color));
        }
        this.primaryGreyReverseButton.setMessage(this.primaryGreyReverse ? BoolBTN_ON : BoolBTN_OFF);
        this.accent1GreyReverseButton.setMessage(this.accent1GreyReverse ? BoolBTN_ON : BoolBTN_OFF);
        this.accent2GreyReverseButton.setMessage(this.accent2GreyReverse ? BoolBTN_ON : BoolBTN_OFF);
        this.keepCustomSkinButton.setMessage(this.keepCustomSkin ? BoolBTN_ON : BoolBTN_OFF);
        this.enableFormColorSystemButton.setMessage(this.enableFormColorSystem ? BoolBTN_ON : BoolBTN_OFF);
        this.isUpdateConfigWidget = false;
        // 更新Data5 由Data5转Data4再转Data3 最后由isUpdateSliderFormConfig Flag阻止更新至Data1
        // switch (this.SliderIndex) {
        //     case 0 -> { this.PanelConfigNameLabel.setMessage(PrimaryColorLabel); }
        //     case 1 -> { this.PanelConfigNameLabel.setMessage(AccentColor1Label); }
        //     case 2 -> { this.PanelConfigNameLabel.setMessage(AccentColor2Label); }
        //     case 3 -> { this.PanelConfigNameLabel.setMessage(EyeColorALabel); }
        //     case 4 -> { this.PanelConfigNameLabel.setMessage(EyeColorBLabel); }
        // }
        int Color = 0x00FFFFFF;
        switch (this.SliderIndex) {
            case 0 -> { Color = this.primaryColor; }
            case 1 -> { Color = this.accentColor1Color; }
            case 2 -> { Color = this.accentColor2Color; }
            case 3 -> { Color = this.eyeColorA; }
            case 4 -> { Color = this.eyeColorB; }
        }
        int R = (Color >> 16) & 0xFF;
        int G = (Color >> 8) & 0xFF;
        int B = Color & 0xFF;
        int A = (Color >> 24) & 0xFF;
        this.isUpdateSliderFormConfig = true;
        this.SliderRSlider.setIntValue(R);
        this.SliderGSlider.setIntValue(G);
        this.SliderBSlider.setIntValue(B);
        this.SliderA = A;
        this.SliderAButton.setMessage(this.SliderA != 0 ? BoolBTN_ON : BoolBTN_OFF);
        this.isUpdateSliderFormConfig = false;
        this.updateHSVFromRGB();
    }

    // Data2 函数
    private void onData2ChangedOrClicked(int textBoxIndex) {
        if (this.isUpdateConfigWidget) {
            return;
        }
        if (!this.UseSliderTextBox) {
            switch (textBoxIndex) {
                // OnChanged
                case 0 -> { this.primaryColor = decodeColor(this.primaryColorTextBox.getText()); }
                case 1 -> { this.accentColor1Color = decodeColor(this.accentColor1TextBox.getText()); }
                case 2 -> { this.accentColor2Color = decodeColor(this.accentColor2TextBox.getText()); }
                case 3 -> { this.eyeColorA = decodeColor(this.eyeColorATextBox.getText()); }
                case 4 -> { this.eyeColorB = decodeColor(this.eyeColorBTextBox.getText()); }
                // OnClicked
                case 5 -> { this.primaryGreyReverse = !this.primaryGreyReverse; }
                case 6 -> { this.accent1GreyReverse = !this.accent1GreyReverse; }
                case 7 -> { this.accent2GreyReverse = !this.accent2GreyReverse; }
                case 8 -> { this.keepCustomSkin = !this.keepCustomSkin; }
                case 9 -> { this.enableFormColorSystem = !this.enableFormColorSystem; }
            }
        } else {
            switch (textBoxIndex) {
                // OnChanged
                case 0, 1, 2, 3, 4 -> {
                    switch (this.SliderIndex) {
                        case 0 -> { this.primaryColor = decodeColor(this.SliderTextBox.getText()); }
                        case 1 -> { this.accentColor1Color = decodeColor(this.SliderTextBox.getText()); }
                        case 2 -> { this.accentColor2Color = decodeColor(this.SliderTextBox.getText()); }
                        case 3 -> { this.eyeColorA = decodeColor(this.SliderTextBox.getText()); }
                        case 4 -> { this.eyeColorB = decodeColor(this.SliderTextBox.getText()); }
                    }
                }
                // OnClicked
                case 5 -> { this.primaryGreyReverse = !this.primaryGreyReverse; }
                case 6 -> { this.accent1GreyReverse = !this.accent1GreyReverse; }
                case 7 -> { this.accent2GreyReverse = !this.accent2GreyReverse; }
                case 8 -> { this.keepCustomSkin = !this.keepCustomSkin; }
                case 9 -> { this.enableFormColorSystem = !this.enableFormColorSystem; }
            }
        }
        this.isColorSettingDirty = true;
        this.onData1Changed();
    }

    // Data3 函数
    private void onData3Changed() {
        if (this.isUpdateSliderFormConfig) {
            return;
        }
        if (this.isUpdateRGBHSV && !forceUpdateFormRGBHSV) {
            return;
        }
        int Color = SliderA << 24 | SliderR << 16 | SliderG << 8 | SliderB;
        switch (this.SliderIndex) {
            case 0 -> { this.primaryColor = Color; }
            case 1 -> { this.accentColor1Color = Color; }
            case 2 -> { this.accentColor2Color = Color; }
            case 3 -> { this.eyeColorA = Color; }
            case 4 -> { this.eyeColorB = Color; }
        }
        this.isColorSettingDirty = true;
        this.onData1Changed();
    }

    // Data4 函数
    private void onData4ChangedOrClicked(int textBoxIndex) {
        if (this.isUpdateSlider == 0) {
            switch (textBoxIndex) {
                case 0 -> { this.SliderRSlider.setIntValue(colorChannel2Int(this.SliderRTextBox.getText(), 0, 255)); }
                case 1 -> { this.SliderGSlider.setIntValue(colorChannel2Int(this.SliderGTextBox.getText(), 0, 255)); }
                case 2 -> { this.SliderBSlider.setIntValue(colorChannel2Int(this.SliderBTextBox.getText(), 0, 255)); }
                case 3 -> { this.SliderHSlider.setIntValue(colorChannel2Int(this.SliderHTextBox.getText(), 0, 359)); }
                case 4 -> { this.SliderSSlider.setIntValue(colorChannel2Int(this.SliderSTextBox.getText(), 0, 100)); }
                case 5 -> { this.SliderVSlider.setIntValue(colorChannel2Int(this.SliderVTextBox.getText(), 0, 100)); }
                case 6 -> { this.SliderA = this.SliderA == 0 ? 255 : 0; }
            }
        } else {
            switch (textBoxIndex) {
                case 0 -> { this.SliderR = colorChannel2Int(this.SliderRTextBox.getText(), 0, 255); }
                case 1 -> { this.SliderG = colorChannel2Int(this.SliderGTextBox.getText(), 0, 255); }
                case 2 -> { this.SliderB = colorChannel2Int(this.SliderBTextBox.getText(), 0, 255); }
                case 3 -> { this.SliderH = colorChannel2Int(this.SliderHTextBox.getText(), 0, 359); }
                case 4 -> { this.SliderS = colorChannel2Int(this.SliderSTextBox.getText(), 0, 100); }
                case 5 -> { this.SliderV = colorChannel2Int(this.SliderVTextBox.getText(), 0, 100); }
                case 6 -> { this.SliderA = this.SliderA == 0 ? 255 : 0; }
            }
        }
        this.onData3Changed();
        this.SliderAButton.setMessage(this.SliderA != 0 ? BoolBTN_ON : BoolBTN_OFF);
    }

    // Data5 函数
    private void updateHSVFromRGB() {
        if (this.isUpdateRGBHSV || this.isUpdateSlider > 0) {
            return;
        }
        this.isUpdateRGBHSV = true;
        int[] HSV = FormTextureUtils.rgbToHsv(SliderR, SliderG, SliderB);
        this.SliderHSlider.setIntValue(HSV[0]);
        this.SliderSSlider.setIntValue(HSV[1]);
        this.SliderVSlider.setIntValue(HSV[2]);
        this.forceUpdateFormRGBHSV = true;
        this.onData3Changed();
        this.forceUpdateFormRGBHSV = false;
        this.isUpdateRGBHSV = false;
    }

    private void updateRGBFromHSV() {
        if (this.isUpdateRGBHSV || this.isUpdateSlider > 0) {
            return;
        }
        this.isUpdateRGBHSV = true;
        int[] RGB = FormTextureUtils.hsvToRgb(SliderH, SliderS, SliderV);
        this.SliderRSlider.setIntValue(RGB[0]);
        this.SliderGSlider.setIntValue(RGB[1]);
        this.SliderBSlider.setIntValue(RGB[2]);
        this.forceUpdateFormRGBHSV = true;
        this.onData3Changed();
        this.forceUpdateFormRGBHSV = false;
        this.isUpdateRGBHSV = false;
    }

    private void onData5Changed(int sliderIndex) {
        this.isUpdateSlider++;
        switch (sliderIndex) {
            case 0 -> { this.SliderRTextBox.setText(String.valueOf(this.SliderRSlider.getIntValue())); }
            case 1 -> { this.SliderGTextBox.setText(String.valueOf(this.SliderGSlider.getIntValue())); }
            case 2 -> { this.SliderBTextBox.setText(String.valueOf(this.SliderBSlider.getIntValue())); }
            case 3 -> { this.SliderHTextBox.setText(String.valueOf(this.SliderHSlider.getIntValue())); }
            case 4 -> { this.SliderSTextBox.setText(String.valueOf(this.SliderSSlider.getIntValue())); }
            case 5 -> { this.SliderVTextBox.setText(String.valueOf(this.SliderVSlider.getIntValue())); }
        }
        this.isUpdateSlider--;
        // 在从配置加载数据时 不用自动更新HSV 由后续代码自动更新
        if (this.isUpdateSliderFormConfig) {
            return;
        }
        switch (sliderIndex) {
            case 0, 1, 2 -> {
                this.updateHSVFromRGB();
            }
            case 3, 4, 5 -> {
                this.updateRGBFromHSV();
            }
        }
    }
    // 二级菜单
    public void updatePanel() {
        if (isOpenPanel02) {
            ConfigPanel01.forEach(element -> element.visible = false);
            ConfigPanel02.forEach(element -> element.visible = true);
        } else {
            ConfigPanel01.forEach(element -> element.visible = true);
            ConfigPanel02.forEach(element -> element.visible = false);
        }
        this.onData1Changed();
    }

    public void openPanel(int index) {
        this.isOpenPanel02 = true;
        this.SliderIndex = index;
        this.updatePanel();
    }

    public void closePanel() {
        this.onData3Changed();
        this.isOpenPanel02 = false;
        this.updatePanel();
    }

    // Global Slot 函数
    private String getGlobalSlotName(int index) {
        return ShapeShifterCurseFabricClient.formColorData.V2_getName_GlobalSlot(index);
    }

    private void onGlobalSlotNameChanged(int slotIndex) {
        ShapeShifterCurseFabricClient.formColorData.V2_setName_GlobalSlot(slotIndex, this.globalSlotNameInputs.get(slotIndex).getText());
    }

    private boolean isGlobalSettingExists(int index) {
        String id = String.format("fcs_v2_%s", index);
        return ShapeShifterCurseFabricClient.formColorData.customSetting.containsKey(id);
    }

    private @Nullable FormTextureUtils.ColorSetting getGlobalSetting(int index) {
        String id = String.format("fcs_v2_%s", index);
        return ShapeShifterCurseFabricClient.formColorData.customSetting.get(id);
    }

    private void setGlobalSetting(int index) {
        String id = String.format("fcs_v2_%s", index);
        FormTextureUtils.ColorSetting colorSettingRGBA = this.getColorSetting(false);
        ShapeShifterCurseFabricClient.formColorData.customSetting.put(id, colorSettingRGBA);
        this.updateSavaButtonActive();
    }

    private void removeGlobalSetting(int index) {
        String id = String.format("fcs_v2_%s", index);
        ShapeShifterCurseFabricClient.formColorData.customSetting.remove(id);
        this.updateSavaButtonActive();
    }

    private void updateSavaButtonActive() {
        if (!this.isScreenInit) {
            return;
        }
        for (int index = 0; index < this.globalSlotNameInputs.size(); index++) {
            boolean dataExist = this.isGlobalSettingExists(index);
            Pair<FCS_ButtonWidget, FCS_ButtonWidget> buttonWidget = globalSlotButton.get(index);
            FCS_ButtonWidget deleteButtonWidget = buttonWidget.getRight();
            deleteButtonWidget.active = dataExist;
            FCS_ButtonWidget updButtonWidget = buttonWidget.getLeft();
            updButtonWidget.active = true;
            updButtonWidget.TEXTURE_X = dataExist ? 15 : 0;
        }
    }

    public FormColorSelectMenuV2(Text title, @Nullable Screen parsetScreen) {
        this(title);
        this.parsetScreen = parsetScreen;
    }

    public void loadData(FormTextureUtils.ColorSetting colorSetting) {
        primaryColor = colorSetting.getPrimaryColor();
        accentColor1Color = colorSetting.getAccentColor1();
        accentColor2Color = colorSetting.getAccentColor2();
        eyeColorA = colorSetting.getEyeColorA();
        eyeColorB = colorSetting.getEyeColorB();
        primaryGreyReverse = colorSetting.getPrimaryGreyReverse();
        accent1GreyReverse = colorSetting.getAccent1GreyReverse();
        accent2GreyReverse = colorSetting.getAccent2GreyReverse();
        isColorSettingDirty = true;
        this.onData1Changed();
    }

    public void loadServerData(FormTextureUtils.ColorSetting colorSetting) {
        // 服务器上的是 ABGR
        primaryColor = FormTextureUtils.ABGR2ARGB(colorSetting.getPrimaryColor());
        accentColor1Color = FormTextureUtils.ABGR2ARGB(colorSetting.getAccentColor1());
        accentColor2Color = FormTextureUtils.ABGR2ARGB(colorSetting.getAccentColor2());
        eyeColorA = FormTextureUtils.ABGR2ARGB(colorSetting.getEyeColorA());
        eyeColorB = FormTextureUtils.ABGR2ARGB(colorSetting.getEyeColorB());
        primaryGreyReverse = colorSetting.getPrimaryGreyReverse();
        accent1GreyReverse = colorSetting.getAccent1GreyReverse();
        accent2GreyReverse = colorSetting.getAccent2GreyReverse();
        isColorSettingDirty = true;
        this.onData1Changed();
    }

    public void loadData() {
        if (minecraftClient.player != null) {
            PlayerSkinComponent component = RegPlayerSkinComponent.SKIN_SETTINGS.get(minecraftClient.player);
            FormTextureUtils.ColorSetting colorSetting = component.getFormColor();
            this.loadServerData(colorSetting);
        } else {
            primaryColor = ShapeShifterCurseFabric.playerCustomConfig.primaryColor;
            accentColor1Color = ShapeShifterCurseFabric.playerCustomConfig.accentColor1Color;
            accentColor2Color = ShapeShifterCurseFabric.playerCustomConfig.accentColor2Color;
            eyeColorA = ShapeShifterCurseFabric.playerCustomConfig.eyeColorA;
            eyeColorB = ShapeShifterCurseFabric.playerCustomConfig.eyeColorB;
            primaryGreyReverse = ShapeShifterCurseFabric.playerCustomConfig.primaryGreyReverse;
            accent1GreyReverse = ShapeShifterCurseFabric.playerCustomConfig.accent1GreyReverse;
            accent2GreyReverse = ShapeShifterCurseFabric.playerCustomConfig.accent2GreyReverse;
            this.onData1Changed();
        }
        isColorSettingDirty = true;
    }

    public FormColorSelectMenuV2(Text title) {
        super(title);
        this.reloadFormIDIndex();
        loadData();
        if (!FormTextureUtils.useTempFormTexture) {
            FormTextureUtils.useTempFormTexture = true;
            FormTextureUtils.tempFormTextureProcessor = this;
            isLockTempTextureSystem = true;
        } else {
            ShapeShifterCurseFabric.LOGGER.warn("Temp Texture System is already in use, dynamic texture rendering will not work");
        }
        if (!FormTextureUtils.useTempFormModel) {
            FormTextureUtils.useTempFormModel = true;
            FormTextureUtils.tempFormModelProcessor = this;
            isLockTempModelSystem = true;
        } else {
            ShapeShifterCurseFabric.LOGGER.warn("Temp Form Model System is already in use, dynamic form rendering will not work");
        }
        if (!FormTextureUtils.useTempCustomSkinConfig) {
            FormTextureUtils.useTempCustomSkinConfig = true;
            FormTextureUtils.tempCustomSkinConfigOverrider = this;
            isLockTempConfigSystem = true;
        } else {
            ShapeShifterCurseFabric.LOGGER.warn("Temp Config System is already in use, dynamic config rendering will not work");
        }
        if (instance != null) {
            ShapeShifterCurseFabric.LOGGER.error("FormColorSelectMenu is already in use, only one instance is allowed");
        }
        instance = this;
    }

    @Override
    public void init() {
        super.init();
        int BPosX = width / 2 - BG_WIDTH / 2;  // 图片左上角 X
        int BPosY = height / 2 - BG_HEIGHT / 2;  // 图片左上角 Y
        int TextColor = 0xDDDDDD;
        // 330,20,70,11 - Global Slot Label
        this.addDrawableChild(new TextWidget(BPosX + 330, BPosY + 20, 70, 11, GlobalSlotTitle, textRenderer)).setTextColor(TextColor);
        // 20,196,68,11 - 从剪切板获取
        this.addDrawableChild(ButtonWidget.builder(DownloadFromClipboard, button -> {
                    String keyBoardData = minecraftClient.keyboard.getClipboard();
                    FormTextureUtils.ColorSetting cs = FormColorData.ColorSettingFormString(keyBoardData);
                    if (cs != null) {
                        this.loadData(cs);
                    }
                }).position(BPosX + 20, BPosY + 196).size(68, 11).build()
        );
        // 94,196,68,11 - 发送到剪切板
        this.addDrawableChild(ButtonWidget.builder(UploadToClipboard, button -> {
                    String keyBoardData = FormColorData.ColorSettingtoString(this.getColorSetting(false), true);
                    if (keyBoardData == null) {
                        return;
                    }
                    minecraftClient.keyboard.setClipboard(keyBoardData);
                }).position(BPosX + 94, BPosY + 196).size(68, 11).build()
        );
        // 48,20,86,11 - Form Name Button
        ButtonWidget formScrollButton = ButtonWidget.builder(NoneFromNameLabel, button -> {
            this.reloadFormIDIndex();
            this.reloadFormIDName();
        }).position(BPosX + 48, BPosY + 20).size(86, 11).build();
        this.addDrawableChild(formScrollButton);
        this.formNameLabel = formScrollButton;
        // 31,20,11,11 - Form Scroll Left Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> {
            this.scrollFormID(-1, true);
        }).position(BPosX + 31, BPosY + 20).size(11, 11).build());
        // 140,20,11,11 - Form Scroll Right Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> {
            this.scrollFormID(1, true);
        }).position(BPosX + 140, BPosY + 20).size(11, 11).build());
        this.reloadFormIDName();
        // Config Panel 01
        // 192,39,68,11 - PrimaryColor Label
        TextWidget primaryColorLabel = new TextWidget(BPosX + 192, BPosY + 39, 68, 11, PrimaryColorLabel, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(primaryColorLabel);
        this.ConfigPanel01.add(primaryColorLabel);
        // 270,39,45,11 - PrimaryColor Button
        ButtonWidgetOKey primaryColorButton = new ButtonWidgetOKey(BPosX + 270, BPosY + 39, 45, 11, RIGHT_CLICK_TO_MODIFY, button -> {
            this.openPanel(0);
        }, ButtonWidgetOKey.DEFAULT_NARRATION_SUPPLIER);
        primaryColorButton.canClick = ButtonWidgetOKey.RIGHT_CLICK;
        this.addDrawableChild(primaryColorButton);
        this.ConfigPanel01.add(primaryColorButton);
        // 192,54,68,11 - AccentColor1 Label
        TextWidget accentColor1Label = new TextWidget(BPosX + 192, BPosY + 54, 68, 11, AccentColor1Label, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(accentColor1Label);
        this.ConfigPanel01.add(accentColor1Label);
        // 270,54,45,11 - AccentColor1 Button
        ButtonWidgetOKey accentColor1Button = new ButtonWidgetOKey(BPosX + 270, BPosY + 54, 45, 11, RIGHT_CLICK_TO_MODIFY, button -> {
            this.openPanel(1);
        }, ButtonWidgetOKey.DEFAULT_NARRATION_SUPPLIER);
        accentColor1Button.canClick = ButtonWidgetOKey.RIGHT_CLICK;
        this.addDrawableChild(accentColor1Button);
        this.ConfigPanel01.add(accentColor1Button);
        // 192,69,68,11 - AccentColor2 Label
        TextWidget accentColor2Label = new TextWidget(BPosX + 192, BPosY + 69, 68, 11, AccentColor2Label, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(accentColor2Label);
        this.ConfigPanel01.add(accentColor2Label);
        // 270,69,45,11 - AccentColor2 Button
        ButtonWidgetOKey accentColor2Button = new ButtonWidgetOKey(BPosX + 270, BPosY + 69, 45, 11, RIGHT_CLICK_TO_MODIFY, button -> {
            this.openPanel(2);
        }, ButtonWidgetOKey.DEFAULT_NARRATION_SUPPLIER);
        accentColor2Button.canClick = ButtonWidgetOKey.RIGHT_CLICK;
        this.addDrawableChild(accentColor2Button);
        this.ConfigPanel01.add(accentColor2Button);
        // 192,84,68,11 - EyeColorA Label
        TextWidget eyeColorALabel = new TextWidget(BPosX + 192, BPosY + 84, 68, 11, EyeColorALabel, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(eyeColorALabel);
        this.ConfigPanel01.add(eyeColorALabel);
        // 270,84,45,11 - EyeColorA Button
        ButtonWidgetOKey eyeColorAButton = new ButtonWidgetOKey(BPosX + 270, BPosY + 84, 45, 11, RIGHT_CLICK_TO_MODIFY, button -> {
            this.openPanel(3);
        }, ButtonWidgetOKey.DEFAULT_NARRATION_SUPPLIER);
        eyeColorAButton.canClick = ButtonWidgetOKey.RIGHT_CLICK;
        this.addDrawableChild(eyeColorAButton);
        this.ConfigPanel01.add(eyeColorAButton);
        // 192,99,68,11 - EyeColorB Label
        TextWidget eyeColorBLabel = new TextWidget(BPosX + 192, BPosY + 99, 68, 11, EyeColorBLabel, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(eyeColorBLabel);
        this.ConfigPanel01.add(eyeColorBLabel);
        // 270,99,45,11 - EyeColorB Button
        ButtonWidgetOKey eyeColorBButton = new ButtonWidgetOKey(BPosX + 270, BPosY + 99, 45, 11, RIGHT_CLICK_TO_MODIFY, button -> {
            this.openPanel(4);
        }, ButtonWidgetOKey.DEFAULT_NARRATION_SUPPLIER);
        eyeColorBButton.canClick = ButtonWidgetOKey.RIGHT_CLICK;
        this.addDrawableChild(eyeColorBButton);
        this.ConfigPanel01.add(eyeColorBButton);
        // 177,114,101,12 - PrimaryGreyReverse Label
        TextWidget primaryGreyReverseLabel = new TextWidget(BPosX + 177, BPosY + 114, 101, 12, PrimaryGreyReverseLabel, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(primaryGreyReverseLabel);
        this.ConfigPanel01.add(primaryGreyReverseLabel);
        // 288,114,27,12 - PrimaryGreyReverse Button
        ButtonWidget primaryGreyReverseButton = ButtonWidget.builder(this.primaryGreyReverse ? BoolBTN_ON :BoolBTN_OFF, (button) -> {
            this.onData2ChangedOrClicked(5);
        }).position(BPosX + 288, BPosY + 114).size(27, 12).build();
        this.addDrawableChild(primaryGreyReverseButton);
        this.primaryGreyReverseButton = primaryGreyReverseButton;
        this.ConfigPanel01.add(primaryGreyReverseButton);
        // 177,130,101,12 - Accent1GreyReverse Label
        TextWidget accent1GreyReverseLabel = new TextWidget(BPosX + 177, BPosY + 130, 101, 12, Accent1GreyReverseLabel, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(accent1GreyReverseLabel);
        this.ConfigPanel01.add(accent1GreyReverseLabel);
        // 288,130,27,12 - Accent1GreyReverse Button
        ButtonWidget accent1GreyReverseButton = ButtonWidget.builder(this.accent1GreyReverse ? BoolBTN_ON :BoolBTN_OFF, (button) -> {
            this.onData2ChangedOrClicked(6);
        }).position(BPosX + 288, BPosY + 130).size(27, 12).build();
        this.addDrawableChild(accent1GreyReverseButton);
        this.accent1GreyReverseButton = accent1GreyReverseButton;
        this.ConfigPanel01.add(accent1GreyReverseButton);
        // 177,146,101,12 - Accent2GreyReverse Label
        TextWidget accent2GreyReverseLabel = new TextWidget(BPosX + 177, BPosY + 146, 101, 12, Accent2GreyReverseLabel, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(accent2GreyReverseLabel);
        this.ConfigPanel01.add(accent2GreyReverseLabel);
        // 288,146,27,12 - Accent2GreyReverse Button
        ButtonWidget accent2GreyReverseButton = ButtonWidget.builder(this.accent2GreyReverse ? BoolBTN_ON :BoolBTN_OFF, (button) -> {
            this.onData2ChangedOrClicked(7);
        }).position(BPosX + 288, BPosY + 146).size(27, 12).build();
        this.addDrawableChild(accent2GreyReverseButton);
        this.accent2GreyReverseButton = accent2GreyReverseButton;
        this.ConfigPanel01.add(accent2GreyReverseButton);
        // 177,176,101,11 - Keep Original Skin Label
        TextWidget keepOriginalSkinLabel = new TextWidget(BPosX + 177, BPosY + 176, 101, 11, KeepOriginalSkinLabel, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(keepOriginalSkinLabel);
        this.ConfigPanel01.add(keepOriginalSkinLabel);
        // 288,176,27,11 - Keep Original Skin Button
        ButtonWidget keepOriginalSkinButton = ButtonWidget.builder(this.keepCustomSkin ? BoolBTN_ON :BoolBTN_OFF, (button) -> {
            this.onData2ChangedOrClicked(8);
        }).position(BPosX + 288, BPosY + 176).size(27, 11).build();
        this.addDrawableChild(keepOriginalSkinButton);
        this.keepCustomSkinButton = keepOriginalSkinButton;
        this.ConfigPanel01.add(keepOriginalSkinButton);
        // 177,191,101,11 - Enable Form Color Label
        TextWidget enableFormColorLabel = new TextWidget(BPosX + 177, BPosY + 191, 101, 11, IsEnableFormColorSystemLabel, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(enableFormColorLabel);
        this.ConfigPanel01.add(enableFormColorLabel);
        // 288,191,27,11 - Enable Form Color Button
        ButtonWidget enableFormColorButton = ButtonWidget.builder(this.enableFormColorSystem ? BoolBTN_ON : BoolBTN_OFF, (button) -> {
            this.onData2ChangedOrClicked(9);
        }).position(BPosX + 288, BPosY + 191).size(27, 11).build();
        this.addDrawableChild(enableFormColorButton);
        this.enableFormColorSystemButton = enableFormColorButton;
        this.ConfigPanel01.add(enableFormColorButton);
        // Config Panel 02
        // 177,68,41,11 - Config Label
        TextWidget configLabel = new TextWidget(BPosX + 177, BPosY + 68, 41, 11, HEX_TEXT, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(configLabel);
        this.PanelConfigNameLabel = configLabel;
        this.ConfigPanel02.add(configLabel);
        // 222,68,93,11 - Config Input
        TextFieldWidget configInput = new TextFieldWidget(textRenderer, BPosX + 222, BPosY + 68, 93, 11, EmptyText);
        configInput.setMaxLength(9);
        configInput.setChangedListener((text) -> {
            this.onData2ChangedOrClicked(0);
        });
        this.addDrawableChild(configInput);
        this.SliderTextBox = configInput;
        this.ConfigPanel02.add(configInput);
        // 177,83,11,11 - R Label
        TextWidget rLabel = new TextWidget(BPosX + 177, BPosY + 83, 11, 11, ColorChannel_R, textRenderer);
        this.addDrawableChild(rLabel);
        this.ConfigPanel02.add(rLabel);
        // 177,98,11,11 - G Label
        TextWidget gLabel = new TextWidget(BPosX + 177, BPosY + 98, 11, 11, ColorChannel_G, textRenderer);
        this.addDrawableChild(gLabel);
        this.ConfigPanel02.add(gLabel);
        // 177,113,11,11 - B Label
        TextWidget bLabel = new TextWidget(BPosX + 177, BPosY + 113, 11, 11, ColorChannel_B, textRenderer);
        this.addDrawableChild(bLabel);
        this.ConfigPanel02.add(bLabel);
        // 177,128,11,11 - H Label
        TextWidget hLabel = new TextWidget(BPosX + 177, BPosY + 128, 11, 11, ColorChannel_H, textRenderer);
        this.addDrawableChild(hLabel);
        this.ConfigPanel02.add(hLabel);
        // 177,143,11,11 - S Label
        TextWidget sLabel = new TextWidget(BPosX + 177, BPosY + 143, 11, 11, ColorChannel_S, textRenderer);
        this.addDrawableChild(sLabel);
        this.ConfigPanel02.add(sLabel);
        // 177,158,11,11 - V Label
        TextWidget vLabel = new TextWidget(BPosX + 177, BPosY + 158, 11, 11, ColorChannel_V, textRenderer);
        this.addDrawableChild(vLabel);
        this.ConfigPanel02.add(vLabel);
        // 192,83,26,11 - R Input
        TextFieldWidget rInput = new TextFieldWidget(textRenderer, BPosX + 192, BPosY + 83, 26, 11, EmptyText);
        rInput.setMaxLength(3);
        rInput.setChangedListener((text) -> {
            this.onData4ChangedOrClicked(0);
        });
        this.addDrawableChild(rInput);
        this.SliderRTextBox = rInput;
        this.ConfigPanel02.add(rInput);
        // 192,98,26,11 - G Input
        TextFieldWidget gInput = new TextFieldWidget(textRenderer, BPosX + 192, BPosY + 98, 26, 11, EmptyText);
        gInput.setMaxLength(3);
        gInput.setChangedListener((text) -> {
            this.onData4ChangedOrClicked(1);
        });
        this.addDrawableChild(gInput);
        this.SliderGTextBox = gInput;
        this.ConfigPanel02.add(gInput);
        // 192,113,26,11 - B Input
        TextFieldWidget bInput = new TextFieldWidget(textRenderer, BPosX + 192, BPosY + 113, 26, 11, EmptyText);
        bInput.setMaxLength(3);
        bInput.setChangedListener((text) -> {
            this.onData4ChangedOrClicked(2);
        });
        this.addDrawableChild(bInput);
        this.SliderBTextBox = bInput;
        this.ConfigPanel02.add(bInput);
        // 192,128,26,11 - H Input
        TextFieldWidget hInput = new TextFieldWidget(textRenderer, BPosX + 192, BPosY + 128, 26, 11, EmptyText);
        hInput.setMaxLength(3);
        hInput.setChangedListener((text) -> {
            this.onData4ChangedOrClicked(3);
        });
        this.addDrawableChild(hInput);
        this.SliderHTextBox = hInput;
        this.ConfigPanel02.add(hInput);
        // 192,143,26,11 - S Input
        TextFieldWidget sInput = new TextFieldWidget(textRenderer, BPosX + 192, BPosY + 143, 26, 11, EmptyText);
        sInput.setMaxLength(3);
        sInput.setChangedListener((text) -> {
            this.onData4ChangedOrClicked(4);
        });
        this.addDrawableChild(sInput);
        this.SliderSTextBox = sInput;
        this.ConfigPanel02.add(sInput);
        // 192,158,26,11 - V Input
        TextFieldWidget vInput = new TextFieldWidget(textRenderer, BPosX + 192, BPosY + 158, 26, 11, EmptyText);
        vInput.setMaxLength(3);
        vInput.setChangedListener((text) -> {
            this.onData4ChangedOrClicked(5);
        });
        this.addDrawableChild(vInput);
        this.SliderVTextBox = vInput;
        this.ConfigPanel02.add(vInput);
        // 222,85,93,7 - R Slider
        SimpleIntSliderWidget rSlider = new SimpleIntSliderWidget(BPosX + 222, BPosY + 85, 93, 7, EmptyText, 0d, 0, 255);
        rSlider.onChanged = ((value) -> {
            this.onData5Changed(0);
        });
        this.addDrawableChild(rSlider);
        this.SliderRSlider = rSlider;
        this.ConfigPanel02.add(rSlider);
        // 222,100,93,7 - G Slider
        SimpleIntSliderWidget gSlider = new SimpleIntSliderWidget(BPosX + 222, BPosY + 100, 93, 7, EmptyText, 0d, 0, 255);
        gSlider.onChanged = ((value) -> {
            this.onData5Changed(1);
        });
        this.addDrawableChild(gSlider);
        this.SliderGSlider = gSlider;
        this.ConfigPanel02.add(gSlider);
        // 222,115,93,7 - B Slider
        SimpleIntSliderWidget bSlider = new SimpleIntSliderWidget(BPosX + 222, BPosY + 115, 93, 7, EmptyText, 0d, 0, 255);
        bSlider.onChanged = ((value) -> {
            this.onData5Changed(2);
        });
        this.addDrawableChild(bSlider);
        this.SliderBSlider = bSlider;
        this.ConfigPanel02.add(bSlider);
        // 222,130,93,7 - H Slider
        SimpleIntSliderWidget hSlider = new SimpleIntSliderWidget(BPosX + 222, BPosY + 130, 93, 7, EmptyText, 0d, 0, 359);
        hSlider.onChanged = ((value) -> {
            this.onData5Changed(3);
        });
        this.addDrawableChild(hSlider);
        this.SliderHSlider = hSlider;
        this.ConfigPanel02.add(hSlider);
        // 222,145,93,7 - S Slider
        SimpleIntSliderWidget sSlider = new SimpleIntSliderWidget(BPosX + 222, BPosY + 145, 93, 7, EmptyText, 0d, 0, 100);
        sSlider.onChanged = ((value) -> {
            this.onData5Changed(4);
        });
        this.addDrawableChild(sSlider);
        this.SliderSSlider = sSlider;
        this.ConfigPanel02.add(sSlider);
        // 222,160,93,7 - V Slider
        SimpleIntSliderWidget vSlider = new SimpleIntSliderWidget(BPosX + 222, BPosY + 160, 93, 7, EmptyText, 0d, 0, 100);
        vSlider.onChanged = ((value) -> {
            this.onData5Changed(5);
        });
        this.addDrawableChild(vSlider);
        this.SliderVSlider = vSlider;
        this.ConfigPanel02.add(vSlider);
        // 177,174,101,12 - Is Enable Layer Label
        TextWidget isEnableLayerLabel = new TextWidget(BPosX + 177, BPosY + 174, 101, 12, IsEnableLayerLabel, textRenderer).setTextColor(TextColor);
        this.addDrawableChild(isEnableLayerLabel);
        this.ConfigPanel02.add(isEnableLayerLabel);
        // 288,174,27,12 - Is Enable Layer Button
        ButtonWidget isEnableLayerButton = ButtonWidget.builder(this.SliderA != 0 ? BoolBTN_ON : BoolBTN_OFF, (button) -> {
            this.onData4ChangedOrClicked(6);
        }).position(BPosX + 288, BPosY + 174).size(27, 12).build();
        this.addDrawableChild(isEnableLayerButton);
        this.SliderAButton = isEnableLayerButton;
        this.ConfigPanel02.add(isEnableLayerButton);
        // 255,190,60,12 - Exit Slider Button
        ButtonWidget exitSliderButton = ButtonWidget.builder(ExitSliderButtonLabel, (button) -> {
            this.closePanel();
        }).position(BPosX + 255, BPosY + 190).size(60, 12).build();
        this.addDrawableChild(exitSliderButton);
        this.ConfigPanel02.add(exitSliderButton);

        this.globalSlotButton.clear();
        this.globalSlotNameInputs.clear();

        // 330,37,70,15 - global_form_slot_1
        this.createSaveDataButtons(0, BPosX + 330, BPosY + 37);
        // 330,56,70,15 - global_form_slot_2
        this.createSaveDataButtons(1, BPosX + 330, BPosY + 56);
        // 330,75,70,15 - global_form_slot_3
        this.createSaveDataButtons(2, BPosX + 330, BPosY + 75);
        // 330,94,70,15 - global_form_slot_4
        this.createSaveDataButtons(3, BPosX + 330, BPosY + 94);
        // 330,113,70,15 - global_form_slot_5
        this.createSaveDataButtons(4, BPosX + 330, BPosY + 113);
        // 330,132,70,15 - global_form_slot_6
        this.createSaveDataButtons(5, BPosX + 330, BPosY + 132);
        // 330,151,70,15 - global_form_slot_7
        this.createSaveDataButtons(6, BPosX + 330, BPosY + 151);
        // 330,170,70,15 - global_form_slot_8
        this.createSaveDataButtons(7, BPosX + 330, BPosY + 170);
        // 330,189,70,15 - global_form_slot_9
        this.createSaveDataButtons(8, BPosX + 330, BPosY + 189);

        this.isScreenInit = true;
        this.updatePanel();
    }

    private void RenderEntity(DrawContext context, int x, int y, int size, int mouseX, int mouseY, LivingEntity entity) {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float g = (float)Math.atan((double)(mouseY / 40.0F));
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        float m = entity.prevBodyYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.prevBodyYaw = entity.bodyYaw;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        InventoryScreen.drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
        entity.bodyYaw = h;
        entity.prevBodyYaw = m;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    private void drawExtraPart(DrawContext context, int x, int y, int PartX, int PartY, int Width, int Height) {
        int realX = PartX + EXTRA_PART_START_X;
        int realY = PartY + EXTRA_PART_START_Y;
        context.drawTexture(BG_TEXTURE, x, y, realX, realY, Width, Height, BG_IMAGE_WIDTH, BG_IMAGE_HEIGHT);
    }

    public void renderTextureBackground(DrawContext context) {
        int BG_X = width / 2 - BG_WIDTH / 2;
        int BG_Y = height / 2 - BG_HEIGHT / 2;
        context.drawTexture(BG_TEXTURE, BG_X, BG_Y, 0, 0, BG_WIDTH, BG_HEIGHT, BG_IMAGE_WIDTH, BG_IMAGE_HEIGHT);
        if (!isOpenPanel02) {
            // 172,34,148,173,0,0
            this.drawExtraPart(context, BG_X + 172, BG_Y + 34, 0, 0, 148, 173);
        } else {
            // 172,34,148,173,149,0
            this.drawExtraPart(context, BG_X + 172, BG_Y + 34, 149, 0, 148, 173);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int BPosX = width / 2 - BG_WIDTH / 2;
        int BPosY = height / 2 - BG_HEIGHT / 2;
        this.renderBackground(context);
        this.renderTextureBackground(context);

        if (!isOpenPanel02) {
            // 177,39,11,11
            context.fill(BPosX + 177, BPosY + 39, BPosX + 188, BPosY + 50, this.primaryColor);
            // 177,54,11,11
            context.fill(BPosX + 177, BPosY + 54, BPosX + 188, BPosY + 65, this.accentColor1Color);
            // 177,69,11,11
            context.fill(BPosX + 177, BPosY + 69, BPosX + 188, BPosY + 80, this.accentColor2Color);
            // 177,84,11,11
            context.fill(BPosX + 177, BPosY + 84, BPosX + 188, BPosY + 95, this.eyeColorA);
            // 177,99,11,11
            context.fill(BPosX + 177, BPosY + 99, BPosX + 188, BPosY + 110, this.eyeColorB);
        } else {
            // 177,39,24,25
            context.fill(BPosX + 177, BPosY + 39, BPosX + 201, BPosY + 64, (this.SliderA << 24) | (this.SliderR << 16) | (this.SliderG << 8) | (this.SliderB));
        }
        if (timer > 60) {
            this.updateSavaButtonActive();
        } else {
            timer++;
        }
        // 20,34,142,157
        if (minecraftClient.player != null) {
            RenderEntity(context, BPosX + 91, BPosY + 156, 50, BPosX + 91 - mouseX, BPosY + 96 - mouseY, minecraftClient.player);
        }
        super.render(context, mouseX, mouseY, delta);
    }


    public void saveDataToClient(boolean savaColorData, boolean saveExtraData) {
        if (savaColorData) {
            ShapeShifterCurseFabric.playerCustomConfig.primaryColor = primaryColor;
            ShapeShifterCurseFabric.playerCustomConfig.accentColor1Color = accentColor1Color;
            ShapeShifterCurseFabric.playerCustomConfig.accentColor2Color = accentColor2Color;
            ShapeShifterCurseFabric.playerCustomConfig.eyeColorA = eyeColorA;
            ShapeShifterCurseFabric.playerCustomConfig.eyeColorB = eyeColorB;
            ShapeShifterCurseFabric.playerCustomConfig.primaryGreyReverse = primaryGreyReverse;
            ShapeShifterCurseFabric.playerCustomConfig.accent1GreyReverse = accent1GreyReverse;
            ShapeShifterCurseFabric.playerCustomConfig.accent2GreyReverse = accent2GreyReverse;
        }
        if (saveExtraData) {
            ShapeShifterCurseFabric.playerCustomConfig.keep_original_skin = keepCustomSkin;
            ShapeShifterCurseFabric.playerCustomConfig.enable_form_color = enableFormColorSystem;
        }
        AutoConfig.getConfigHolder(PlayerCustomConfig.class).save();
    }

    @Override
    public void close() {
        CleanColorSettingCache();
        if (this.isLockTempTextureSystem) {
            FormTextureUtils.useTempFormTexture = false;
            FormTextureUtils.tempFormTextureProcessor = null;
            isLockTempTextureSystem = false;
        }
        if (this.isLockTempModelSystem) {
            FormTextureUtils.useTempFormModel = false;
            FormTextureUtils.tempFormModelProcessor = null;
            isLockTempModelSystem = false;
        }
        if (this.isLockTempConfigSystem) {
            FormTextureUtils.useTempCustomSkinConfig = false;
            FormTextureUtils.tempCustomSkinConfigOverrider = null;
            isLockTempConfigSystem = false;
        }
        instance = null;
        try {
            ModPacketsS2C.sendUpdateCustomColor(this.getColorSetting(false), false, true, this.keepCustomSkin,  this.enableFormColorSystem); // 如果没进游戏时会发送失败 懒得做判断了 加一个Try
        } catch (Exception ignored) {
        }
        this.saveDataToClient(true, true);
        ShapeShifterCurseFabricClient.formColorData.writeToConfig();
        if (this.parsetScreen != null && this.client != null) {
            this.client.setScreen(this.parsetScreen);
        } else {
            super.close();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int BPosX = width / 2 - BG_WIDTH / 2;
        int BPosY = height / 2 - BG_HEIGHT / 2;
        if (!this.isOpenPanel02 && this.isScreenInit) {
            // 177,39,11,11
            if (mouseX > BPosX + 177 && mouseX < BPosX + 188 && mouseY > BPosY + 39 && mouseY < BPosY + 50) {
                this.openPanel(0);
            } else
            // 177,54,11,11
            if (mouseX > BPosX + 177 && mouseX < BPosX + 188 && mouseY > BPosY + 54 && mouseY < BPosY + 65) {
                this.openPanel(1);
            } else
            // 177,69,11,11
            if (mouseX > BPosX + 177 && mouseX < BPosX + 188 && mouseY > BPosY + 69 && mouseY < BPosY + 80) {
                this.openPanel(2);
            } else
            // 177,84,11,11
            if (mouseX > BPosX + 177 && mouseX < BPosX + 188 && mouseY > BPosY + 84 && mouseY < BPosY + 95) {
                this.openPanel(3);
            } else
            // 177,99,11,11
            if (mouseX > BPosX + 177 && mouseX < BPosX + 188 && mouseY > BPosY + 99 && mouseY < BPosY + 110) {
                this.openPanel(4);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // 实时形态颜色显示系统
    private Identifier getNextDynamicFormID() {
        return new Identifier(identifierNameSpace, identifierPrefix + nowColorSettingIndex++);
    }

    private void CleanColorSettingCache() {
        TextureManager textureManager = minecraftClient.getTextureManager();
        for (Identifier id : colorSettingCacheMap.values().stream().flatMap(map -> map.values().stream()).toList()) {
            textureManager.destroyTexture(id);
        }
        colorSettingCacheMap.clear();
    }

    @Override
    public Identifier getTexture(int modelID, String category, Identifier texture, Identifier mask, boolean OnlyMultiply) {
        if (this.modelID != modelID) {
            this.modelID = modelID;
            CleanColorSettingCache();
        }
        // 可以关闭这个功能
        if (!this.enableFormColorSystem) {
            return texture;
        }
        return colorSettingCacheMap.computeIfAbsent(category, k -> new HashMap<>()).computeIfAbsent(this.getColorSetting(true), k -> {
            // 这种方法不会内存泄漏 但是得自己管理临时材质
            NativeImageBackedTexture nativeImageBackedTexture = FormTextureUtils.BakeTextureNoMemLeak(texture, mask, this.getColorSetting(true), OnlyMultiply);
            Identifier id = getNextDynamicFormID();
            minecraftClient.getTextureManager().registerTexture(id, nativeImageBackedTexture);
            return id;
        });
    }

    @Override
    public boolean keepOriginalSkin() {
        return this.keepCustomSkin;
    }
}
