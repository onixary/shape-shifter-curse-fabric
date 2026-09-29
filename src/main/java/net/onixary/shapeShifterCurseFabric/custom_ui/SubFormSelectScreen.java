package net.onixary.shapeShifterCurseFabric.custom_ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.ScaleScrollTextWidget;
import net.onixary.shapeShifterCurseFabric.custom_ui.ui_part.WidgetEXUtils;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2C;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.ISubForm;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.util.FormTextureUtils;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric.MOD_ID;

public class SubFormSelectScreen extends Screen implements WidgetEXUtils.IWidgetEX, FormTextureUtils.TempFormModelProcessor {
    private static final int BG_WIDTH = 420;
    private static final int BG_HEIGHT = 227;
    private static final Identifier BG_TEXTURE = new Identifier(MOD_ID, "textures/gui/sub_form_menu.png");

    private List<Identifier> availableForms;
    private int nowFormIndex = 0;
    private boolean isLockTempModelSystem = false;

    private TextWidget formNameText;
    private ScaleScrollTextWidget formDescText;
    private ButtonWidget selectFormButton;
    private ButtonWidget prevFormButton;
    private ButtonWidget nextFormButton;

    public SubFormSelectScreen(Text title) {
        super(title);
        if (!FormTextureUtils.useTempFormModel) {
            FormTextureUtils.useTempFormModel = true;
            FormTextureUtils.tempFormModelProcessor = this;
            isLockTempModelSystem = true;
        } else {
            ShapeShifterCurseFabric.LOGGER.warn("Temp Form Model System is already in use, dynamic form rendering will not work");
        }
    }

    private List<Identifier> getAvailableForms() {
        List<Identifier> availableForms = new ArrayList<>();
        IForm playerForm = FormUtils.getPlayerForm(this.client.player);
        IForm nowForm = playerForm;
        if (playerForm instanceof ISubForm subForm && subForm.isSubForm()) {
            playerForm = subForm.getMasterForm();
        }
        if (playerForm == null) {
            return availableForms;
        }
        List<IForm> subForms = RegPlayerForms.getSubForms(playerForm);
        subForms.add(playerForm);
        subForms.removeIf(form -> form.isEquals(nowForm));
        subForms.removeIf(form -> !(FormUtils.isFormCanUse(this.client.player, form)));
        availableForms.addAll(subForms.stream().map(IForm::getFormID).toList());
        return availableForms;
    }

    private void SendSetForm(Identifier formID) {
        ModPacketsS2C.sendSetSubForm(formID);
    }

    @Override
    public void init() {
        availableForms = getAvailableForms();
        int baseX = (this.width - BG_WIDTH) / 2;
        int baseY = (this.height - BG_HEIGHT) / 2;
        // 152 32 116 14 - Label
        this.addDrawableChild(new TextWidget(baseX + 152, baseY + 32, 116, 14, Text.literal("Sub Form Select Menu"), this.textRenderer));
        // 393 7 20 20 - Close
        this.addDrawableChild(ButtonWidget.builder(Text.literal("X"), button -> this.close()).position(baseX + 393, baseY + 7).size(20, 20).build());

        // 223 58 77 14 - FormName
        formNameText = new TextWidget(baseX + 223, baseY + 58, 77, 14, Text.literal(""), this.textRenderer);
        // 223 75 77 88 - Form Desc
        formDescText = new ScaleScrollTextWidget(baseX + 223, baseY + 75, 77, 88, 1.0f, Text.literal(""), this.textRenderer);
        // 223 166 77 14 - Select Form Button
        selectFormButton = ButtonWidget.builder(Text.literal("SELECT"), button -> {
            Identifier formID = availableForms.get(nowFormIndex);
            if (formID != null) {
                SendSetForm(formID);
                this.close();
            }
        }).position(baseX + 223, baseY + 166).size(77, 14).build();
        // 93 116 16 16 - Prev Form Button
        prevFormButton = ButtonWidget.builder(Text.literal("<"), button -> {
            nowFormIndex--;
            if (nowFormIndex < 0) {
                nowFormIndex = availableForms.size() - 1;
            }
            updateInfo();
        }).position(baseX + 93, baseY + 116).size(16, 16).build();
        // 311 116 16 16 - Next Form Button
        nextFormButton = ButtonWidget.builder(Text.literal(">"), button -> {
            nowFormIndex++;
            if (nowFormIndex >= availableForms.size()) {
                nowFormIndex = 0;
            }
            updateInfo();
        }).position(baseX + 311, baseY + 116).size(16, 16).build();

        selectFormButton.active = availableForms.size() > 1;
        prevFormButton.active = false;
        nextFormButton.active = false;

        this.addDrawableChild(formNameText);
        this.addDrawableChild(formDescText);
        this.addDrawableChild(selectFormButton);
        this.addDrawableChild(prevFormButton);
        this.addDrawableChild(nextFormButton);

        this.updatePageButtons();
        this.updateInfo();
        super.init();
    }

