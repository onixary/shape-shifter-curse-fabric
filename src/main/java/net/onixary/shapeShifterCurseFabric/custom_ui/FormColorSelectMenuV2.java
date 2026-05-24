package net.onixary.shapeShifterCurseFabric.custom_ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.SimpleIntSliderWidget;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class FormColorSelectMenuV2 extends Screen implements FormTextureUtils.TempTextureProcessor {
    // Data0:
    private boolean isColorSettingDirty = true;
    private FormTextureUtils.ColorSetting colorSetting_ARGB = null;
    private FormTextureUtils.ColorSetting colorSetting_ABGR = null;

    // Data1: 由它更新Data0修改它后需要把isColorSettingDirty设为true
    private int primaryColor = 0x00FFFFFF;
    private int accentColor1Color = 0x00FFFFFF;
    private int accentColor2Color = 0x00FFFFFF;
    private int eyeColorA = 0x00FFFFFF;
    private int eyeColorB = 0x00FFFFFF;
    private boolean primaryGreyReverse = false;
    private boolean accent1GreyReverse = false;
    private boolean accent2GreyReverse = false;

    // Data2: 由Data1的数据更新 修改时直接修改对应int 需要一个flag标记 防止循环调用
    private int isUpdateConfigWidget = 0;  // 由
    private TextFieldWidget primaryColorTextBox;
    private TextFieldWidget accentColor1TextBox;
    private TextFieldWidget accentColor2TextBox;
    private TextFieldWidget eyeColorATextBox;
    private TextFieldWidget eyeColorBTextBox;
    // 点击直接切换
    private ButtonWidget primaryGreyReverseButton;
    private ButtonWidget accent1GreyReverseButton;
    private ButtonWidget accent2GreyReverseButton;

    // Data3: 修改它后需要调用刷新函数 直接修改对应的TextBox
    private int SliderIndex = -1;
    private int SliderR = 0;
    private int SliderG = 0;
    private int SliderB = 0;
    private int SliderA = 0;
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

    private final HashMap<String, HashMap<FormTextureUtils.ColorSetting, Identifier>> colorSettingCacheMap = new HashMap<>();  // 防止内存泄漏
    private int modelID = -1;
    private static final String identifierNameSpace = MOD_ID;
    private static final String identifierPrefix = "dynamic_fcs_v2_";
    private static long nowColorSettingIndex = 0;  // 自增ID

    public FormColorSelectMenuV2(Text title) {
        super(title);
    }

    @Override
    public Identifier getTexture(int modelID, String category, Identifier texture, Identifier mask, boolean OnlyMultiply) {
        return null;
    }
}
