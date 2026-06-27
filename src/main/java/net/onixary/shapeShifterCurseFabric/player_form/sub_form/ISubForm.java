package net.onixary.shapeShifterCurseFabric.player_form.sub_form;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ISubForm {
    public String getSubFormID();

    public @NotNull IForm getRootForm();

    default PlayerFormBodyType getBodyType() {
        return getRootForm().getBodyType();
    }

    default @NotNull Text getContentText(CodexData.ContentType type) {
        return getRootForm().getContentText(type);
    }

    default @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        return getRootForm().getAnimStateController(player, animSystemData, animStateID);
    }

    default void registerPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        getRootForm().registerPowerAnim(player, animSystemData);
    }

    default boolean isPowerAnimRegistered(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        return getRootForm().isPowerAnimRegistered(player, animSystemData);
    }

    default @NotNull Pair<Boolean, @Nullable AnimationHolder> getPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier powerAnimID) {
        return getRootForm().getPowerAnim(player, animSystemData, powerAnimID);
    }

    default void applyScale(PlayerEntity player) {
        getRootForm().applyScale(player);
    }
}
