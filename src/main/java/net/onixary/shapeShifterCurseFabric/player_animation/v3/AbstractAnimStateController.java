package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAnimStateController {
    public abstract @Nullable AnimationHolder getAnimation(PlayerEntity player, AnimSystem.AnimSystemData data);
}
