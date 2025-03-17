package net.onixary.shapeShifterCurseFabric.player_form.ability;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;

public class PlayerFormComponent implements AutoSyncedComponent {
    private PlayerForms currentForm = PlayerForms.ORIGINAL_BEFORE_ENABLE;
    private PlayerForms previousForm = PlayerForms.ORIGINAL_BEFORE_ENABLE;
    // is current form caused by cursed moon
    private boolean isByCursedMoon = false;
    // is current form regressed from final form by cursed moon
    private boolean isRegressedFromFinal = false;
    // is current form caused by cure
    // used to handle when player cured self when under cursed moon
    private boolean isByCure = false;

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        // 读取状态枚举
        try {
            this.currentForm = PlayerForms.valueOf(nbtCompound.getString("currentForm"));
            this.previousForm = PlayerForms.valueOf(nbtCompound.getString("previousForm"));
            this.isByCursedMoon = nbtCompound.getBoolean("isByCursedMoon");
            this.isRegressedFromFinal = nbtCompound.getBoolean("isRegressedFromFinal");
            this.isByCure = nbtCompound.getBoolean("isByCure");
        } catch (IllegalArgumentException e) {
            this.currentForm = PlayerForms.ORIGINAL_BEFORE_ENABLE;
            this.previousForm = PlayerForms.ORIGINAL_BEFORE_ENABLE;
            this.isByCursedMoon = false;
            this.isRegressedFromFinal = false;
            this.isByCure = false;
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putString("currentForm", currentForm.name());
        nbtCompound.putString("previousForm", previousForm.name());
        nbtCompound.putBoolean("isByCursedMoon", isByCursedMoon);
        nbtCompound.putBoolean("isRegressedFromFinal", isRegressedFromFinal);
        nbtCompound.putBoolean("isByCure", isByCure);
    }

    public PlayerForms getCurrentForm() {
        return currentForm;
    }

    public PlayerForms getPreviousForm() {
        return previousForm;
    }

    public boolean isByCursedMoon() {
        return isByCursedMoon;
    }

    public void setByCursedMoon(boolean byCursedMoon) {
        isByCursedMoon = byCursedMoon;
    }

    public boolean isRegressedFromFinal() {
        return isRegressedFromFinal;
    }

    public void setRegressedFromFinal(boolean regressedFromFinal) {
        isRegressedFromFinal = regressedFromFinal;
    }

    public boolean isByCure() {
        return isByCure;
    }

    public void setByCure(boolean byCure) {
        isByCure = byCure;
    }

    public void setCurrentForm(PlayerForms form) {
        this.previousForm = this.currentForm;
        this.currentForm = form;
    }
}
