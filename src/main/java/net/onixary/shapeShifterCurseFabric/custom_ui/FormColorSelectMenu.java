package net.onixary.shapeShifterCurseFabric.custom_ui;

import blue.endless.jankson.annotation.Nullable;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.config.PlayerCustomConfig;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.FCS_ButtonWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.SimpleIntSliderWidget;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.util.FormColorData;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class FormColorSelectMenu extends Screen implements FormTextureUtils.TempTextureProcessor {
    private static final Identifier texture = new Identifier(MOD_ID,"textures/gui/form_color_select_menu.png");
    private static final int BG_WIDTH = 420;
    private static final int BG_HEIGHT = 227;

    public static FormColorSelectMenu instance = null;

    private static final Text EmptyText = Text.empty();
    private static final Text BoolBTN_ON = Text.translatable("text.cloth-config.boolean.value.true");
    private static final Text BoolBTN_OFF = Text.translatable("text.cloth-config.boolean.value.false");

    // Label
    private static final Text FormSlotTitle = Text.translatable("gui.shape_shifter_curse_fabric.fcs.form_slot_title");
    private static final Text GlobalSlotTitle = Text.translatable("gui.shape_shifter_curse_fabric.fcs.global_slot_title");
    private static final Text FormDefaultSlotTitle = Text.translatable("gui.shape_shifter_curse_fabric.fcs.form_default_slot_title");
    private static final Text Title = Text.translatable("gui.shape_shifter_curse_fabric.fcs.title");

    private static final Text ColorChannel_R = Text.translatable("gui.shape_shifter_curse_fabric.fcs.color_channel_r");
    private static final Text ColorChannel_G = Text.translatable("gui.shape_shifter_curse_fabric.fcs.color_channel_g");
    private static final Text ColorChannel_B = Text.translatable("gui.shape_shifter_curse_fabric.fcs.color_channel_b");

    private static final Text IsEnableLayerLabel = Text.translatable("gui.shape_shifter_curse_fabric.fcs.is_enable_layer");
    private static final Text ExitSliderButtonLabel = Text.translatable("gui.shape_shifter_curse_fabric.fcs.exit_slider_button");

    // Button
    private static final Text DownloadFromServer = Text.translatable("gui.shape_shifter_curse_fabric.fcs.from_server");
    private static final Text UploadToServer = Text.translatable("gui.shape_shifter_curse_fabric.fcs.to_server");
    private static final Text DownloadFromClient = Text.translatable("gui.shape_shifter_curse_fabric.fcs.from_client");
    private static final Text UploadToClient = Text.translatable("gui.shape_shifter_curse_fabric.fcs.to_client");
    private static final Text DownloadFromClipboard = Text.translatable("gui.shape_shifter_curse_fabric.fcs.from_clipboard");
    private static final Text UploadToClipboard = Text.translatable("gui.shape_shifter_curse_fabric.fcs.to_clipboard");

    // Config Entry
    private static final Text PrimaryColorLabel = Text.translatable("text.autoconfig.shape-shifter-curse-custom.option.primaryColor");
    private static final Text AccentColor1Label = Text.translatable("text.autoconfig.shape-shifter-curse-custom.option.accentColor1Color");
    private static final Text AccentColor2Label = Text.translatable("text.autoconfig.shape-shifter-curse-custom.option.accentColor2Color");
    private static final Text EyeColorALabel = Text.translatable("text.autoconfig.shape-shifter-curse-custom.option.eyeColorA");
    private static final Text EyeColorBLabel = Text.translatable("text.autoconfig.shape-shifter-curse-custom.option.eyeColorB");
    private static final Text PrimaryGreyReverseLabel = Text.translatable("text.autoconfig.shape-shifter-curse-custom.option.primaryGreyReverse");
    private static final Text Accent1GreyReverseLabel = Text.translatable("text.autoconfig.shape-shifter-curse-custom.option.accent1GreyReverse");
    private static final Text Accent2GreyReverseLabel = Text.translatable("text.autoconfig.shape-shifter-curse-custom.option.accent2GreyReverse");

    private boolean isScreenInit = false;
    private TextFieldWidget primaryColorEditBox = null;
    private TextFieldWidget accentColor1EditBox = null;
    private TextFieldWidget accentColor2EditBox = null;
    private TextFieldWidget eyeColorAEditBox = null;
    private TextFieldWidget eyeColorBEditBox = null;
    private ButtonWidget primaryGreyReverseButton = null;
    private ButtonWidget accent1GreyReverseButton = null;
    private ButtonWidget accent2GreyReverseButton = null;

    private SimpleIntSliderWidget sliderR = null;
    private SimpleIntSliderWidget sliderG = null;
    private SimpleIntSliderWidget sliderB = null;
    private TextFieldWidget sliderREditBox = null;
    private TextFieldWidget sliderGEditBox = null;
    private TextFieldWidget sliderBEditBox = null;
    private ButtonWidget isEnableLayerButton = null;

    private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    private final HashMap<String, HashMap<FormTextureUtils.ColorSetting, Identifier>> colorSettingCacheMap = new HashMap<>();  // 防止内存泄漏
    private int modelID = -1;
    private static final String IdentifierNameSpace = MOD_ID;
    private static final String IdentifierPrefix = "dynamic_fcs_";
    private static long nowColorSettingIndex = 0;  // 自增ID

    private Identifier getNextDynamicFormID() {
        return new Identifier(IdentifierNameSpace, IdentifierPrefix + nowColorSettingIndex++);
    }

    private void CleanColorSettingCache() {
        TextureManager textureManager = minecraftClient.getTextureManager();
        for (Identifier id : colorSettingCacheMap.values().stream().flatMap(map -> map.values().stream()).toList()) {
            textureManager.destroyTexture(id);
        }
        colorSettingCacheMap.clear();
    }

    // 顺序是 ARGB
    private int primaryColor = 0x00FFFFFF;
    private int accentColor1Color = 0x00FFFFFF;
    private int accentColor2Color = 0x00FFFFFF;
    private int eyeColorA = 0x00FFFFFF;
    private int eyeColorB = 0x00FFFFFF;
    private boolean primaryGreyReverse = false;
    private boolean accent1GreyReverse = false;
    private boolean accent2GreyReverse = false;

    private boolean isColorSettingDirty = true;
    private FormTextureUtils.ColorSetting colorSetting_ARGB = null;
    private FormTextureUtils.ColorSetting colorSetting_ABGR = null;

    private int tempSliderConfigIndex = -1;
    private int tempSliderR = 0;
    private int tempSliderG = 0;
    private int tempSliderB = 0;
    private int tempSliderAlpha = 0;

    private boolean isOpenSlider = false;
    private List<ClickableWidget> config_panel_01 = new ArrayList<>();  // 保存config输入框 label之类的 用于切换
    private List<ClickableWidget> config_panel_02 = new ArrayList<>();  // 保存 RGB条 一些按钮

    // 修改 tempSliderX 后调用
    public void updateSlider() {
        int Color = tempSliderAlpha << 24 | tempSliderR << 16 | tempSliderG << 8 | tempSliderB;
        switch (tempSliderConfigIndex) {
            case 0 -> primaryColor = Color;
            case 1 -> accentColor1Color = Color;
            case 2 -> accentColor2Color = Color;
            case 3 -> eyeColorA = Color;
            case 4 -> eyeColorB = Color;
        }
        isColorSettingDirty = true;
    }

    // 非条修改颜色后调用
    public void reloadSlider() {
        int sliderColor = 0;
        switch (tempSliderConfigIndex) {
            case 0 -> sliderColor = primaryColor;
            case 1 -> sliderColor = accentColor1Color;
            case 2 -> sliderColor = accentColor2Color;
            case 3 -> sliderColor = eyeColorA;
            case 4 -> sliderColor = eyeColorB;
        }
        tempSliderR = (sliderColor >>> 16) & 0xFF;
        tempSliderG = (sliderColor >>> 8) & 0xFF;
        tempSliderB = sliderColor & 0xFF;
        tempSliderAlpha = (sliderColor >>> 24) & 0xFF;
        sliderR.setIntValue(tempSliderR);
        sliderG.setIntValue(tempSliderG);
        sliderB.setIntValue(tempSliderB);
        isEnableLayerButton.setMessage(tempSliderAlpha != 0 ? BoolBTN_ON : BoolBTN_OFF);
    }

    public void updatePanel() {
        if (isOpenSlider) {
            config_panel_01.forEach(element -> element.visible = false);
            config_panel_02.forEach(element -> element.visible = true);
        } else {
            config_panel_01.forEach(element -> element.visible = true);
            config_panel_02.forEach(element -> element.visible = false);
        }
        this.updateUI();
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
        this.updateUI();
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
        this.updateUI();
    }

    public void loadData() {
        if (minecraftClient.player != null) {
            loadData(true);
        } else {
            loadData(false);
        }
    }

    public void loadData(boolean serverSide) {
        if (serverSide) {
            if (minecraftClient.player != null) {
                FormTextureUtils.ColorSetting colorSetting = RegPlayerSkinComponent.SKIN_SETTINGS.get(minecraftClient.player).getFormColor();
                this.loadServerData(colorSetting);
            }
        } else {
            primaryColor = ShapeShifterCurseFabric.playerCustomConfig.primaryColor;
            accentColor1Color = ShapeShifterCurseFabric.playerCustomConfig.accentColor1Color;
            accentColor2Color = ShapeShifterCurseFabric.playerCustomConfig.accentColor2Color;
            eyeColorA = ShapeShifterCurseFabric.playerCustomConfig.eyeColorA;
            eyeColorB = ShapeShifterCurseFabric.playerCustomConfig.eyeColorB;
            primaryGreyReverse = ShapeShifterCurseFabric.playerCustomConfig.primaryGreyReverse;
            accent1GreyReverse = ShapeShifterCurseFabric.playerCustomConfig.accent1GreyReverse;
            accent2GreyReverse = ShapeShifterCurseFabric.playerCustomConfig.accent2GreyReverse;
            this.updateUI();
        }
        isColorSettingDirty = true;
    }

    public @NotNull FormTextureUtils.ColorSetting getColorSetting(boolean ABGR) {
        if (isColorSettingDirty) {
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
                    FormTextureUtils.ARGB2ABGR(eyeColorA),
                    FormTextureUtils.ARGB2ABGR(eyeColorB),
                    primaryGreyReverse,
                    accent1GreyReverse,
                    accent2GreyReverse
            );
            isColorSettingDirty = false;
        }
        return ABGR ? colorSetting_ABGR : colorSetting_ARGB;
    }

    private boolean isUsingTempTexture = true;

    public FormColorSelectMenu(Text title) {
        super(title);
        loadData();
        if (!FormTextureUtils.useTempTexture) {
            FormTextureUtils.useTempTexture = true;
            FormTextureUtils.tempTextureProcessor = this;
        } else {
            ShapeShifterCurseFabric.LOGGER.warn("Temp Texture System is already in use, dynamic texture rendering will not work");
            isUsingTempTexture = false;
        }
        if (instance != null) {
            ShapeShifterCurseFabric.LOGGER.error("FormColorSelectMenu is already in use, only one instance is allowed");
        }
        instance = this;
    }

    public void renderTextureBackground(DrawContext context) {
        int BG_X = width / 2 - BG_WIDTH / 2;
        int BG_Y = height / 2 - BG_HEIGHT / 2;
        context.drawTexture(texture, BG_X, BG_Y, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
    }

    public int colorChannel2Int(String channel) {
        try {
            int value = Integer.parseInt(channel);
            if (value > 255) {
                return 255;
            }
            if (value < 0) {
                return 0;
            }
            return value;
        } catch (Exception ignored) {
            return 0;
        }
    }

    public int decodeColor(String Color) {
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

    public String encodeColor(int Color) {
        return String.format(Locale.ROOT, "#%08X", Color);
    }

    private boolean isUpdateUI = false;
    private boolean isUpdateSlider = false;

    public void onConfigChanged() {
        if (!this.isScreenInit || isUpdateUI) {
            return;
        }
        this.primaryColor = decodeColor(this.primaryColorEditBox.getText());
        this.accentColor1Color = decodeColor(this.accentColor1EditBox.getText());
        this.accentColor2Color = decodeColor(this.accentColor2EditBox.getText());
        this.eyeColorA = decodeColor(this.eyeColorAEditBox.getText());
        this.eyeColorB = decodeColor(this.eyeColorBEditBox.getText());
        this.primaryGreyReverse = primaryGreyReverseButton.getMessage().equals(BoolBTN_ON);
        this.accent1GreyReverse = accent1GreyReverseButton.getMessage().equals(BoolBTN_ON);
        this.accent2GreyReverse = accent2GreyReverseButton.getMessage().equals(BoolBTN_ON);
        this.isColorSettingDirty = true;
    }

    public void updateUI() {
        if (!this.isScreenInit) {
            return;
        }
        this.isUpdateUI = true;
        this.primaryColorEditBox.setText(encodeColor(this.primaryColor));
        this.accentColor1EditBox.setText(encodeColor(this.accentColor1Color));
        this.accentColor2EditBox.setText(encodeColor(this.accentColor2Color));
        this.eyeColorAEditBox.setText(encodeColor(this.eyeColorA));
        this.eyeColorBEditBox.setText(encodeColor(this.eyeColorB));
        this.primaryGreyReverseButton.setMessage(this.primaryGreyReverse ? BoolBTN_ON : BoolBTN_OFF);
        this.accent1GreyReverseButton.setMessage(this.accent1GreyReverse ? BoolBTN_ON : BoolBTN_OFF);
        this.accent2GreyReverseButton.setMessage(this.accent2GreyReverse ? BoolBTN_ON : BoolBTN_OFF);
        this.reloadSlider();
        this.reloadAllSlotName();
        this.isUpdateUI = false;
    }

    public void onFormChange() {
        if (!this.isScreenInit) {
            return;
        }
        this.loadData();
    }

    public static void onFormChange_STATIC() {
        if (instance != null) {
            instance.onFormChange();
        }
    }

    @Override
    public void init() {
        // 格式:
        // X,Y,Width,Height - WidgetDesc
        // 推荐使用AI自动补全修改 先注释掉原来的代码 重新写那个位置大小注释 AI大概率能正确填充

        super.init();
        int BPosX = width / 2 - BG_WIDTH / 2;  // 图片左上角 X
        int BPosY = height / 2 - BG_HEIGHT / 2;  // 图片左上角 Y
        // Label
        // 20,128,80,9 - 形态4槽
        this.addDrawableChild(new TextWidget(BPosX + 20, BPosY + 128, 80, 9, FormSlotTitle, textRenderer).setTextColor(0xDDDDDD));
        // 135,5,180,9 - Title
        this.addDrawableChild(new TextWidget(BPosX + 135, BPosY + 5, 180, 9, title, textRenderer).setTextColor(0xDDDDDD));
        // 320,5,80,9 - 全局9槽
        this.addDrawableChild(new TextWidget(BPosX + 320, BPosY + 5, 80, 9, GlobalSlotTitle, textRenderer).setTextColor(0xDDDDDD));
        // 320,182,80,9 - 形态默认槽
        this.addDrawableChild(new TextWidget(BPosX + 320, BPosY + 182, 80, 9, FormDefaultSlotTitle, textRenderer).setTextColor(0xDDDDDD));
        // Normal Button
        // 85,5,45,15 - 获取服务器数据
        this.addDrawableChild(ButtonWidget.builder(DownloadFromServer, button -> {
            loadData(true);
        }).position(BPosX + 85, BPosY + 5).size(45, 15).build()
        );
        // 85,23,45,15 - 发送到服务器
        this.addDrawableChild(ButtonWidget.builder(UploadToServer, button -> {
            ModPacketsS2C.sendUpdateCustomSetting(this.getColorSetting(false));
        }).position(BPosX + 85, BPosY + 23).size(45, 15).build()
        );
        // 85,41,45,15 - 获取客户端数据(配置)
        this.addDrawableChild(ButtonWidget.builder(DownloadFromClient, button -> {
            loadData(false);
        }).position(BPosX + 85, BPosY + 41).size(45, 15).build()
        );
        // 85,59,45,15 - 发送到客户端(配置)
        this.addDrawableChild(ButtonWidget.builder(UploadToClient, button -> {
            ShapeShifterCurseFabric.playerCustomConfig.primaryColor = primaryColor;
            ShapeShifterCurseFabric.playerCustomConfig.accentColor1Color = accentColor1Color;
            ShapeShifterCurseFabric.playerCustomConfig.accentColor2Color = accentColor2Color;
            ShapeShifterCurseFabric.playerCustomConfig.eyeColorA = eyeColorA;
            ShapeShifterCurseFabric.playerCustomConfig.eyeColorB = eyeColorB;
            ShapeShifterCurseFabric.playerCustomConfig.primaryGreyReverse = primaryGreyReverse;
            ShapeShifterCurseFabric.playerCustomConfig.accent1GreyReverse = accent1GreyReverse;
            ShapeShifterCurseFabric.playerCustomConfig.accent2GreyReverse = accent2GreyReverse;
            AutoConfig.getConfigHolder(PlayerCustomConfig.class).save();
        }).position(BPosX + 85, BPosY + 59).size(45, 15).build()
        );
        // 85,77,45,15 - 从剪切板获取
        this.addDrawableChild(ButtonWidget.builder(DownloadFromClipboard, button -> {
            String keyBoardData = minecraftClient.keyboard.getClipboard();
            FormTextureUtils.ColorSetting cs = FormColorData.ColorSettingFormString(keyBoardData);
            if (cs != null) {
                this.loadData(cs);
            }
        }).position(BPosX + 85, BPosY + 77).size(45, 15).build()
        );
        // 85,95,45,15 - 发送到剪切板
        this.addDrawableChild(ButtonWidget.builder(UploadToClipboard, button -> {
            String keyBoardData = FormColorData.ColorSettingtoString(this.getColorSetting(false), true);
            if (keyBoardData == null) {
                return;
            }
            minecraftClient.keyboard.setClipboard(keyBoardData);
        }).position(BPosX + 85, BPosY + 95).size(45, 15).build()
        );
        // Config Pair
        // 139,27,75,11 - PrimaryColor Label
        TextWidget primaryColorLabel = new TextWidget(BPosX + 139, BPosY + 27, 75, 11, PrimaryColorLabel, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(primaryColorLabel);
        this.config_panel_01.add(primaryColorLabel);
        // 241,27,70,11 - PrimaryColor Input
        TextFieldWidget primaryColorInput = new TextFieldWidget(this.textRenderer, BPosX + 241, BPosY + 27, 70, 11, null, EmptyText);
        primaryColorInput.setMaxLength(9);
        primaryColorInput.setChangedListener((text) -> {
            this.onConfigChanged();
        });
        this.addDrawableChild(primaryColorInput);
        this.config_panel_01.add(primaryColorInput);
        this.primaryColorEditBox = primaryColorInput;
        // 139,41,75,11 - AccentColor1 Label
        TextWidget accentColor1Label = new TextWidget(BPosX + 139, BPosY + 41, 75, 11, AccentColor1Label, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(accentColor1Label);
        this.config_panel_01.add(accentColor1Label);
        // 241,41,70,11 - AccentColor1 Input
        TextFieldWidget accentColor1Input = new TextFieldWidget(this.textRenderer, BPosX + 241, BPosY + 41, 70, 11, null, EmptyText);
        accentColor1Input.setMaxLength(9);
        accentColor1Input.setChangedListener((text) -> {
            this.onConfigChanged();
        });
        this.addDrawableChild(accentColor1Input);
        this.config_panel_01.add(accentColor1Input);
        this.accentColor1EditBox = accentColor1Input;
        // 139,55,75,11 - AccentColor2 Label
        TextWidget accentColor2Label = new TextWidget(BPosX + 139, BPosY + 55, 75, 11, AccentColor2Label, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(accentColor2Label);
        this.config_panel_01.add(accentColor2Label);
        // 241,55,70,11 - AccentColor2 Input
        TextFieldWidget accentColor2Input = new TextFieldWidget(this.textRenderer, BPosX + 241, BPosY + 55, 70, 11, null, EmptyText);
        accentColor2Input.setMaxLength(9);
        accentColor2Input.setChangedListener((text) -> {
            this.onConfigChanged();
        });
        this.addDrawableChild(accentColor2Input);
        this.config_panel_01.add(accentColor2Input);
        this.accentColor2EditBox = accentColor2Input;
        // 139,69,75,11 - EyeColorA Label
        TextWidget eyeColorALabel = new TextWidget(BPosX + 139, BPosY + 69, 75, 11, EyeColorALabel, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(eyeColorALabel);
        this.config_panel_01.add(eyeColorALabel);
        // 241,69,70,11 - EyeColorA Input
        TextFieldWidget eyeColorAInput = new TextFieldWidget(this.textRenderer, BPosX + 241, BPosY + 69, 70, 11, null, EmptyText);
        eyeColorAInput.setMaxLength(9);
        eyeColorAInput.setChangedListener((text) -> {
            this.onConfigChanged();
        });
        this.addDrawableChild(eyeColorAInput);
        this.config_panel_01.add(eyeColorAInput);
        this.eyeColorAEditBox = eyeColorAInput;
        // 139,83,75,11 - EyeColorB Label
        TextWidget eyeColorBLabel = new TextWidget(BPosX + 139, BPosY + 83, 75, 11, EyeColorBLabel, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(eyeColorBLabel);
        this.config_panel_01.add(eyeColorBLabel);
        // 241,83,70,11 - EyeColorB Input
        TextFieldWidget eyeColorBInput = new TextFieldWidget(this.textRenderer, BPosX + 241, BPosY + 83, 70, 11, null, EmptyText);
        eyeColorBInput.setMaxLength(9);
        eyeColorBInput.setChangedListener((text) -> {
            this.onConfigChanged();
        });
        this.addDrawableChild(eyeColorBInput);
        this.config_panel_01.add(eyeColorBInput);
        this.eyeColorBEditBox = eyeColorBInput;
        // 139,97,75,11 - PrimaryGreyReverse Label
        TextWidget primaryGreyReverseLabel = new TextWidget(BPosX + 139, BPosY + 97, 75, 11, PrimaryGreyReverseLabel, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(primaryGreyReverseLabel);
        this.config_panel_01.add(primaryGreyReverseLabel);
        // 228,97,83,11 - PrimaryGreyReverse Button
        ButtonWidget primaryGreyReverseButton = ButtonWidget.builder(this.primaryGreyReverse ? BoolBTN_ON :BoolBTN_OFF, (button) -> {
            this.primaryGreyReverse = !this.primaryGreyReverse;
            if (this.primaryGreyReverse) {
                button.setMessage(BoolBTN_ON);
            } else {
                button.setMessage(BoolBTN_OFF);
            }
            this.isColorSettingDirty = true;
        }).position(BPosX + 228, BPosY + 97).size(83, 11).build();
        this.addDrawableChild(primaryGreyReverseButton);
        this.config_panel_01.add(primaryGreyReverseButton);
        this.primaryGreyReverseButton = primaryGreyReverseButton;
        // 139,111,75,11 - Accent1GreyReverse Label
        TextWidget accent1GreyReverseLabel = new TextWidget(BPosX + 139, BPosY + 111, 75, 11, Accent1GreyReverseLabel, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(accent1GreyReverseLabel);
        this.config_panel_01.add(accent1GreyReverseLabel);
        // 228,111,83,11 - Accent1GreyReverse Button
        ButtonWidget accent1GreyReverseButton = ButtonWidget.builder(this.accent1GreyReverse ? BoolBTN_ON :BoolBTN_OFF, (button) -> {
            this.accent1GreyReverse = !this.accent1GreyReverse;
            if (this.accent1GreyReverse) {
                button.setMessage(BoolBTN_ON);
            } else {
                button.setMessage(BoolBTN_OFF);
            }
            this.isColorSettingDirty = true;
        }).position(BPosX + 228, BPosY + 111).size(83, 11).build();
        this.addDrawableChild(accent1GreyReverseButton);
        this.config_panel_01.add(accent1GreyReverseButton);
        this.accent1GreyReverseButton = accent1GreyReverseButton;
        // 139,125,75,11 - Accent2GreyReverse Label
        TextWidget accent2GreyReverseLabel = new TextWidget(BPosX + 139, BPosY + 125, 75, 11, Accent2GreyReverseLabel, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(accent2GreyReverseLabel);
        this.config_panel_01.add(accent2GreyReverseLabel);
        // 228,125,83,11 - Accent2GreyReverse Button
        ButtonWidget accent2GreyReverseButton = ButtonWidget.builder(this.accent2GreyReverse ? BoolBTN_ON :BoolBTN_OFF, (button) -> {
            this.accent2GreyReverse = !this.accent2GreyReverse;
            if (this.accent2GreyReverse) {
                button.setMessage(BoolBTN_ON);
            } else {
                button.setMessage(BoolBTN_OFF);
            }
            this.isColorSettingDirty = true;
        }).position(BPosX + 228, BPosY + 125).size(83, 11).build();
        this.addDrawableChild(accent2GreyReverseButton);
        this.config_panel_01.add(accent2GreyReverseButton);
        this.accent2GreyReverseButton = accent2GreyReverseButton;
        // 139,27,25,11 R Label
        TextWidget rLabel = new TextWidget(BPosX + 139, BPosY + 27, 25, 11, ColorChannel_R, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(rLabel);
        this.config_panel_02.add(rLabel);
        // 139,41,25,11 G Label
        TextWidget gLabel = new TextWidget(BPosX + 139, BPosY + 41, 25, 11, ColorChannel_G, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(gLabel);
        this.config_panel_02.add(gLabel);
        // 139,55,25,11 B Label
        TextWidget bLabel = new TextWidget(BPosX + 139, BPosY + 55, 25, 11, ColorChannel_B, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(bLabel);
        this.config_panel_02.add(bLabel);
        // 177,27,30,11 R Input
        TextFieldWidget sliderREditBox = new TextFieldWidget(textRenderer, BPosX + 177, BPosY + 27, 30, 11, EmptyText);
        sliderREditBox.setMaxLength(3);
        sliderREditBox.setChangedListener((text) -> {
            this.tempSliderR = this.colorChannel2Int(text);
            this.updateSlider();
            if (!isUpdateSlider) {
                this.sliderR.setIntValue(this.tempSliderR);
            }
        });
        this.addDrawableChild(sliderREditBox);
        this.config_panel_02.add(sliderREditBox);
        this.sliderREditBox = sliderREditBox;
        // 177,41,30,11 G Input
        TextFieldWidget sliderGEditBox = new TextFieldWidget(textRenderer, BPosX + 177, BPosY + 41, 30, 11, EmptyText);
        sliderGEditBox.setMaxLength(3);
        sliderGEditBox.setChangedListener((text) -> {
            this.tempSliderG = this.colorChannel2Int(text);
            this.updateSlider();
            if (!isUpdateSlider) {
                this.sliderG.setIntValue(this.tempSliderG);
            }
        });
        this.addDrawableChild(sliderGEditBox);
        this.config_panel_02.add(sliderGEditBox);
        this.sliderGEditBox = sliderGEditBox;
        // 177,55,30,11 B Input
        TextFieldWidget sliderBEditBox = new TextFieldWidget(textRenderer, BPosX + 177, BPosY + 55, 30, 11, EmptyText);
        sliderBEditBox.setMaxLength(3);
        sliderBEditBox.setChangedListener((text) -> {
            this.tempSliderB = this.colorChannel2Int(text);
            this.updateSlider();
            if (!isUpdateSlider) {
                this.sliderB.setIntValue(this.tempSliderB);
            }
        });
        this.addDrawableChild(sliderBEditBox);
        this.config_panel_02.add(sliderBEditBox);
        this.sliderBEditBox = sliderBEditBox;
        // Slider的改动直接改sliderXEditBox就行 不用updateSlider
        // 211,27,100,11 R Slider
        SimpleIntSliderWidget sliderR = new SimpleIntSliderWidget(BPosX + 211, BPosY + 27, 100, 11, EmptyText, 0d, 0, 255);
        sliderR.onChanged = (widget) -> {
            this.isUpdateSlider = true;
            this.sliderREditBox.setText(String.valueOf(widget.getIntValue()));
            this.isUpdateSlider = false;
        };
        this.addDrawableChild(sliderR);
        this.config_panel_02.add(sliderR);
        this.sliderR = sliderR;
        // 211,41,100,11 G Slider
        SimpleIntSliderWidget sliderG = new SimpleIntSliderWidget(BPosX + 211, BPosY + 41, 100, 11, EmptyText, 0d, 0, 255);
        sliderG.onChanged = (widget) -> {
            this.isUpdateSlider = true;
            this.sliderGEditBox.setText(String.valueOf(widget.getIntValue()));
            this.isUpdateSlider = false;
        };
        this.addDrawableChild(sliderG);
        this.config_panel_02.add(sliderG);
        this.sliderG = sliderG;
        // 211,55,100,11 B Slider
        SimpleIntSliderWidget sliderB = new SimpleIntSliderWidget(BPosX + 211, BPosY + 55, 100, 11, EmptyText, 0d, 0, 255);
        sliderB.onChanged = (widget) -> {
            this.isUpdateSlider = true;
            this.sliderBEditBox.setText(String.valueOf(widget.getIntValue()));
            this.isUpdateSlider = false;
        };
        this.addDrawableChild(sliderB);
        this.config_panel_02.add(sliderB);
        this.sliderB = sliderB;
        // 139,69,75,11 - Is Enable Layer Label
        TextWidget isEnableLayerLabel = new TextWidget(BPosX + 139, BPosY + 69, 75, 11, IsEnableLayerLabel, textRenderer).setTextColor(0xDDDDDD);
        this.addDrawableChild(isEnableLayerLabel);
        this.config_panel_02.add(isEnableLayerLabel);
        // 228,69,36,11 - Is Enable Layer Button
        ButtonWidget isEnableLayerButton = ButtonWidget.builder(this.tempSliderAlpha != 0 ? BoolBTN_ON : BoolBTN_OFF, (button) -> {
            this.tempSliderAlpha = this.tempSliderAlpha == 0 ? 255 : 0;
            if (this.tempSliderAlpha != 0) {
                button.setMessage(BoolBTN_ON);
            }
            else {
                button.setMessage(BoolBTN_OFF);
            }
            this.updateSlider();
        }).position(BPosX + 228, BPosY + 69).size(36, 11).build();
        this.addDrawableChild(isEnableLayerButton);
        this.config_panel_02.add(isEnableLayerButton);
        this.isEnableLayerButton = isEnableLayerButton;
        // 281,69,30,11 - Exit Slider Button
        ButtonWidget exitSliderButton = ButtonWidget.builder(ExitSliderButtonLabel, (button) -> {
            this.updateSlider();
            this.isOpenSlider = false;
            this.updatePanel();
        }).position(BPosX + 281, BPosY + 69).size(30, 11).build();
        this.addDrawableChild(exitSliderButton);
        this.config_panel_02.add(exitSliderButton);

        this.formLocalSettingButtons.clear();
        this.formLocalSettingTextFields.clear();
        this.formDefaultSettingButton = null;
        this.formDefaultSettingTextField = null;
        this.globalSettingButtons.clear();
        this.globalSettingTextFields.clear();

        // 20,140,80,15 local_form_slot_1
        this.createSaveDataButtons(0, 0, BPosX + 20, BPosY + 140);
        // 20,158,80,15 local_form_slot_2
        this.createSaveDataButtons(0, 1, BPosX + 20, BPosY + 158);
        // 20,176,80,15 local_form_slot_3
        this.createSaveDataButtons(0, 2, BPosX + 20, BPosY + 176);
        // 20,194,80,15 local_form_slot_4
        this.createSaveDataButtons(0, 3, BPosX + 20, BPosY + 194);

        // 320,17,80,15 global_form_slot_1
        this.createSaveDataButtons(1, 0, BPosX + 320, BPosY + 17);
        // 320,35,80,15 global_form_slot_2
        this.createSaveDataButtons(1, 1, BPosX + 320, BPosY + 35);
        // 320,53,80,15 global_form_slot_3
        this.createSaveDataButtons(1, 2, BPosX + 320, BPosY + 53);
        // 320,71,80,15 global_form_slot_4
        this.createSaveDataButtons(1, 3, BPosX + 320, BPosY + 71);
        // 320,89,80,15 global_form_slot_5
        this.createSaveDataButtons(1, 4, BPosX + 320, BPosY + 89);
        // 320,107,80,15 global_form_slot_6
        this.createSaveDataButtons(1, 5, BPosX + 320, BPosY + 107);
        // 320,125,80,15 global_form_slot_7
        this.createSaveDataButtons(1, 6, BPosX + 320, BPosY + 125);
        // 320,143,80,15 global_form_slot_8
        this.createSaveDataButtons(1, 7, BPosX + 320, BPosY + 143);
        // 320,161,80,15 global_form_slot_9
        this.createSaveDataButtons(1, 8, BPosX + 320, BPosY + 161);

        // 320,194,80,15 form_default_slot
        this.createSaveDataButtons(2, 0, BPosX + 320, BPosY + 194);

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

    private static int timer = 0;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int BPosX = width / 2 - BG_WIDTH / 2;
        int BPosY = height / 2 - BG_HEIGHT / 2;
        this.renderBackground(context);
        this.renderTextureBackground(context);
        // 20,5,60,120
        if (minecraftClient.player != null) {
            RenderEntity(context, BPosX + 50, BPosY + 100, 30, BPosX + 50 - mouseX, BPosY + 100 - mouseY, minecraftClient.player);
        }
        if (!isOpenSlider) {
            // 228,27,11,11
            context.fill(BPosX + 228, BPosY + 27, BPosX + 239, BPosY + 38, this.primaryColor);
            // 228,41,11,11
            context.fill(BPosX + 228, BPosY + 41, BPosX + 239, BPosY + 52, this.accentColor1Color);
            // 228,55,11,11
            context.fill(BPosX + 228, BPosY + 55, BPosX + 239, BPosY + 66, this.accentColor2Color);
            // 228,69,11,11
            context.fill(BPosX + 228, BPosY + 69, BPosX + 239, BPosY + 80, this.eyeColorA);
            // 228,83,11,11
            context.fill(BPosX + 228, BPosY + 83, BPosX + 239, BPosY + 94, this.eyeColorB);
        } else {
            // 267,69,11,11
            context.fill(BPosX + 267, BPosY + 69, BPosX + 278, BPosY + 80, (this.tempSliderAlpha << 24) | (this.tempSliderR << 16) | (this.tempSliderG << 8) | (this.tempSliderB));
        }
        if (timer > 60) {
            this.updateSavaButtonActive();
        } else {
            timer++;
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public Identifier getTexture(int modelID, String category, Identifier texture, Identifier mask, boolean OnlyMultiply) {
        if (this.modelID != modelID) {
            this.modelID = modelID;
            CleanColorSettingCache();
        }
        return colorSettingCacheMap.computeIfAbsent(category, k -> new HashMap<>()).computeIfAbsent(this.getColorSetting(true), k -> {
            // 这种方法不会内存泄漏 但是得自己管理临时材质
            NativeImageBackedTexture nativeImageBackedTexture = FormTextureUtils.BakeTextureNoMemLeak(texture, mask, this.getColorSetting(true), OnlyMultiply);
            Identifier id = getNextDynamicFormID();
            minecraftClient.getTextureManager().registerTexture(id, nativeImageBackedTexture);
            return id;
        });
    }

    public void saveData() {
        ShapeShifterCurseFabricClient.formColorData.writeToConfig();
    }

    @Override
    public void close() {
        CleanColorSettingCache();
        super.close();
        if (isUsingTempTexture) {
            FormTextureUtils.useTempTexture = false;
            FormTextureUtils.tempTextureProcessor = null;
            isUsingTempTexture = false;
        }
        instance = null;
        try {
            ModPacketsS2C.sendUpdateCustomSetting(this.getColorSetting(false)); // 如果没进游戏时会发送失败 懒得做判断了 加一个Try
        } catch (Exception ignored) {
        }
        this.saveData();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int BPosX = width / 2 - BG_WIDTH / 2;
        int BPosY = height / 2 - BG_HEIGHT / 2;
        if (!this.isOpenSlider && this.isScreenInit && button != 0) {
            // 228,27,11,11
            if (mouseX > BPosX + 228 && mouseX < BPosX + 239 && mouseY > BPosY + 27 && mouseY < BPosY + 38) {
                this.tempSliderConfigIndex = 0;
                this.isOpenSlider = true;
                this.updatePanel();
            } else
            // 228,41,11,11
            if (mouseX > BPosX + 228 && mouseX < BPosX + 239 && mouseY > BPosY + 41 && mouseY < BPosY + 52) {
                this.tempSliderConfigIndex = 1;
                this.isOpenSlider = true;
                this.updatePanel();
            } else
            // 228,55,11,11
            if (mouseX > BPosX + 228 && mouseX < BPosX + 239 && mouseY > BPosY + 55 && mouseY < BPosY + 66) {
                this.tempSliderConfigIndex = 2;
                this.isOpenSlider = true;
                this.updatePanel();
            } else
            // 228,69,11,11
            if (mouseX > BPosX + 228 && mouseX < BPosX + 239 && mouseY > BPosY + 69 && mouseY < BPosY + 80) {
                this.tempSliderConfigIndex = 3;
                this.isOpenSlider = true;
                this.updatePanel();
            } else
            // 228,83,11,11
            if (mouseX > BPosX + 228 && mouseX < BPosX + 239 && mouseY > BPosY + 83 && mouseY < BPosY + 94) {
                this.tempSliderConfigIndex = 4;
                this.isOpenSlider = true;
                this.updatePanel();
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private @Nullable Identifier getPlayerForm() {
        PlayerEntity player = minecraftClient.player;
        if (player == null) {
            return null;
        }
        PlayerFormComponent playerFormComponent = RegPlayerFormComponent.PLAYER_FORM.get(player);
        return playerFormComponent.getCurrentForm().FormID;
    }

    private boolean isFormLocalSettingExists(int index) {
        String id = String.format("fcs_%s", index);
        Identifier formID = this.getPlayerForm();
        if (formID != null) {
            return ShapeShifterCurseFabricClient.formColorData.customSettingByForm.getOrDefault(formID, new HashMap<>()).containsKey(id);
        }
        return false;
    }

    private @Nullable FormTextureUtils.ColorSetting getFormLocalSetting(int index) {
        String id = String.format("fcs_%s", index);
        Identifier formID = this.getPlayerForm();
        if (formID != null) {
            return ShapeShifterCurseFabricClient.formColorData.customSettingByForm.getOrDefault(formID, new HashMap<>()).get(id);
        }
        return null;
    }

    private void setFormLocalSetting(int index) {
        String id = String.format("fcs_%s", index);
        Identifier formID = this.getPlayerForm();
        if (formID != null) {
            FormTextureUtils.ColorSetting colorSettingRGBA = this.getColorSetting(false);
            ShapeShifterCurseFabricClient.formColorData.customSettingByForm.computeIfAbsent(formID, k -> new HashMap<>()).put(id, colorSettingRGBA);
        }
        this.updateSavaButtonActive();
    }

    private void removeFormLocalSetting(int index) {
        String id = String.format("fcs_%s", index);
        Identifier formID = this.getPlayerForm();
        if (formID != null) {
            ShapeShifterCurseFabricClient.formColorData.customSettingByForm.computeIfAbsent(formID, k -> new HashMap<>()).remove(id);
        }
        this.updateSavaButtonActive();
    }

    private boolean isGlobalSettingExists(int index) {
        String id = String.format("fcs_%s", index);
        return ShapeShifterCurseFabricClient.formColorData.customSetting.containsKey(id);
    }

    private @Nullable FormTextureUtils.ColorSetting getGlobalSetting(int index) {
        String id = String.format("fcs_%s", index);
        return ShapeShifterCurseFabricClient.formColorData.customSetting.get(id);
    }

    private void setGlobalSetting(int index) {
        String id = String.format("fcs_%s", index);
        FormTextureUtils.ColorSetting colorSettingRGBA = this.getColorSetting(false);
        ShapeShifterCurseFabricClient.formColorData.customSetting.put(id, colorSettingRGBA);
        this.updateSavaButtonActive();
    }

    private void removeGlobalSetting(int index) {
        String id = String.format("fcs_%s", index);
        ShapeShifterCurseFabricClient.formColorData.customSetting.remove(id);
        this.updateSavaButtonActive();
    }

    private boolean isFormDefaultSettingExists() {
        Identifier formID = this.getPlayerForm();
        if (formID != null) {
            return ShapeShifterCurseFabricClient.formColorData.formDefaultSetting.containsKey(formID);
        }
        return false;
    }

    private @Nullable FormTextureUtils.ColorSetting getFormDefaultSetting() {
        Identifier formID = this.getPlayerForm();
        if (formID != null) {
            return ShapeShifterCurseFabricClient.formColorData.formDefaultSetting.get(formID);
        }
        return null;
    }

    private void setFormDefaultSetting() {
        Identifier formID = this.getPlayerForm();
        if (formID != null) {
            FormTextureUtils.ColorSetting colorSettingRGBA = this.getColorSetting(false);
            ShapeShifterCurseFabricClient.formColorData.formDefaultSetting.put(formID, colorSettingRGBA);
        }
        this.updateSavaButtonActive();
    }

    private void removeFormDefaultSetting() {
        Identifier formID = this.getPlayerForm();
        if (formID != null) {
            ShapeShifterCurseFabricClient.formColorData.formDefaultSetting.remove(formID);
        }
        this.updateSavaButtonActive();
    }

    private final List<Pair<FCS_ButtonWidget, FCS_ButtonWidget>> formLocalSettingButtons = new ArrayList<>();
    private final List<TextFieldWidget> formLocalSettingTextFields = new ArrayList<>();
    private Pair<FCS_ButtonWidget, FCS_ButtonWidget> formDefaultSettingButton = null;
    private TextFieldWidget formDefaultSettingTextField = null;
    private final List<Pair<FCS_ButtonWidget, FCS_ButtonWidget>> globalSettingButtons = new ArrayList<>();
    private final List<TextFieldWidget> globalSettingTextFields = new ArrayList<>();

    private void updateSavaButtonActive() {
        if (this.isScreenInit) {
            if (minecraftClient.player == null) {
                for (Pair<FCS_ButtonWidget, FCS_ButtonWidget> buttonWidget : this.formLocalSettingButtons) {
                    FCS_ButtonWidget deleteButtonWidget = buttonWidget.getRight();
                    deleteButtonWidget.active = false;
                    FCS_ButtonWidget updButtonWidget = buttonWidget.getLeft();
                    updButtonWidget.active = false;
                    updButtonWidget.TEXTURE_X = 0;
                }
                this.formDefaultSettingButton.getLeft().active = false;
                this.formDefaultSettingButton.getRight().active = false;
                this.formDefaultSettingButton.getLeft().TEXTURE_X = 0;
            } else {
                for (int index = 0; index < formLocalSettingButtons.size(); index++) {
                    boolean dataExist = this.isFormLocalSettingExists(index);
                    Pair<FCS_ButtonWidget, FCS_ButtonWidget> buttonWidget = formLocalSettingButtons.get(index);
                    FCS_ButtonWidget deleteButtonWidget = buttonWidget.getRight();
                    deleteButtonWidget.active = dataExist;
                    FCS_ButtonWidget updButtonWidget = buttonWidget.getLeft();
                    updButtonWidget.active = true;
                    updButtonWidget.TEXTURE_X = dataExist ? 15 : 0;
                }
                boolean dataExist = this.isFormDefaultSettingExists();
                this.formDefaultSettingButton.getLeft().active = true;
                this.formDefaultSettingButton.getRight().active = dataExist;
                this.formDefaultSettingButton.getLeft().TEXTURE_X = dataExist ? 15 : 0;
            }
            for (int index = 0; index < globalSettingButtons.size(); index++) {
                boolean dataExist = this.isGlobalSettingExists(index);
                Pair<FCS_ButtonWidget, FCS_ButtonWidget> buttonWidget = globalSettingButtons.get(index);
                FCS_ButtonWidget deleteButtonWidget = buttonWidget.getRight();
                deleteButtonWidget.active = dataExist;
                FCS_ButtonWidget updButtonWidget = buttonWidget.getLeft();
                updButtonWidget.active = true;
                updButtonWidget.TEXTURE_X = dataExist ? 15 : 0;
            }
        }
    }

    public void saveCustomColorData(int ButtonType, int Index) {
        if (ButtonType == 0) {
            this.setFormLocalSetting(Index);
        } else if (ButtonType == 1) {
            this.setGlobalSetting(Index);
        } else if (ButtonType == 2) {
            this.setFormDefaultSetting();
        }
        return;
    }

    private void deleteSaveData(int ButtonType, int Index) {
        if (ButtonType == 0) {
            this.removeFormLocalSetting(Index);
        } else if (ButtonType == 1) {
            this.removeGlobalSetting(Index);
        } else if (ButtonType == 2) {
            this.removeFormDefaultSetting();
        }
    }

    private void loadSaveData(int ButtonType, int Index) {
        @Nullable FormTextureUtils.ColorSetting colorSetting = null;
        if (ButtonType == 0) {
            colorSetting = getFormLocalSetting(Index);
        } else if (ButtonType == 1) {
            colorSetting = getGlobalSetting(Index);
        } else if (ButtonType == 2) {
            colorSetting = getFormDefaultSetting();
        }
        if (colorSetting != null) {
            this.loadData(colorSetting);
        }
    }

    private void saveSlotName(int ButtonType, int Index) {
        if (ButtonType == 0) {
            Identifier FormID = this.getPlayerForm();
            if (FormID == null) {
                return;
            }
            ShapeShifterCurseFabricClient.formColorData.setName_LocalFormSlot(FormID, Index, this.formLocalSettingTextFields.get(Index).getText());
        } else if (ButtonType == 1) {
            ShapeShifterCurseFabricClient.formColorData.setName_GlobalSlot(Index, this.globalSettingTextFields.get(Index).getText());
        } else if (ButtonType == 2) {
            Identifier FormID = this.getPlayerForm();
            if (FormID == null) {
                return;
            }
            ShapeShifterCurseFabricClient.formColorData.setName_DefaultSlot(FormID, this.formDefaultSettingTextField.getText());
        }
    }

    private String getSlotName(int ButtonType, int Index) {
        if (ButtonType == 0) {
            Identifier FormID = this.getPlayerForm();
            if (FormID == null) {
                return "";
            }
            return ShapeShifterCurseFabricClient.formColorData.getName_LocalFormSlot(FormID, Index);
        } else if (ButtonType == 1) {
            return ShapeShifterCurseFabricClient.formColorData.getName_GlobalSlot(Index);
        } else if (ButtonType == 2) {
            Identifier FormID = this.getPlayerForm();
            if (FormID == null)
                return "";

            return ShapeShifterCurseFabricClient.formColorData.getName_DefaultSlot(FormID);
        }
        return "";
    }

    private void reloadAllSlotName() {
        for (int index = 0; index < globalSettingButtons.size(); index++) {
            this.globalSettingTextFields.get(index).setText(this.getSlotName(1, index));
        }
        Identifier FormID = this.getPlayerForm();
        if (FormID != null) {
            this.formDefaultSettingTextField.setText(this.getSlotName(2, 0));
            for (int index = 0; index < formLocalSettingButtons.size(); index++) {
                this.formLocalSettingTextFields.get(index).setText(this.getSlotName(0, index));
            }
        }
    }

    private void createSaveDataButtons(int ButtonType, int Index, int X, int Y) {
        // X,Y,80,15
        // X+0,Y+0,15,15 upload/download Button
        FCS_ButtonWidget updButtonWidget = new FCS_ButtonWidget(X, Y, EmptyText, (button -> {
            if (button instanceof FCS_ButtonWidget fcsButtonWidget) {
                // 靠UI判断 省的写一个变量了
                if (fcsButtonWidget.TEXTURE_X == 15) {
                    this.loadSaveData(ButtonType, Index);
                } else if (fcsButtonWidget.TEXTURE_X == 0) {
                    this.saveCustomColorData(ButtonType, Index);
                }
            }
        }), (textSupplier) -> (MutableText)textSupplier.get(), 0);

        // X+15,Y+0,50,15 slot name input
        TextFieldWidget textFieldWidget = new TextFieldWidget(this.textRenderer, X + 15, Y, 50, 15, EmptyText);
        textFieldWidget.setChangedListener((text) -> {
            this.saveSlotName(ButtonType, Index);
        });

        // X+65,Y+0,15,15 delete Button
        FCS_ButtonWidget deleteButtonWidget = new FCS_ButtonWidget(X + 65, Y, EmptyText, (button -> {
            if (button instanceof FCS_ButtonWidget fcsButtonWidget) {
                if (fcsButtonWidget.TEXTURE_X == 30) {
                    this.deleteSaveData(ButtonType, Index);
                }
            }
        }), (textSupplier) -> (MutableText)textSupplier.get(), 30);
        switch (ButtonType) {
            case 0:
                formLocalSettingButtons.add(new Pair<>(updButtonWidget, deleteButtonWidget));
                formLocalSettingTextFields.add(textFieldWidget);
                break;
            case 1:
                globalSettingButtons.add(new Pair<>(updButtonWidget, deleteButtonWidget));
                globalSettingTextFields.add(textFieldWidget);
                break;
            case 2:
                formDefaultSettingButton = new Pair<>(updButtonWidget, deleteButtonWidget);
                formDefaultSettingTextField = textFieldWidget;
                break;
        }
        this.addDrawableChild(updButtonWidget);
        this.addDrawableChild(textFieldWidget);
        this.addDrawableChild(deleteButtonWidget);
    }
}
