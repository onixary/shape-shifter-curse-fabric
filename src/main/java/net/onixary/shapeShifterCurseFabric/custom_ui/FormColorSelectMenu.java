package net.onixary.shapeShifterCurseFabric.custom_ui;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Clipboard;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
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

    private static final Text EmptyText = Text.empty();
    private static final Text BoolBTN_ON = Text.translatable("text.cloth-config.boolean.value.true");
    private static final Text BoolBTN_OFF = Text.translatable("text.cloth-config.boolean.value.false");

    // Label
    private static final Text FormSlotTitle = Text.translatable("gui.shape_shifter_curse_fabric.fcs.form_slot_title");
    private static final Text GlobalSlotTitle = Text.translatable("gui.shape_shifter_curse_fabric.fcs.global_slot_title");
    private static final Text FormDefaultSlotTitle = Text.translatable("gui.shape_shifter_curse_fabric.fcs.form_default_slot_title");
    private static final Text Title = Text.translatable("gui.shape_shifter_curse_fabric.fcs.title");

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

    private boolean isEditBoxInit = false;
    private TextFieldWidget primaryColorEditBox = null;
    private TextFieldWidget accentColor1EditBox = null;
    private TextFieldWidget accentColor2EditBox = null;
    private TextFieldWidget eyeColorAEditBox = null;
    private TextFieldWidget eyeColorBEditBox = null;
    private ButtonWidget primaryGreyReverseButton = null;
    private ButtonWidget accent1GreyReverseButton = null;
    private ButtonWidget accent2GreyReverseButton = null;

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
    private int primaryColor = 0xFFFFFFFF;
    private int accentColor1Color = 0xFFFFFFFF;
    private int accentColor2Color = 0xFFFFFFFF;
    private int eyeColorA = 0xff000000;
    private int eyeColorB = 0xff000000;
    private boolean primaryGreyReverse = false;
    private boolean accent1GreyReverse = false;
    private boolean accent2GreyReverse = false;

    private boolean isColorSettingDirty = true;
    private FormTextureUtils.ColorSetting colorSetting = null;

    private int tempSliderConfigIndex = -1;
    private int tempSliderR = 0;
    private int tempSliderG = 0;
    private int tempSliderB = 0;
    private int tempSliderAlpha = 0;

    private boolean isOpenSlider = false;
    private List<ClickableWidget> config_panel_01 = new ArrayList<>();  // 保存config输入框 label之类的 用于切换
    private List<ClickableWidget> config_panel_02 = new ArrayList<>();  // 保存 RGB条 一些按钮

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
                this.loadData(colorSetting);
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

    public @NotNull FormTextureUtils.ColorSetting getColorSetting() {
        if (isColorSettingDirty) {
            colorSetting = new FormTextureUtils.ColorSetting(primaryColor, accentColor1Color, accentColor2Color, eyeColorA, eyeColorB, primaryGreyReverse, accent1GreyReverse, accent2GreyReverse);
            isColorSettingDirty = false;
        }
        return colorSetting;
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
    }

    public void renderTextureBackground(DrawContext context) {
        int BG_X = width / 2 - BG_WIDTH / 2;
        int BG_Y = height / 2 - BG_HEIGHT / 2;
        context.drawTexture(texture, BG_X, BG_Y, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
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

    public void onConfigChanged() {
        if (!this.isEditBoxInit || isUpdateUI) {
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
        if (!this.isEditBoxInit) {
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
        this.isUpdateUI = false;
    }

    @Override
    public void init() {
        super.init();
        int BPosX = width / 2 - BG_WIDTH / 2;
        int BPosY = height / 2 - BG_HEIGHT / 2;
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
            ModPacketsS2C.sendUpdateCustomSetting(this.getColorSetting());
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
            String keyBoardData = FormColorData.ColorSettingtoString(this.getColorSetting());
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
        this.isEditBoxInit = true;
        // TODO RGB条
        this.updatePanel();
        // TODO 特殊按钮 这个得写class了
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
        // TODO Color Panel

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public Identifier getTexture(int modelID, String category, Identifier texture, Identifier mask, boolean OnlyMultiply) {
        if (this.modelID != modelID) {
            this.modelID = modelID;
            CleanColorSettingCache();
        }
        return colorSettingCacheMap.computeIfAbsent(category, k -> new HashMap<>()).computeIfAbsent(this.getColorSetting(), k -> {
            // 这种方法不会内存泄漏 但是得自己管理临时材质
            NativeImageBackedTexture nativeImageBackedTexture = FormTextureUtils.BakeTextureNoMemLeak(texture, mask, this.getColorSetting(), OnlyMultiply);
            Identifier id = getNextDynamicFormID();
            minecraftClient.getTextureManager().registerTexture(id, nativeImageBackedTexture);
            return id;
        });
    }

    public void saveData() {
        // TODO 调用FormColorData存储
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
        try {
            ModPacketsS2C.sendUpdateCustomSetting(this.getColorSetting()); // 如果没进游戏时会发送失败 懒得做判断了 加一个Try
        } catch (Exception ignored) {
        }
        this.saveData();
    }
}
