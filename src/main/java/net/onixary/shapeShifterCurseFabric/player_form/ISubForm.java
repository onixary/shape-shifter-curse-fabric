package net.onixary.shapeShifterCurseFabric.player_form;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.player_form.utils.FormUtils;
import net.onixary.shapeShifterCurseFabric.player_form.utils.PlayerFormComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ISubForm extends IForm {
    default Boolean isSubForm() {
        return true;
    }

    @Nullable IForm getMasterForm();

    // 默认使用主形态的能力
    @Override
    default @NotNull Pair<Identifier, Identifier> getFormLayer() {
        IForm masterForm = this.getMasterForm();
        if (masterForm != null) {
            return masterForm.getFormLayer();
        }
        throw new RuntimeException("Master form is null");
    }

    @Nullable Pair<List<Identifier>, List<Identifier>> getLayerModifier();

    @Override
    default void afterApplyLayer(PlayerEntity player) {
        Pair<List<Identifier>, List<Identifier>> modifier = getLayerModifier();
        if (modifier != null) {
            Identifier powerSource = getFormLayer().getRight();
            List<Identifier> addPowerList = modifier.getLeft();
            List<Identifier> removePowerList = modifier.getRight();
            for (Identifier powerID : addPowerList) {
                FormUtils.applyPower(player, powerID, powerSource);
            }
            for (Identifier powerID : removePowerList) {
                FormUtils.removePower(player, powerID, powerSource);
            }
        }
    }

    @Override
    default void onTransform_Finish(PlayerEntity player) {
        PlayerFormComponent pfc = PlayerFormComponent.COMPONENT.get(player);
        pfc.setFallbackForm(this.getMasterForm().getFormID());
    }

    @Override
    default void onRegister() {
        IForm masterForm = this.getMasterForm();
        if (masterForm != null) {
            RegPlayerForms.registerSubForm(masterForm, this);
        }
    }
}
