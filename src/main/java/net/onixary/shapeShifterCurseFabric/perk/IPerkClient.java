package net.onixary.shapeShifterCurseFabric.perk;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.util.util.ISprite;
import org.jetbrains.annotations.NotNull;

public interface IPerkClient {
    public Identifier getID();

    public default ISprite getIcon() {
        return RegPerks.FALLBACK_PERK_ICON;
    }

    public default Text getName() {
        return IPerkClient.getDefaultName(this.getID());
    }

    public default Text getDesc() {
        return IPerkClient.getDefaultDesc(this.getID());
    }

    public static @NotNull Text getDefaultName(@NotNull Identifier perkName) {
        String NameSpace = perkName.getNamespace();
        String Path = perkName.getPath();
        return Text.translatable("ssc_perk." + NameSpace + "." + Path + ".name");
    }

    public static @NotNull Text getDefaultDesc(@NotNull Identifier perkName) {
        String NameSpace = perkName.getNamespace();
        String Path = perkName.getPath();
        return Text.translatable("ssc_perk." + NameSpace + "." + Path + ".desc");
    }
}
