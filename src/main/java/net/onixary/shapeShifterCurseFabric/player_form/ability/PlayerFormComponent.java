package net.onixary.shapeShifterCurseFabric.player_form.ability;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerForms;

public class PlayerFormComponent implements AutoSyncedComponent {
    private PlayerForms currentForm = PlayerForms.ORIGINAL_BEFORE_ENABLE;

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        // 读取状态枚举
        try {
            this.currentForm = PlayerForms.valueOf(nbtCompound.getString("currentForm"));
        } catch (IllegalArgumentException e) {
            this.currentForm = PlayerForms.ORIGINAL_BEFORE_ENABLE;
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putString("currentForm", currentForm.name());
    }

    public PlayerForms getCurrentForm() { return currentForm; }
    public void setCurrentForm(PlayerForms form) { this.currentForm = form; }
}
