package net.onixary.shapeShifterCurseFabric.player_form.utils;

import net.minecraft.entity.player.PlayerEntity;

public interface IFormWithCondition {
    default boolean checkCanUse(PlayerEntity player) {
        return true;
    }
}
