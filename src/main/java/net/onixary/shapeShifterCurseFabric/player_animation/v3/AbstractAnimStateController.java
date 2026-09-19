package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractAnimStateController {
    public abstract @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data);

    private boolean isRegistered = false;

    public boolean isRegistered(PlayerEntity player, AnimSystem.AnimSystemData data) {
        return isRegistered;
    }

    public void registerAnim(PlayerEntity player, AnimSystem.AnimSystemData data) {
        isRegistered = true;
    }

    // 仅用于特殊系统 比如TransformController 其他系统请勿使用(没对应逻辑)
    public boolean isEnabled(PlayerEntity player, AnimSystem.AnimSystemData data) {
        return true;
    }

    public @NotNull List<AnimationHolder> getAllAnimations() {
        return List.of();
    }
}
