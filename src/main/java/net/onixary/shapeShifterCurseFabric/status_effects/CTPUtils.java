package net.onixary.shapeShifterCurseFabric.status_effects;

import com.google.common.base.Objects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;
import net.onixary.shapeShifterCurseFabric.player_form.ability.PlayerFormComponent;
import net.onixary.shapeShifterCurseFabric.player_form.ability.RegPlayerFormComponent;

// Custom Transformative Potion Utils (CTP)
public class CTPUtils {
    public interface CTPFormIDHolder {
        Identifier getCTPFormID();
        void setCTPFormID(Identifier formID);
    }

    public static PlayerFormBase getTransformativePotionForm(PlayerEntity player) {
        if (player == null) {
            ShapeShifterCurseFabric.LOGGER.error("CustomTransformativeStatue PlayerEntity is null");
            return RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
        }
        PlayerFormComponent playerFormComponent = RegPlayerFormComponent.PLAYER_FORM.get(player);
        if (playerFormComponent == null) {
            ShapeShifterCurseFabric.LOGGER.error("CustomTransformativeStatue PlayerFormComponent is null");
            return RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
        }
        return RegPlayerForms.getPlayerFormOrDefault(playerFormComponent.getCustomPotionFormID(), RegPlayerForms.ORIGINAL_BEFORE_ENABLE);
    }

    public static void setTransformativePotionForm(PlayerEntity player, Identifier formID) {
        if (player == null) {
            ShapeShifterCurseFabric.LOGGER.error("CustomTransformativeStatue PlayerEntity is null");
            return;
        }
        PlayerFormComponent playerFormComponent = RegPlayerFormComponent.PLAYER_FORM.get(player);
        if (playerFormComponent == null) {
            ShapeShifterCurseFabric.LOGGER.error("CustomTransformativeStatue PlayerFormComponent is null");
            return;
        }
        if (!Objects.equal(playerFormComponent.getCustomPotionFormID(), formID)) {
            playerFormComponent.setCustomPotionFormID(formID);
            RegPlayerFormComponent.PLAYER_FORM.sync(player);
        }
    }

    public static void resetTransformativePotionForm(PlayerEntity player) {
        setTransformativePotionForm(player, RegPlayerForms.ORIGINAL_BEFORE_ENABLE.FormID);
    }

    public static Identifier getCTPFormIDFromNBT(NbtCompound nbtCompound) {
        if (nbtCompound == null) {
            return null;
        }
        if (nbtCompound.contains("targetForm")) {
            return Identifier.tryParse(nbtCompound.getString("targetForm"));
        }
        return null;
    }
}
