package net.onixary.shapeShifterCurseFabric.player_form;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DynamicForm implements IForm {
    @Override
    public @NotNull Identifier getFormID() {
        return null;
    }

    @Override
    public @NotNull Set<String> getFormFlag() {
        return Set.of();
    }

    @Override
    public int getFormTier() {
        return 0;
    }

    @Override
    public @Nullable IFormGroup getFormGroup() {
        return null;
    }

    @Override
    public void setFormGroup(IFormGroup group, int formTier) {

    }

    @Override
    public @NotNull Pair<Identifier, Identifier> getFormLayer() {
        return null;
    }

    @Override
    public @NotNull PlayerFormBodyType getBodyType() {
        return null;
    }

    @Override
    public void applyScale(PlayerEntity player) {

    }

    public static DynamicForm fromJson(@Nullable Identifier identifier, JsonObject data) {
        return null;
    }
}
