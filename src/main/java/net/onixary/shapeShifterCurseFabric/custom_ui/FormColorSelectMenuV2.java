package net.onixary.shapeShifterCurseFabric.custom_ui;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.client.ShapeShifterCurseFabricClient;
import net.onixary.shapeShifterCurseFabric.config.PlayerCustomConfig;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.FCS_ButtonWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.SimpleIntSliderWidget;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

// XuHaoNan:
// 需要的功能
// RGB HSV 拉条
// 全局颜色槽位的上传和下载
// 玩家模型展示框
// 自动加载/保存 颜色数据 当开启自动同步颜色时会自动把数据写入到config中 否则仅写入到服务器中 数据用服务器端的数据 如果未进入游戏 则使用客户端的数据
// 剪切板上传下载
// 二级菜单(由于Onixary提的方案有问题 等待修改方案中)

// 比V1少的功能
// 客户端 服务器 独立上传下载按钮 可以实现多个服务器不同颜色数据
// 形态默认颜色(功能保留 UI没了)
// 形态独立颜色槽位(功能有 但没给V2留槽位名字存储 仅V1使用 所以对应功能不删)
// 20260524整的新活 由于依赖"形态默认颜色"系统的更新机制 而且架构已经固定 所以V2不加此功能
// V1的部分Label 文本保留 反正写都写了 保留吧 我之后把V1迁移到我的拓展里时可以减少一点工作量

public class FormColorSelectMenuV2 extends Screen implements FormTextureUtils.TempTextureProcessor {
    // LANG 从V1复制的
    private static final Text BoolBTN_ON = Text.translatable("text.cloth-config.boolean.value.true");
    private static final Text BoolBTN_OFF = Text.translatable("text.cloth-config.boolean.value.false");

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

    // Data2: 由Data1的数据更新 修改时直接修改对应int 需要一个flag标记 防止循环调用 更新对应Data5数据
    private boolean isUpdateConfigWidget = false;  // 由
    private TextFieldWidget primaryColorTextBox;
    private TextFieldWidget accentColor1TextBox;
    private TextFieldWidget accentColor2TextBox;
    private TextFieldWidget eyeColorATextBox;
    private TextFieldWidget eyeColorBTextBox;
    // 点击直接切换
    private ButtonWidget primaryGreyReverseButton;
    private ButtonWidget accent1GreyReverseButton;
    private ButtonWidget accent2GreyReverseButton;

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
    private TextFieldWidget SliderRTextBox;
    private TextFieldWidget SliderGTextBox;
    private TextFieldWidget SliderBTextBox;
    private TextFieldWidget SliderHTextBox;
    private TextFieldWidget SliderSTextBox;
    private TextFieldWidget SliderVTextBox;
    // 点击直接切换
    private ButtonWidget SliderAButton;

