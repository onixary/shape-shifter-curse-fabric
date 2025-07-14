package net.onixary.shapeShifterCurseFabric.player_form.skin;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;

public class PlayerSkinComponent implements Component, AutoSyncedComponent {
    private boolean keepOriginalSkin = false;

    public boolean shouldKeepOriginalSkin() {
        return keepOriginalSkin;
    }

    public void setKeepOriginalSkin(boolean keepOriginalSkin) {
        this.keepOriginalSkin = keepOriginalSkin;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        try {
        this.keepOriginalSkin = tag.getBoolean("KeepOriginalSkin");
        }
        catch(IllegalArgumentException e)
        {
            this.keepOriginalSkin = false; // Default to false
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("KeepOriginalSkin", this.keepOriginalSkin);
    }
}
