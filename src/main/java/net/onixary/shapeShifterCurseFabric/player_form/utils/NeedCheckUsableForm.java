package net.onixary.shapeShifterCurseFabric.player_form.utils;

import net.minecraft.entity.player.PlayerEntity;

public interface NeedCheckUsableForm {
    default boolean IsPlayerCanUse(PlayerEntity player) {
        return true;
    }
}
