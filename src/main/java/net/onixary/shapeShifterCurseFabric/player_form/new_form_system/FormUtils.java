package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class FormUtils {
    public static class FlagData {
        public final String flag;
        public FlagData(String flag) { this.flag = flag; }
        public String getFlag() { return flag; }
        public boolean hasFlag(IForm form) { return form.getFormFlag().contains(flag); }
        public Set<String> appendFlag(Set<String> oldFlag) {
            Set<String> newSet = new HashSet<>(oldFlag);
            newSet.add(flag);
            return Set.copyOf(newSet);
        }
        public Predicate<IForm> hasFlag() { return form -> form.getFormFlag().contains(flag); }
    }

    public static final FlagData HasSlowFall = new FlagData("slow_fall"); // 给动画系统用的 替代hasSlowFall函数
    public static final FlagData NoInstinct = new FlagData("no_instinct"); // 禁用本能系统(条消失) 给sp 开书前后 最终形态用
    public static final FlagData LockInstinct = new FlagData("lock_instinct"); // 锁定本能系统(条不消失) 给最后一个可退回形态用
    public static final FlagData NoInhibitor = new FlagData("no_inhibitor");  // 禁止常规抑制剂(除了创造版本) 给最终形态用
    public static final FlagData NoCursedMoonTFTarget = new FlagData("no_cursed_moon_target"); // 禁止诅咒之月变形至目标形态 给蜘蛛茧 SP形态用
    public static final FlagData NoCursedMoonEffect = new FlagData("no_cursed_moon_effect"); // 免疫诅咒之月效果 给开书前 SP 最终形态用
    public static final FlagData NoInstinctTFTarget = new FlagData("no_instinct_target"); // 禁止本能系统变形至目标形态

    public static Set<String> buildFormFlag(FlagData... flags) {
        Set<String> flagSet = new HashSet<>();
        for (FlagData flag : flags) {
            flagSet.add(flag.getFlag());
        }
        return Set.copyOf(flagSet);
    }

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