    // Data5: 修改时将Data4的flag++ 更新对应的TextBox 检查自身Flag 选择是否触发更新RGB HSV
    private boolean isUpdateHSV = false;  // 当此值为true 不会触发RGB转HSV更新 由RGB修改触发 触发后将此flag设置为true
    private boolean isUpdateRGB = false;  // 当此值为true 不会触发HSV转RGB更新 由HSV修改触发 触发后将此flag设置为true
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
    public static FormColorSelectMenuV2 instance;
    private boolean isLockTempTextureSystem = false;  // 用于还原
    private @Nullable Screen parsetScreen = null;
    private final HashMap<String, HashMap<FormTextureUtils.ColorSetting, Identifier>> colorSettingCacheMap = new HashMap<>();  // 防止内存泄漏
    private int modelID = -1;
    private static final String identifierNameSpace = MOD_ID;
    private static final String identifierPrefix = "dynamic_fcs_v2_";
    private static long nowColorSettingIndex = 0;  // 自增ID
    private int timer = 0;
    private List<Pair<FCS_ButtonWidget, FCS_ButtonWidget>> globalSlotButton = new ArrayList<>();
    private List<TextFieldWidget> globalSlotNameInputs = new ArrayList<>();

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
        // 更新Data2
        this.isUpdateConfigWidget = true;
        this.primaryColorTextBox.setText(encodeColor(this.primaryColor));
        this.accentColor1TextBox.setText(encodeColor(this.accentColor1Color));
        this.accentColor2TextBox.setText(encodeColor(this.accentColor2Color));
        this.eyeColorATextBox.setText(encodeColor(this.eyeColorA));
        this.eyeColorBTextBox.setText(encodeColor(this.eyeColorB));
        this.primaryGreyReverseButton.setMessage(this.primaryGreyReverse ? BoolBTN_ON : BoolBTN_OFF);
        this.accent1GreyReverseButton.setMessage(this.accent1GreyReverse ? BoolBTN_ON : BoolBTN_OFF);
        this.accent2GreyReverseButton.setMessage(this.accent2GreyReverse ? BoolBTN_ON : BoolBTN_OFF);
        this.isUpdateConfigWidget = false;
        // 更新Data5 由Data5转Data4再转Data3 最后由isUpdateSliderFormConfig Flag阻止更新至Data1
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
        this.SliderAButton.setMessage(this.SliderA == 0 ? BoolBTN_OFF : BoolBTN_ON);
        this.isUpdateSliderFormConfig = false;
        this.updateHSVFromRGB();
    }

    // Data2 函数
    private void onData2ChangedOrClicked(int textBoxIndex) {
        if (this.isUpdateConfigWidget) {
            return;
        }
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
        }
        this.isColorSettingDirty = true;
        this.onData1Changed();
    }

    // Data3 函数
    private void onData3Changed() {
        if (this.isUpdateSliderFormConfig) {
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
            this.onData3Changed();
        }
        this.SliderAButton.setMessage(this.SliderA == 0 ? BoolBTN_OFF : BoolBTN_ON);
    }

    // Data5 函数
    private void updateHSVFromRGB() {
        if (this.isUpdateRGB) {
            return;
        }
        this.isUpdateHSV = true;
        int[] HSV = FormTextureUtils.rgbToHsv(SliderR, SliderG, SliderB);
        this.SliderHSlider.setIntValue(HSV[0]);
        this.SliderSSlider.setIntValue(HSV[1]);
        this.SliderVSlider.setIntValue(HSV[2]);
        this.isUpdateHSV = false;
    }

    private void updateRGBFromHSV() {
        if (this.isUpdateHSV) {
            return;
        }
        this.isUpdateRGB = true;
        int[] RGB = FormTextureUtils.hsvToRgb(SliderH, SliderS, SliderV);
        this.SliderRSlider.setIntValue(RGB[0]);
        this.SliderGSlider.setIntValue(RGB[1]);
        this.SliderBSlider.setIntValue(RGB[2]);
        this.isUpdateRGB = false;
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

    // Global Slot 函数
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
        super(title);
        this.parsetScreen = parsetScreen;
    }

    @Override
    public void init() {
        // TODO 等待最终UI方案
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // TODO 等待最终UI方案

        if (timer > 60) {
            this.updateSavaButtonActive();
        } else {
            timer++;
        }
    }


    public void saveDataToClient() {
        ShapeShifterCurseFabric.playerCustomConfig.primaryColor = primaryColor;
        ShapeShifterCurseFabric.playerCustomConfig.accentColor1Color = accentColor1Color;
        ShapeShifterCurseFabric.playerCustomConfig.accentColor2Color = accentColor2Color;
        ShapeShifterCurseFabric.playerCustomConfig.eyeColorA = eyeColorA;
        ShapeShifterCurseFabric.playerCustomConfig.eyeColorB = eyeColorB;
        ShapeShifterCurseFabric.playerCustomConfig.primaryGreyReverse = primaryGreyReverse;
        ShapeShifterCurseFabric.playerCustomConfig.accent1GreyReverse = accent1GreyReverse;
        ShapeShifterCurseFabric.playerCustomConfig.accent2GreyReverse = accent2GreyReverse;
        AutoConfig.getConfigHolder(PlayerCustomConfig.class).save();
    }

    @Override
    public void close() {
        CleanColorSettingCache();
        if (this.isLockTempTextureSystem) {
            FormTextureUtils.useTempTexture = false;
            FormTextureUtils.tempTextureProcessor = null;
            isLockTempTextureSystem = false;
        }
        instance = null;
        try {
            ModPacketsS2C.sendUpdateCustomColor(this.getColorSetting(false), false); // 如果没进游戏时会发送失败 懒得做判断了 加一个Try
        } catch (Exception ignored) {
        }
        this.saveDataToClient();
        ShapeShifterCurseFabricClient.formColorData.writeToConfig();
        if (this.parsetScreen != null && this.client != null) {
            this.client.setScreen(this.parsetScreen);
        } else {
            super.close();
        }
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
        return colorSettingCacheMap.computeIfAbsent(category, k -> new HashMap<>()).computeIfAbsent(this.getColorSetting(true), k -> {
            // 这种方法不会内存泄漏 但是得自己管理临时材质
            NativeImageBackedTexture nativeImageBackedTexture = FormTextureUtils.BakeTextureNoMemLeak(texture, mask, this.getColorSetting(true), OnlyMultiply);
            Identifier id = getNextDynamicFormID();
            minecraftClient.getTextureManager().registerTexture(id, nativeImageBackedTexture);
            return id;
        });
    }
}
