package net.onixary.shapeShifterCurseFabric.player_form.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.util.Verify.PatronDataSegment;
import org.jetbrains.annotations.Nullable;

public interface IPatronForm {
    default boolean checkCanUse(@Nullable PlayerEntity player, @Nullable PatronDataSegment patronData) {
        return true;
    }
}
