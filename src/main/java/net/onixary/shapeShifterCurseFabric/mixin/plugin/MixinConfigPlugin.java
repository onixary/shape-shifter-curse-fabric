package net.onixary.shapeShifterCurseFabric.mixin.plugin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {

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
        // 检查是否为 PlayerEntityRendererFallFlyingMixin
        if (mixinClassName.endsWith("PlayerEntityRendererFallFlyingMixin")) {
            boolean isViveCraftLoaded = FabricLoader.getInstance().isModLoaded("vivecraft");
            if (isViveCraftLoaded) {
                System.out.println("[ShapeShifterCurse] ViveCraft detected, skipping PlayerEntityRendererFallFlyingMixin");
                return false; // 完全跳过这个 mixin
            }
        }
        return true; // 允许其他 mixin 正常加载
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
