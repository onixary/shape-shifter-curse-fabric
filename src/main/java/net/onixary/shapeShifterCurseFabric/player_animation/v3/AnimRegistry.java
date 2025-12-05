package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// 注册包含AnimState AnimStateController AnimFSM的类

// TODO 提供数据包可用的 AnimStateController 补充animState和animFSM
public class AnimRegistry {
    public static class AnimState {
        AbstractAnimStateController defaultController;
    }

    public static RegistryKey<Registry<AnimState>> animStateRegistryKey = RegistryKey.ofRegistry(ShapeShifterCurseFabric.identifier("anim_state"));
    public static Registry<AnimState> animStateRegistry = new SimpleRegistry<>(animStateRegistryKey, Lifecycle.stable());

    public static RegistryKey<Registry<Supplier<AbstractAnimStateController>>> animStateControllerRegistryKey = RegistryKey.ofRegistry(ShapeShifterCurseFabric.identifier("anim_state_controller"));
    public static Registry<Supplier<AbstractAnimStateController>> animStateControllerRegistry = new SimpleRegistry<>(animStateControllerRegistryKey, Lifecycle.stable());

    public static RegistryKey<Registry<AbstractAnimFSM>> animFSMRegistryKey = RegistryKey.ofRegistry(ShapeShifterCurseFabric.identifier("anim_fsm"));
    public static Registry<AbstractAnimFSM> animFSMRegistry = new SimpleRegistry<>(animFSMRegistryKey, Lifecycle.stable());


    public static void registerAnimState(Identifier identifier, AnimState animState) {
        Registry.register(animStateRegistry, identifier, animState);
    }

    public static void registerAnimStateController(Identifier identifier, Supplier<AbstractAnimStateController> animStateController) {
        Registry.register(animStateControllerRegistry, identifier, animStateController);
    }

    public static void registerAnimFSM(Identifier identifier, AbstractAnimFSM animFSM) {
        Registry.register(animFSMRegistry, identifier, animFSM);
    }

    public static @Nullable AnimState getAnimState(Identifier identifier) {
        return animStateRegistry.get(identifier);
    }

    // 每个Form里都有一个预设了不同参数的AnimStateController
    public static @Nullable AbstractAnimStateController getAnimStateController(Identifier identifier) {
        Supplier<AbstractAnimStateController> animStateController = animStateControllerRegistry.get(identifier);
        if (animStateController != null) {
            return animStateController.get();
        }
        return null;
    }

    public static @Nullable Supplier<AbstractAnimStateController> getAnimStateControllerSupplier(Identifier identifier) {
        return animStateControllerRegistry.get(identifier);
    }

    public static @Nullable AbstractAnimFSM getAnimFSM(Identifier identifier) {
        return animFSMRegistry.get(identifier);
    }
}
