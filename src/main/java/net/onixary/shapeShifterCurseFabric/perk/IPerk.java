package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;

// Server Side
public interface IPerk {
    Identifier getID();

    default void onGain(PlayerEntity player, IForm form) {
        this.onLoad(player, form);
    }

    default boolean canGain(PlayerEntity player, IForm form) {
        return true;  // Tier 和 DependentPerkID 的判定由 PerkTree 处理
    }

    default void onLoad(PlayerEntity player, IForm form) { }
}
