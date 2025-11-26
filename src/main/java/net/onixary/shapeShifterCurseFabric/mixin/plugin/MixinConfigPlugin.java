package net.onixary.shapeShifterCurseFabric.mixin.plugin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    private record MixinRequiredMods(String[] value, String[] not) { }
    private static final HashMap<String, MixinRequiredMods> mixinRequiredMods = new HashMap<>();

    static {
        mixinRequiredMods.put("net.onixary.shapeShifterCurseFabric.mixin.PlayerEntityRendererFallFlyingMixin", new MixinRequiredMods(new String[]{}, new String[]{"vivecraft"}));
        mixinRequiredMods.put("net.onixary.shapeShifterCurseFabric.mixin.integration.AppleSkin", new MixinRequiredMods(new String[]{"appleskin"}, new String[]{}));
    }

    @Override
    public void onLoad(String mixinPackage) {
        // 插件加载时调用
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // 原先的代码 使用硬编码的方式
        // 检查是否为 PlayerEntityRendererFallFlyingMixin
        // if (mixinClassName.endsWith("PlayerEntityRendererFallFlyingMixin")) {
        //     boolean isViveCraftLoaded = FabricLoader.getInstance().isModLoaded("vivecraft");
        //     if (isViveCraftLoaded) {
        //         System.out.println("[ShapeShifterCurse] ViveCraft detected, skipping PlayerEntityRendererFallFlyingMixin");
        //         return false; // 完全跳过这个 mixin
        //     }
        // }

        // 检查是否符合MixinRequiredMods注解的要求 注解会导致Mixin提前加载 临时使用static注册
        if (mixinRequiredMods.containsKey(mixinClassName)) {
            MixinRequiredMods requiredMods = mixinRequiredMods.get(mixinClassName);
            for (String mod : requiredMods.value) {
                if (!FabricLoader.getInstance().isModLoaded(mod)) {
                    System.out.println("[ShapeShifterCurse] " + mod + " not detected, skipping " + mixinClassName);
                    return false; // 完全跳过这个 mixin
                }
            }
            for (String mod : requiredMods.not) {
                if (FabricLoader.getInstance().isModLoaded(mod)) {
                    System.out.println("[ShapeShifterCurse] " + mod + " detected, skipping " + mixinClassName);
                    return false; // 完全跳过这个 mixin
                }
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // 不需要特殊处理
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // 应用前处理（可选）
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // 应用后处理（可选）
    }
}
