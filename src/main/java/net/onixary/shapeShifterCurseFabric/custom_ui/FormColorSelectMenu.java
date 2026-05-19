package net.onixary.shapeShifterCurseFabric.custom_ui;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.skin.RegPlayerSkinComponent;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class FormColorSelectMenu extends Screen implements FormTextureUtils.TempTextureProcessor {
    public static final Identifier texture = new Identifier(MOD_ID,"textures/gui/form_color_select_menu.png");
    private static final int BG_WIDTH = 470;
    private static final int BG_HEIGHT = 247;

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

    public void loadData(boolean serverSide) {
        if (serverSide) {
            if (minecraftClient.player != null) {
                FormTextureUtils.ColorSetting colorSetting = RegPlayerSkinComponent.SKIN_SETTINGS.get(minecraftClient.player).getFormColor();
                primaryColor = colorSetting.getPrimaryColor();
                accentColor1Color = colorSetting.getAccentColor1();
                accentColor2Color = colorSetting.getAccentColor2();
                eyeColorA = colorSetting.getEyeColorA();
                eyeColorB = colorSetting.getEyeColorB();
                primaryGreyReverse = colorSetting.getPrimaryGreyReverse();
                accent1GreyReverse = colorSetting.getAccent1GreyReverse();
                accent2GreyReverse = colorSetting.getAccent2GreyReverse();
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
        loadData(false);
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

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        this.renderTextureBackground(context);
    }

    @Override
    public Identifier getTexture(int modelID, String category, Identifier texture, Identifier mask, boolean OnlyMultiply) {
        if (this.modelID != modelID) {
            this.modelID = modelID;
            CleanColorSettingCache();
        }
        return colorSettingCacheMap.computeIfAbsent(category, k -> new HashMap<>()).computeIfAbsent(colorSetting, k -> {
            // 这种方法不会内存泄漏 但是得自己管理临时材质
            NativeImageBackedTexture nativeImageBackedTexture = FormTextureUtils.BakeTextureNoMemLeak(texture, mask, colorSetting, OnlyMultiply);
            Identifier id = getNextDynamicFormID();
            minecraftClient.getTextureManager().registerTexture(id, nativeImageBackedTexture);
            return id;
        });
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
    }
}
