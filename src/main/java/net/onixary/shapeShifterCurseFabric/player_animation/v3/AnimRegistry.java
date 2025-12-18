package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import com.google.gson.JsonObject;
import com.mojang.serialization.Lifecycle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

// 注册包含AnimState AnimStateController AnimFSM的类
public class AnimRegistry {
    public static class AnimState {
        public AbstractAnimStateController defaultController;

        public AnimState(AbstractAnimStateController defaultController) {
            this.defaultController = defaultController;
        }
    }

    public static class PowerDefaultAnim {  // 覆写时的defaultAnimationData应该为null
        boolean IsRegistered = false;
        AnimUtils.AnimationHolderData defaultAnimationData = null;
        AnimationHolder defaultAnimation = null;
        public PowerDefaultAnim() {
            this((AnimUtils.AnimationHolderData)null);
        }

        public PowerDefaultAnim(@Nullable AnimUtils.AnimationHolderData defaultAnimationData) {
            this.defaultAnimationData = defaultAnimationData;
        }

        public @Nullable AnimationHolder getAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
            return this.defaultAnimation;
        }

        public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
            if (this.IsRegistered) {
                return;
            }
            if (this.defaultAnimationData != null) {
                this.defaultAnimation = this.defaultAnimationData.build();
            }
            this.IsRegistered = true;
        }

        // 用于动画系统 不推荐覆写 修改逻辑推荐覆写getAnim + registerAnim
        public @Nullable AnimationHolder ANIM_SYSTEM_GET_CURRENT_ANIM(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
            if (!this.IsRegistered) {
                this.registerAnim(player, animSystemData);
            }
            return this.getAnim(player, animSystemData);
        }
    }

    public static RegistryKey<Registry<AnimState>> animStateRegistryKey = RegistryKey.ofRegistry(ShapeShifterCurseFabric.identifier("anim_state"));
    public static Registry<AnimState> animStateRegistry = new SimpleRegistry<>(animStateRegistryKey, Lifecycle.stable());

    public static RegistryKey<Registry<Function<JsonObject, AbstractAnimStateController>>> animStateControllerRegistryKey = RegistryKey.ofRegistry(ShapeShifterCurseFabric.identifier("anim_state_controller"));
    public static Registry<Function<JsonObject, AbstractAnimStateController>> animStateControllerRegistry = new SimpleRegistry<>(animStateControllerRegistryKey, Lifecycle.stable());

    public static RegistryKey<Registry<AbstractAnimFSM>> animFSMRegistryKey = RegistryKey.ofRegistry(ShapeShifterCurseFabric.identifier("anim_fsm"));
    public static Registry<AbstractAnimFSM> animFSMRegistry = new SimpleRegistry<>(animFSMRegistryKey, Lifecycle.stable());

    public static RegistryKey<Registry<PowerDefaultAnim>> powerAnimIDRegistryKey = RegistryKey.ofRegistry(ShapeShifterCurseFabric.identifier("power_anim_id"));
    public static Registry<PowerDefaultAnim> powerAnimIDRegistry = new SimpleRegistry<>(powerAnimIDRegistryKey, Lifecycle.stable());

    public static Identifier registerAnimState(Identifier identifier, AnimState animState) {
        Registry.register(animStateRegistry, identifier, animState);
        return identifier;
    }

    // 用于数据包
    public static Identifier registerAnimStateController(Identifier identifier, Function<JsonObject, AbstractAnimStateController> animStateController) {
        Registry.register(animStateControllerRegistry, identifier, animStateController);
        return identifier;
    }

    public static Identifier registerAnimFSM(Identifier identifier, AbstractAnimFSM animFSM) {
        Registry.register(animFSMRegistry, identifier, animFSM);
        return identifier;
    }

    public static Identifier registerPowerDefaultAnim(Identifier identifier, PowerDefaultAnim powerDefaultAnim) {
        Registry.register(powerAnimIDRegistry, identifier, powerDefaultAnim);
        return identifier;
    }

    public static @Nullable AnimState getAnimState(Identifier identifier) {
        return animStateRegistry.get(identifier);
    }

    // 每个Form里都有一个预设了不同参数的AnimStateController
    public static @Nullable AbstractAnimStateController getAnimStateController(Identifier identifier, JsonObject jsonData) {
        Function<JsonObject, AbstractAnimStateController> animStateController = animStateControllerRegistry.get(identifier);
        if (animStateController != null) {
            return animStateController.apply(jsonData);
        }
        return null;
    }

    public static @Nullable Function<JsonObject, AbstractAnimStateController> getAnimStateControllerSupplier(Identifier identifier) {
        return animStateControllerRegistry.get(identifier);
    }

    public static @Nullable AbstractAnimFSM getAnimFSM(Identifier identifier) {
        return animFSMRegistry.get(identifier);
    }

    public static @Nullable PowerDefaultAnim getPowerDefaultAnim(Identifier identifier) {
        return powerAnimIDRegistry.get(identifier);
    }

    static {
        AnimRegistries.register();  // 注册基础的注册项 空方法体 用于强行加载AnimRegistries 别删
    }
}
