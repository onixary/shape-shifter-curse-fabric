package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBodyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Set;
import java.util.function.Consumer;

public class NormalForm implements IForm {
    private final Identifier FORM_ID;
    private IFormGroup formGroup = null;
    private int formTier = -2;
    private Set<String> formFlag = Set.of();
    private PlayerFormBodyType bodyType = PlayerFormBodyType.NORMAL;
    private @Nullable Consumer<PlayerEntity> applyScaleFunc = null;

    public static final Consumer<PlayerEntity> RESET_SCALE_FUNC = (player) -> {
        ScaleData scaleDataWidth = ScaleTypes.WIDTH.getScaleData(player);
        ScaleData scaleDataHeight = ScaleTypes.HEIGHT.getScaleData(player);
        scaleDataWidth.setScale(1.0f);
        scaleDataWidth.setPersistence(true);
        scaleDataHeight.setScale(1.0f);
        scaleDataHeight.setPersistence(true);
        ScaleData scaleDataEyeHeight = ScaleTypes.EYE_HEIGHT.getScaleData(player);
        ScaleData scaleDataHitboxHeight = ScaleTypes.HITBOX_HEIGHT.getScaleData(player);
        scaleDataEyeHeight.setScale(1.0f);
        scaleDataEyeHeight.setPersistence(true);
        scaleDataHitboxHeight.setScale(1.0f);
        scaleDataHitboxHeight.setPersistence(true);
    };

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
    public @Nullable Pair<Identifier, Identifier> getFormLayer() {
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
}
