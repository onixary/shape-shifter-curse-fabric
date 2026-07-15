package net.onixary.shapeShifterCurseFabric.player_form.utils;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface IFormWithCondition {
    default boolean checkCanUse(@Nullable PlayerEntity player) {
        return true;
    }
}
