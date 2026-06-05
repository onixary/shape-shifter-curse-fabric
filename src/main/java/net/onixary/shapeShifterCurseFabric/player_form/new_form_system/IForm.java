package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.data.CodexData;
import net.onixary.shapeShifterCurseFabric.player_animation.AnimationHolder;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AbstractAnimStateController;
import net.onixary.shapeShifterCurseFabric.player_animation.v3.AnimSystem;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

// 新形态变形引擎代码

public interface IForm {
    public @NotNull Identifier getFormID();

    // HasSlowFall 整合进 flag 系统
    public @NotNull Set<String> getFormFlag();

    public int getFormTier();

    public @Nullable IFormGroup getFormGroup();

    public void setFormGroup(IFormGroup group, int formTier);

    // 临时能力系统 等Origins移除后再写
    public @Nullable Pair<Identifier, Identifier> getFormLayer();


    public @NotNull PlayerFormBodyType getBodyType();

    // 将 Name 合并进 ContentType
    public default @NotNull Text getContentText(CodexData.ContentType type) {
        return Text.translatable("codex.form." + this.getFormID().getNamespace() + "." + this.getFormID().getPath() + "." + type.toString().toLowerCase());
    }

    // 变形系统
    public default @NotNull IForm _getNextForm(PlayerEntity player, ITransformReason reason) {
        IForm nextForm = getNextForm(player, reason);
        if (nextForm == null) {
            nextForm = reason.getFallBackNextForm(player, this);
        }
        if (nextForm == null) {
            nextForm = getDefaultNextForm(player, reason);
        }
        // 按代码来说是不可能为null的 但是getDefaultXXXX可能会被大量重载 所以还是加一个判断
        if (nextForm == null) {
            nextForm = this;
            ShapeShifterCurseFabric.LOGGER.error("Form {} has no next form, something wrong!", this.getFormID());
        }
        return nextForm;
    }

    public default @NotNull IForm _getPrevForm(PlayerEntity player, ITransformReason reason) {
        IForm prevForm = getPrevForm(player, reason);
        if (prevForm == null) {
            prevForm = reason.getFallBackPrevForm(player, this);
        }
        if (prevForm == null) {
            prevForm = getDefaultPrevForm(player, reason);
        }
        // 按代码来说是不可能为null的 但是getDefaultXXXX可能会被大量重载 所以还是加一个判断
        if (prevForm == null) {
            prevForm = this;
            ShapeShifterCurseFabric.LOGGER.error("Form {} has no prev form, something wrong!", this.getFormID());
        }
        return prevForm;
    }

    // 选择性处理 如果不匹配则必须返回null
    public default @Nullable IForm getNextForm(PlayerEntity player, ITransformReason reason) {
        return null;
    }

    public default @Nullable IForm getPrevForm(PlayerEntity player, ITransformReason reason) {
        return null;
    }

    public default @NotNull IForm getDefaultNextForm(PlayerEntity player, ITransformReason reason) {
        IFormGroup group = this.getFormGroup();
        int tier = this.getFormTier() + 1;
        IForm result = null;
        if (group != null) {
            result = group.getRandomForm(tier, player.getRandom());
        }
        return result == null ? this : result;
    }

    public default @NotNull IForm getDefaultPrevForm(PlayerEntity player, ITransformReason reason) {
        IForm prevForm = FormUtils.getPrevForm(player);
        int tier = this.getFormTier() - 1;
        if (prevForm != null && prevForm.getFormTier() == tier) {
            return prevForm;
        }
        IFormGroup group = this.getFormGroup();
        IForm result = null;
        if (group != null) {
            result = group.getRandomForm(tier, player.getRandom());
        }
        return result == null ? this : result;
    }

    // 动画系统
    public default @Nullable AbstractAnimStateController getAnimStateController(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier animStateID) {
        return null;
    }

    public default void registerPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) { }

    public default boolean isPowerAnimRegistered(PlayerEntity player, AnimSystem.AnimSystemData animSystemData) {
        return true;
    }

    public default @NotNull Pair<Boolean, @Nullable AnimationHolder> getPowerAnim(PlayerEntity player, AnimSystem.AnimSystemData animSystemData, @NotNull Identifier powerAnimID) {
        return new Pair<>(false, null);
    }

    // 3个Hook 顺序为当前形态onTransform_To 目标形态onTransform_From 目标形态onTransform_Finish
    default void onTransform_From(PlayerEntity player, IForm prevForm) { }

    default void onTransform_Finish(PlayerEntity player) { }

    default void onTransform_To(PlayerEntity player, IForm nextForm) { }

    // Scale 系统
    // 先这样写 等我之后翻一下 pehkui 的代码
    public void applyScale(PlayerEntity player);

    // Interface 没法重载boolean equal(Object)函数
    default boolean isEquals(IForm form) {
        return form != null && this.getFormID().equals(form.getFormID());
    }
}
