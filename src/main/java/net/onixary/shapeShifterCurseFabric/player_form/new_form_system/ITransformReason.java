package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface ITransformReason {
    public Identifier getReasonType();

    default @Nullable IForm getFallBackNextForm(PlayerEntity player, IForm nowForm) {
        return null;
    }

    default @Nullable IForm getFallBackPrevForm(PlayerEntity player, IForm nowForm) {
        return null;
    }
}
