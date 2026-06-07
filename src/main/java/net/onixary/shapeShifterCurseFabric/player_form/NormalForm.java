package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class NormalForm implements IForm {
    private final Identifier FORM_ID;
    private IFormGroup formGroup = null;
    private int formTier = -2;
    private Set<String> formFlag = Set.of();
    private PlayerFormBodyType bodyType = PlayerFormBodyType.NORMAL;
    private @Nullable Consumer<PlayerEntity> applyScaleFunc = null;

    public static final BiFunction<Float, Float, Consumer<PlayerEntity>> NORMAL_SCALE_FUNC_BUILDER = (scale, eye_scale) -> (player) -> {
        ScaleData scaleDataWidth = ScaleTypes.WIDTH.getScaleData(player);
        ScaleData scaleDataHeight = ScaleTypes.HEIGHT.getScaleData(player);
        scaleDataWidth.setScale(scale);
        scaleDataWidth.setPersistence(true);
        scaleDataHeight.setScale(scale);
        scaleDataHeight.setPersistence(true);
        ScaleData scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(player);
        ScaleData scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(player);
        scaleDataEyeHeight.setScale(eye_scale);
        scaleDataEyeHeight.setPersistence(true);
        scaleDataHitboxHeight.setScale(eye_scale);
        scaleDataHitboxHeight.setPersistence(true);
    };

    public static final Consumer<PlayerEntity> RESET_SCALE_FUNC = NORMAL_SCALE_FUNC_BUILDER.apply(1.0f, 1.0f);

    public NormalForm(Identifier formID) {
        this.FORM_ID = formID;
    }

    @Override
    public @NotNull Identifier getFormID() {
        return this.FORM_ID;
    }

    @Override
    public @NotNull Set<String> getFormFlag() {
        return this.formFlag;
    }

    public NormalForm formFlag(String... flag) {
        this.formFlag = Set.of(flag);
        return this;
    }

    public NormalForm formFlag(FormUtils.FlagData... flag) {
        Set<String> flagSet = new HashSet<>();
        for (FormUtils.FlagData flagData : flag) {
            flagSet.add(flagData.getFlag());
        }
        this.formFlag = Set.copyOf(flagSet);
        return this;
    }

    public NormalForm appendFlag(FormUtils.FlagData... flag) {
        Set<String> flagSet = new HashSet<>(this.formFlag);
        for (FormUtils.FlagData flagData : flag) {
            flagSet.add(flagData.getFlag());
        }
        this.formFlag = Set.copyOf(flagSet);
        return this;
    }

    @Override
    public int getFormTier() {
        return this.formTier;
    }

    @Override
    public @Nullable IFormGroup getFormGroup() {
        return formGroup;
    }

    @Override
    public void setFormGroup(IFormGroup group, int formTier) {
        this.formGroup = group;
        this.formTier = formTier;
    }

    @Override
    public @NotNull Pair<Identifier, Identifier> getFormLayer() {
        return new Pair<>(Identifier.of("origins", "origin"), Identifier.of(this.FORM_ID.getNamespace(), "form_" + this.FORM_ID.getPath()));
    }

    @Override
    public @NotNull PlayerFormBodyType getBodyType() {
        return this.bodyType;
    }

    public NormalForm bodyType(PlayerFormBodyType bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    @Override
    public @Nullable IForm getNextForm(PlayerEntity player, ITransformReason reason) {
        return null;
    }

    @Override
    public @Nullable IForm getPrevForm(PlayerEntity player, ITransformReason reason) {
        return null;
    }

    @Override
    public void applyScale(PlayerEntity player) {
        if (this.applyScaleFunc != null) {
            this.applyScaleFunc.accept(player);
            return;
        }
    }

    public NormalForm applyScaleFunc(Consumer<PlayerEntity> func) {
        this.applyScaleFunc = func;
        return this;
    }

    // 所有形态必须重载 equals 函数 由于IForm是接口 没法重载Object的函数
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IForm iForm) {
            return this.isEquals(iForm);
        }
        return false;
    }
}
