package net.onixary.shapeShifterCurseFabric.player_animation.v3;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IPlayerAnimController {

    @Nullable Identifier shape_shifter_curse$getPowerAnimationID();

    void shape_shifter_curse$playAnimation(@NotNull Identifier id, int PlayCount);

    void shape_shifter_curse$playAnimation(@NotNull Identifier id, boolean Loop);

    void shape_shifter_curse$animationDoneCallBack(@NotNull Identifier id);
}
