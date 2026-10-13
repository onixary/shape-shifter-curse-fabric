package net.onixary.shapeShifterCurseFabric.client;

import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class KeyUtils {
    public static HashMap<String, KeyBinding> keyBindingRegistry = new HashMap<>();
    // 选主动技能的轮盘使用 由Mixin实现
    public static @Nullable KeyBinding nowKeyBinding = null;

    public static void registerKeyBinding(String name, KeyBinding keyBinding) {
        keyBindingRegistry.put(name, keyBinding);
    }
}