    @Override
    public void close() {
        if (this.isLockTempModelSystem) {
            FormTextureUtils.useTempFormModel = false;
            FormTextureUtils.tempFormModelProcessor = null;
            isLockTempModelSystem = false;
        }
        super.close();
    }

    public void renderBackgroundTexture(DrawContext context) {
        // 计算居中位置，保持固定尺寸
        int bgX = (this.width - BG_WIDTH) / 2;
        int bgY = (this.height - BG_HEIGHT) / 2;
        context.drawTexture(BG_TEXTURE, bgX, bgY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
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

    private void RenderEntityInViewport(DrawContext context, int viewportX, int viewportY, int viewportWidth, int viewportHeight, int x, int y, int size, int mouseX, int mouseY, LivingEntity entity) {
        context.enableScissor(viewportX, viewportY, viewportX + viewportWidth, viewportY + viewportHeight);
        try {
            RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
            RenderEntity(context, x, y, size, mouseX, mouseY, entity);
        } finally {
            context.disableScissor();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        int baseX = (this.width - BG_WIDTH) / 2;
        int baseY = (this.height - BG_HEIGHT) / 2;
        // 120 58 100 133
        if (client.player != null) {
            int viewportX = baseX + 120;
            int viewportY = baseY + 50;
            int entityX = viewportX + 100 / 2;
            int entityY = viewportY + 133 - 15;
            int entitySize = 50;
            RenderEntityInViewport(
                    context,
                    viewportX, viewportY,
                    100, 133,
                    entityX, entityY,
                    entitySize,
                    entityX - mouseX, entityY - mouseY - entitySize,
                    client.player
            );
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return false;
    }

    @Override
    public WidgetEXUtils.WidgetRect getRect() {
        return null;
    }

    public List<WidgetEXUtils.IWidgetEX> WidgetList = new ArrayList<>();

    @Override
    public List<WidgetEXUtils.IWidgetEX> getWidgetList() {
        return this.WidgetList;
    }

    public void updatePageButtons() {
        prevFormButton.active = nowFormIndex > 0;
        nextFormButton.active = nowFormIndex < availableForms.size() - 1;
    }

    public void clearInfo() {
        formNameText.setMessage(Text.literal(""));
        formDescText.setMessage(Text.literal(""));
        selectFormButton.active = false;
        updatePageButtons();
    }

    public void updateInfo() {
        if (availableForms.isEmpty()) {
            clearInfo();
            return;
        }
        Identifier formID = availableForms.get(nowFormIndex);
        if (formID == null) {
            clearInfo();
            return;
        }
        IForm form = RegPlayerForms.getPlayerForm(formID);
        if (form == null) {
            clearInfo();
            return;
        }
        formNameText.setMessage(form.getContentText(CodexData.ContentType.NAME));
        formDescText.setMessage(form.getContentText(CodexData.ContentType.DESC));
        selectFormButton.active = true;
        updatePageButtons();
    }

    @Override
    public IForm getForm() {
        if (availableForms.isEmpty()) {
            return FormUtils.getPlayerForm(client.player);
        }
        IForm form = RegPlayerForms.getPlayerForm(availableForms.get(nowFormIndex));
        if (form == null) {
            return FormUtils.getPlayerForm(client.player);
        }
        return form;
    }

    @Override
    public Identifier getLayerID() {
        IForm playerForm = this.getForm();
        return playerForm.getRenderLayerOverride() == null ? playerForm.getFormLayer().getRight() : playerForm.getRenderLayerOverride().getRight();
    }
}
