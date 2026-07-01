package net.onixary.shapeShifterCurseFabric.player_form.utils;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
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
import net.onixary.shapeShifterCurseFabric.networking.ModPacketsS2CServer;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimUtils;
import net.onixary.shapeShifterCurseFabric.player_form.DynamicForm;
import net.onixary.shapeShifterCurseFabric.player_form.IForm;
import net.onixary.shapeShifterCurseFabric.player_form.ITransformReason;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.status_effects.attachment.EffectManager;
import net.onixary.shapeShifterCurseFabric.util.TrinketUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
    public static final FlagData CursedMoonFinalForm = new FlagData("cursed_moon_final_form"); // 当诅咒之月进化此形态时 会自动回退到Tier1
    public static final FlagData NoCursedMoonTFTarget = new FlagData("no_cursed_moon_target"); // 禁止诅咒之月变形至目标形态 给蜘蛛茧用
    public static final FlagData NoCursedMoonEffect = new FlagData("no_cursed_moon_effect"); // 免疫诅咒之月效果 给开书前 SP 最终形态用
    public static final FlagData NoInstinctTFTarget = new FlagData("no_instinct_target"); // 禁止本能系统变形至目标形态
    public static final FlagData InhibitorResist = new FlagData("inhibitor_resist");  // 禁止普通抑制剂 给最后一个可退回形态用
    public static final FlagData InhibitorImmune = new FlagData("inhibitor_immune"); // 禁止常规抑制剂(除了创造版本) 给最终形态用
    public static final FlagData CatalystResist = new FlagData("catalyst_resist"); // 禁止普通催化剂
    public static final FlagData CatalystImmune = new FlagData("catalyst_immune"); // 禁止催化剂
    public static final FlagData StarterForm = new FlagData("starter_form"); // 诅咒之月给开书后形态随机挑的形态
    public static final FlagData SpecialForm = new FlagData("special_form"); // SP形态
    public static final FlagData CanTFToFinalForm = new FlagData("can_tf_to_final_form"); // 可以通过高级催化剂变形到最终形态
    public static final FlagData FinalForm = new FlagData("final_form"); // 最终形态 PowerfulCatalyst仅能变形到此形态

    public static record ExtraPower(@NotNull Identifier LayerID, @NotNull Identifier FormID, @NotNull List<Identifier> PowerIDs) {
        public @NotNull Identifier getLayerID() { return LayerID; }
        public @NotNull Identifier getFormID() { return FormID; }
        public @NotNull List<Identifier> getPowerIDs() { return PowerIDs; }

        public boolean canApply(Identifier layerID, Identifier formID) {
            return getLayerID().equals(layerID) && getFormID().equals(formID);
        }
    }

    public static void applyPower(PlayerEntity player, Identifier powerId, Identifier powerSource) {
        if (PowerTypeRegistry.contains(powerId)) {
            PowerType<?> powerType = PowerTypeRegistry.get(powerId);
            if (powerType != null) {
                PowerHolderComponent powerHolder = PowerHolderComponent.KEY.get(player);
                powerHolder.addPower(powerType, powerSource);
            }
        }
        else {
            new Thread(() -> {
                try {
                    boolean FoundPower = false;
                    for (int i = 0; i < 20; i++) {
                        Thread.sleep(100);
                        if (PowerTypeRegistry.contains(powerId)) {
                            FoundPower = true;
                            break;
                        }
                    }
                    if (FoundPower) {
                        PowerType<?> powerType = PowerTypeRegistry.get(powerId);
                        if (powerType != null) {
                            PowerHolderComponent powerHolder = PowerHolderComponent.KEY.get(player);
                            powerHolder.addPower(powerType, powerSource);
                        }
                    }
                    else {
                        ShapeShifterCurseFabric.LOGGER.warn("Failed to apply power " + powerId.toString() + " for player " + player.getName() + " after 2 seconds");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void removePower(PlayerEntity player, Identifier powerId, Identifier powerSource) {
        PowerType<?> powerType = PowerTypeRegistry.get(powerId);
        if (powerType != null) {
            PowerHolderComponent powerHolder = PowerHolderComponent.KEY.get(player);
            powerHolder.removePower(powerType, powerSource);
        }
    }

    public static final HashMap<Identifier, ExtraPower> extraPowerRegistry = new HashMap<>();
    public static void registerExtraPower(Identifier identifier, ExtraPower extraPower) {
        extraPowerRegistry.put(identifier, extraPower);
    }

    public static void applyExtraPower(PlayerEntity player, Pair<Identifier, Identifier> layerData) {
        extraPowerRegistry.forEach((id, extraPower) -> {
            if (extraPower.canApply(layerData.getLeft(), layerData.getRight())) {
                extraPower.getPowerIDs().forEach(powerId -> applyPower(player, powerId, layerData.getRight()));
            }
        });
    }

    public static void applyLayer(PlayerEntity player, Pair<Identifier, Identifier> layerData) {
        // 临时 等移除Origins后再重新这部分
        OriginComponent component = ModComponents.ORIGIN.get(player);
        OriginLayer layer = OriginLayers.getLayer(layerData.getLeft());
        if (layer != null && layerData.getRight() != null) {
            Origin origin = OriginRegistry.get(layerData.getRight());
            if(layer.contains(origin, player)){
                component.setOrigin(layer, origin);
                component.sync();
            }
        }
        applyExtraPower(player, layerData);
    }

    public static Set<String> buildFormFlag(FlagData... flags) {
        Set<String> flagSet = new HashSet<>();
        for (FlagData flag : flags) {
            flagSet.add(flag.getFlag());
        }
        return Set.copyOf(flagSet);
    }

    public static @Nullable IForm getForm(@NotNull Identifier formID) {
        return RegPlayerForms.getPlayerForm(formID);
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
        PlayerFormComponent playerFormComponent = PlayerFormComponent.COMPONENT.get(player);
        playerFormComponent.setForm(form);
        playerFormComponent.sync();

        if (!EffectManager.playerCanHaveTransformativeEffect(player)) {
            EffectManager.clearTransformativeEffect(player);
        }
        // 应用Scale
        form.applyScale(player);
        // 应用Power Origin -> OriginExtraPower -> AccessoryPower
        Pair<Identifier, Identifier> layerPair = form.getFormLayer();
        applyLayer(player, layerPair);
        form.afterApplyLayer(player);
        TrinketUtils.ReApplyAccessoryPowerOnPlayerFormChange(player);
        form.onApplyPowerEnd(player);
        // 停止Power动画 目前就蝙蝠用了
        AnimUtils.stopPowerAnim(player, AnimUtils.AnimationSendSideType.ONLY_SERVER);

        if (!player.getWorld().isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            try {
                ModPacketsS2CServer.sendFormChange(serverPlayer, form.getFormID());
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
        for (IForm form : RegPlayerForms.playerForms.values()) {
            if (predicate.test(form)) {
                result.add(form);
            }
        }
        return result;
    }
}
