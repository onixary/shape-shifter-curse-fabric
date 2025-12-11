package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface IPlayerAnimController {
    @Nullable
    Identifier shape_shifter_curse$getPowerAnimationID();

    // TODO 添加设置动画的方法
}
