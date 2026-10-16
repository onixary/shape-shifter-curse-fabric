package net.onixary.shapeShifterCurseFabric.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.onixary.shapeShifterCurseFabric.magic.IMagic;
import net.onixary.shapeShifterCurseFabric.magic.MagicUtils;
import net.onixary.shapeShifterCurseFabric.magic.PressKeyMagic;

public class SkillKeyBinding extends KeyBinding {
    public static final MinecraftClient client = MinecraftClient.getInstance();

    private final String KeyID;

    public SkillKeyBinding(String translationKey, InputUtil.Type type, int code, String category) {
        super(translationKey, type, code, category);
        this.KeyID = translationKey;
    }

    @Override
    public boolean isPressed() {
        if (super.isPressed()) {
            return true;
        }
        IMagic nowMagic = MagicUtils.getNowSelectMagic(client.player);
        if (nowMagic instanceof PressKeyMagic pressKeyMagic && this.KeyID.equals(pressKeyMagic.getKeyBindID())) {
            return ShapeShifterCurseFabricClient.activeSkillKeybind.isPressed();
        }
        return false;
    }

    @Override
    public boolean wasPressed() {
        if (super.wasPressed()) {
            return true;
        }
        IMagic nowMagic = MagicUtils.getNowSelectMagic(client.player);
        if (nowMagic instanceof PressKeyMagic pressKeyMagic && this.KeyID.equals(pressKeyMagic.getKeyBindID())) {
            return ShapeShifterCurseFabricClient.activeSkillKeybind.wasPressed();
        }
        return false;
    }
}
