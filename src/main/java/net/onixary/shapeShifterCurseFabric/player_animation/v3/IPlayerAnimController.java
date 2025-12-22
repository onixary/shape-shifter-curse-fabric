package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IPlayerAnimController {
    @Nullable Identifier shape_shifter_curse$getPowerAnimationID();

    int shape_shifter_curse$getPowerAnimationCount();

    int shape_shifter_curse$getPowerAnimationTime();

    void shape_shifter_curse$playAnimationWithCount(@NotNull Identifier id, int PlayCount);

    void shape_shifter_curse$playAnimationWithTime(@NotNull Identifier id, int Time);

    void shape_shifter_curse$playAnimationLoop(@NotNull Identifier id);

    void shape_shifter_curse$stopAnimation();

    void shape_shifter_curse$animationDoneCallBack(@NotNull Identifier id);

    void shape_shifter_curse$setAnimationData(@Nullable Identifier id, int count, int time);

}
