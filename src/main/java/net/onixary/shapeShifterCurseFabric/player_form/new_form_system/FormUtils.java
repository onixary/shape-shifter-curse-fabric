package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FormUtils {
    public static @NotNull IForm getPlayerForm(PlayerEntity player) {
        // 未完工
        return null;
    }

    public static @NotNull List<IForm> getPlayerFormHistory(PlayerEntity player) {
        // 未完工
        return null;
    }

    public static void savePlayerFormHistory(PlayerEntity player) {
        // 未完工
        return;
    }

    public static boolean isFormEqual(@Nullable IForm form1, @Nullable IForm form2) {
        return form1 != null && form2 != null && form1.isEquals(form2);
    }

    public static @Nullable IForm getPrevForm(PlayerEntity player) {
        List<IForm> formHistory = getPlayerFormHistory(player);
        if (formHistory.size() > 1 && isFormEqual(getPlayerForm(player), formHistory.get(formHistory.size() - 1))) {
            return formHistory.get(formHistory.size() - 2);
        }
        return null;
    }

    public static void ensureHistoryCurrent(PlayerEntity player) {
        List<IForm> formHistory = getPlayerFormHistory(player);
        if (!formHistory.isEmpty() && !isFormEqual(getPlayerForm(player), formHistory.get(formHistory.size() - 1))) {
            formHistory.clear();
            ShapeShifterCurseFabric.LOGGER.warn("Player " + player.getName().getString() + " form history data error. clear form history data.");
            savePlayerFormHistory(player);
        }
    }

    public static void _loadForm(PlayerEntity player, IForm form) {
        // 未完工
    }

    public static void _setForm(PlayerEntity player, IForm form) {
        IForm prevForm = getPlayerForm(player);
        prevForm.onTransform_To(player, form);
        form.onTransform_From(player, prevForm);
        _loadForm(player, form);
        form.onTransform_Finish(player);
    }

    public static void setForm(PlayerEntity player, IForm form) {
        _setForm(player, form);
        List<IForm> formHistory = getPlayerFormHistory(player);
        formHistory.clear();
        formHistory.add(form);
        savePlayerFormHistory(player);
    }

    public static void setFormNextLevel(PlayerEntity player, ITransformReason reason) {
        IForm form = getPlayerForm(player);
        IForm nextForm = form._getNextForm(player, reason);
        _setForm(player, nextForm);
        getPlayerFormHistory(player).add(nextForm);
        savePlayerFormHistory(player);
    }

    public static void setFormPrevLevel(PlayerEntity player, ITransformReason reason) {
        IForm form = getPlayerForm(player);
        IForm prevForm = form._getPrevForm(player, reason);
        _setForm(player, prevForm);
        List<IForm> formHistory = getPlayerFormHistory(player);
        if (formHistory.size() > 1 && isFormEqual(formHistory.get(formHistory.size() - 1), form) && isFormEqual(formHistory.get(formHistory.size() - 2), prevForm)) {
            formHistory.remove(formHistory.size() - 1);
        } else {
            formHistory.clear();
            ShapeShifterCurseFabric.LOGGER.warn("Player " + player.getName().getString() + " prev form data error. clear prev form data.");
        }
        savePlayerFormHistory(player);
    }
}
