package net.onixary.shapeShifterCurseFabric.player_form.ability;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.RegPlayerForms;

public class PlayerFormComponent implements AutoSyncedComponent {
    private PlayerFormBase currentForm = RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
    private PlayerFormBase previousForm = RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
    // is current form caused by cursed moon
    private boolean isByCursedMoon = false;
    // is current form regressed from final form by cursed moon
    private boolean isRegressedFromFinal = false;
    // is current form caused by cure
    // used to handle when player cured self when under cursed moon
    private boolean isByCure = false;
    private boolean moonEffectApplied = false;
    private boolean endMoonEffectApplied = false;
    private boolean isByCursedMoonEnd = false;
    private boolean firstJoin = true; // 默认为true，表示首次加入

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        // 读取状态枚举
        try {
            this.currentForm = RegPlayerForms.getPlayerFormOrDefault(nbtCompound.getString("currentForm"), RegPlayerForms.ORIGINAL_BEFORE_ENABLE);
            this.previousForm = RegPlayerForms.getPlayerFormOrDefault(nbtCompound.getString("previousForm"), RegPlayerForms.ORIGINAL_BEFORE_ENABLE);
            this.isByCursedMoon = nbtCompound.getBoolean("isByCursedMoon");
            this.isRegressedFromFinal = nbtCompound.getBoolean("isRegressedFromFinal");
            this.isByCure = nbtCompound.getBoolean("isByCure");
            this.moonEffectApplied = nbtCompound.getBoolean("moonEffectApplied");
            this.endMoonEffectApplied = nbtCompound.getBoolean("endMoonEffectApplied");
            this.isByCursedMoonEnd = nbtCompound.getBoolean("isByCursedMoonEnd");
            this.firstJoin = nbtCompound.getBoolean("firstJoin");
        } catch (IllegalArgumentException e) {
            this.currentForm = RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
            this.previousForm = RegPlayerForms.ORIGINAL_BEFORE_ENABLE;
            this.isByCursedMoon = false;
            this.isRegressedFromFinal = false;
            this.isByCure = false;
            this.moonEffectApplied = false;
            this.endMoonEffectApplied = false;
            this.isByCursedMoonEnd = false;
            this.firstJoin = true;
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putString("currentForm", this.currentForm.name());
        nbtCompound.putString("previousForm", this.previousForm.name());
        nbtCompound.putBoolean("isByCursedMoon", this.isByCursedMoon);
        nbtCompound.putBoolean("isRegressedFromFinal", this.isRegressedFromFinal);
        nbtCompound.putBoolean("isByCure", this.isByCure);
        nbtCompound.putBoolean("moonEffectApplied", this.moonEffectApplied);
        nbtCompound.putBoolean("endMoonEffectApplied", this.endMoonEffectApplied);
        nbtCompound.putBoolean("isByCursedMoonEnd", this.isByCursedMoonEnd);
        nbtCompound.putBoolean("firstJoin", this.firstJoin);
    }

    public PlayerFormBase getCurrentForm() {
        return currentForm;
    }

    public PlayerFormBase getPreviousForm() {
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

    public void setCurrentForm(PlayerFormBase form) {
        this.previousForm = this.currentForm;
        this.currentForm = form;
    }

    public boolean isMoonEffectApplied() {
        return moonEffectApplied;
    }

    public void setMoonEffectApplied(boolean moonEffectApplied) {
        this.moonEffectApplied = moonEffectApplied;
    }

    public boolean isEndMoonEffectApplied() {
        return endMoonEffectApplied;
    }

    public void setEndMoonEffectApplied(boolean endMoonEffectApplied) {
        this.endMoonEffectApplied = endMoonEffectApplied;
    }

    public boolean isByCursedMoonEnd() {
        return isByCursedMoonEnd;
    }

    public void setByCursedMoonEnd(boolean byCursedMoonEnd) {
        isByCursedMoonEnd = byCursedMoonEnd;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(boolean firstJoin) {
        this.firstJoin = firstJoin;
    }
}
