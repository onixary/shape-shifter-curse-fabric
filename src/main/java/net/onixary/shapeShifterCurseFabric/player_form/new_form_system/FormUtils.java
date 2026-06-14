package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.integration.origins.component.OriginComponent;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.Origin;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayer;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginLayers;
import net.onixary.shapeShifterCurseFabric.integration.origins.origin.OriginRegistry;
import net.onixary.shapeShifterCurseFabric.integration.origins.registry.ModComponents;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.util.TrinketUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    public static final FlagData NoCursedMoonTFTarget = new FlagData("no_cursed_moon_target"); // 禁止诅咒之月变形至目标形态 给蜘蛛茧 SP形态用
    public static final FlagData NoCursedMoonEffect = new FlagData("no_cursed_moon_effect"); // 免疫诅咒之月效果 给开书前 SP 最终形态用
    public static final FlagData NoInstinctTFTarget = new FlagData("no_instinct_target"); // 禁止本能系统变形至目标形态
    public static final FlagData FinalForm = new FlagData("final_form"); // 最终形态 PowerfulCatalyst仅能变形到此形态
    public static final FlagData NoInhibitor = new FlagData("no_inhibitor");  // 禁止普通抑制剂 给最后一个可退回形态用
    public static final FlagData NoAnyInhibitor = new FlagData("no_any_inhibitor"); // 禁止常规抑制剂(除了创造版本) 给最终形态用
    public static final FlagData StarterForm = new FlagData("starter_form"); // 诅咒之月给开书后形态随机挑的形态

    public static Set<String> buildFormFlag(FlagData... flags) {
        Set<String> flagSet = new HashSet<>();
        for (FlagData flag : flags) {
            flagSet.add(flag.getFlag());
        }
        return Set.copyOf(flagSet);
    }

    public static @Nullable IForm getForm(@NotNull Identifier formID) {
        return null;
    }

    public static @NotNull IForm parseForm(@Nullable Identifier formID, IForm defaultForm) {
        if (formID == null) return defaultForm;
        IForm form = getForm(formID);
        return form != null ? form : defaultForm;
    }

    public static @NotNull IForm getPlayerForm(PlayerEntity player) {
        return PlayerFormComponent.COMPONENT.get(player).nowForm;
    }

    public static @NotNull List<IForm> getPlayerFormHistory(PlayerEntity player) {
        return PlayerFormComponent.COMPONENT.get(player).formHistory;
    }

    public static void savePlayerFormHistory(PlayerEntity player) {
        PlayerFormComponent.COMPONENT.sync(player);
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
        // 临时 等移除Origins后再重新这部分
        OriginComponent component = ModComponents.ORIGIN.get(player);
        Pair<Identifier, Identifier> layerPair = form.getFormLayer();
        OriginLayer layer = OriginLayers.getLayer(layerPair.getLeft());
        if (layer != null && layerPair.getRight() != null) {
            Origin origin = OriginRegistry.get(layerPair.getRight());
            if(layer.contains(origin, player)){
                component.setOrigin(layer, origin);
                component.sync();
            }
        }

        // applyExtraPower
        // checkAndClearTransformativeEffect

        PlayerFormComponent playerFormComponent = PlayerFormComponent.COMPONENT.get(player);
        playerFormComponent.nowForm = form;
        playerFormComponent.sync();

        AnimUtils.stopPowerAnim(player, AnimUtils.AnimationSendSideType.ONLY_SERVER);
        TrinketUtils.ReApplyAccessoryPowerOnPlayerFormChange(player);
        if (!player.getWorld().isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            try {
                // 改成Identifier
                // ModPacketsS2CServer.sendFormChange(serverPlayer, form.getFormID());
            } catch (Exception e) {
                ShapeShifterCurseFabric.LOGGER.error("Failed to send form change notification: ", e);
            }
        }
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

    public static void pushFormHistory(PlayerEntity player, IForm form) {
        List<IForm> formHistory = getPlayerFormHistory(player);
        formHistory.add(form);
        savePlayerFormHistory(player);
    }

    public static void checkAndPullFormHistory(PlayerEntity player, IForm lastForm, IForm prevForm) {
        List<IForm> formHistory = getPlayerFormHistory(player);
        if (formHistory.size() > 1 && isFormEqual(formHistory.get(formHistory.size() - 1), lastForm) && isFormEqual(formHistory.get(formHistory.size() - 2), prevForm)) {
            formHistory.remove(formHistory.size() - 1);
        } else {
            formHistory.clear();
            ShapeShifterCurseFabric.LOGGER.warn("Player " + player.getName().getString() + " form history data error. clear form history data.");
        }
        savePlayerFormHistory(player);
    }

    public static void updateFormHistory(PlayerEntity player, IForm formA, IForm formB) {
        // 如果History为[C, B, A] formA == A formB == B History -> [C, B] 否则向后增加 formB
        List<IForm> formHistory = getPlayerFormHistory(player);
        if (formHistory.size() > 1 && isFormEqual(formHistory.get(formHistory.size() - 1), formA) && isFormEqual(formHistory.get(formHistory.size() - 2), formB)) {
            formHistory.remove(formHistory.size() - 1);
        } else {
            formHistory.add(formB);
        }
        savePlayerFormHistory(player);
    }

    public static @NotNull IForm getFormNextLevel(PlayerEntity player, ITransformReason reason) {
        IForm form = getPlayerForm(player);
        return form._getNextForm(player, reason);
    }

    public static @NotNull IForm getFormPrevLevel(PlayerEntity player, ITransformReason reason) {
        IForm form = getPlayerForm(player);
        return form._getPrevForm(player, reason);
    }

    public static @NotNull List<IForm> getFormByCondition(@NotNull Predicate<IForm> predicate) {
        List<IForm> result = new ArrayList<>();
        // TODO 改完注册表后写
        return result;
    }
}
