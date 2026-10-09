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

public class SubFormSelectScreen_NoForm extends Screen {
    private static final int MENU_WIDTH = 420;
    private static final int MENU_HEIGHT = 227;
    private static final Identifier MENU_TEXTURE = new Identifier(MOD_ID, "textures/gui/sub_form_menu_2.png");

    private static final int BG_WIDTH = 420;
    private static final int BG_HEIGHT = 227;
    private static final Identifier BG_TEXTURE = new Identifier(MOD_ID, "textures/gui/sub_form_menu_bg.png");

    public SubFormSelectScreen_NoForm(Text title) {
        super(title);
    }
    @Override
    public void init() {
        int baseX = (this.width - MENU_WIDTH) / 2;
        int baseY = (this.height - MENU_HEIGHT) / 2;
        // 152 32 116 14 - Label
        this.addDrawableChild(new TextWidget(baseX + 152, baseY + 32, 116, 14, Text.literal("Sub Form Select Menu"), this.textRenderer));
        // 393 7 20 20 - Close
        this.addDrawableChild(ButtonWidget.builder(Text.literal("X"), button -> this.close()).position(baseX + 393, baseY + 7).size(20, 20).build());
        // 132 107 156 36 - No Form Can Select Tip
        this.addDrawableChild(new TextWidget(baseX + 132, baseY + 107, 156, 36, Text.literal("No Form Can Select"), this.textRenderer));
        super.init();
    }

    @Override
    public void close() {
        super.close();
    }

    public void renderBackgroundTexture(DrawContext context) {
        // 计算居中位置，保持固定尺寸
        context.drawTexture(BG_TEXTURE, 0, 0, this.width, this.height, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
        int bgX = (this.width - MENU_WIDTH) / 2;
        int bgY = (this.height - MENU_HEIGHT) / 2;
        context.drawTexture(MENU_TEXTURE, bgX, bgY, 0, 0, MENU_WIDTH, MENU_HEIGHT, MENU_WIDTH, MENU_HEIGHT);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
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
}
